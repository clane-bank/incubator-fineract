/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.gluu.uma;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.gluu.http.CustomResponseHandler;
import org.apache.fineract.gluu.http.GluuLoggingInterceptor;
import org.apache.fineract.gluu.http.StatefulRestTemplate;
import org.apache.fineract.gluu.message.AATResponse;
import org.apache.fineract.gluu.message.GluuUser;
import org.apache.fineract.gluu.message.GluuUser.Name;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.gluu.message.RPTResponse;
import org.apache.fineract.gluu.message.RegisterResponse;
import org.apache.fineract.gluu.message.SearchResult;
import org.apache.fineract.gluu.message.UMAConfig;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Scope("singleton")
@Profile("gluu")
public class GluuUMAServiceImpl implements UmaService, InitializingBean{

	private final static Logger logger = LoggerFactory.getLogger(GluuUMAServiceImpl.class);
	
	@Value( "${gluu.uma_config_uri}" )
	private volatile String configUri;
	@Value( "${gluu.scim_user_endpoint}" )
	private volatile String userEndpoint;
	
	
	@Value( "${gluu.client_id}" )
	private volatile String clientId ;
	@Value( "${gluu.client_secret}" )
	private volatile String clientSecret ;

	private final StatefulRestTemplate statRest;
	private final RestTemplate rest;
	private volatile UMAConfig umaConfig;

	/**
	 * CRUD Gluu Users via UMA *
	 */
	public GluuUMAServiceImpl() {
		
		statRest  = new StatefulRestTemplate();
		statRest.setErrorHandler(new CustomResponseHandler());
		
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new GluuLoggingInterceptor());
		statRest.setInterceptors(interceptors);
		
		rest = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		rest.setInterceptors(interceptors);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	
		if(StringUtils.isNotEmpty(configUri))	{
		  umaConfig = loadUMAConfig();
		  logger.debug("umaConfig : " + umaConfig);
		}
	}
	
	/**
	 * initially load openid config from gluu server
	 * 
	 * @return
	 */
	private UMAConfig loadUMAConfig() {
		ResponseEntity<UMAConfig> response = rest.exchange(configUri, HttpMethod.GET, null, UMAConfig.class);

		Assert.isTrue(response.getStatusCode().value() == 200);

		return response.getBody();
	}
	
	/**
	 * get AAT 
	 * @return
	 */
	private ResponseEntity<AATResponse> requestAatToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Authorization",
				"Basic " + Base64.encodeBase64String(new String(clientId + ":" + clientSecret).getBytes()));

		LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "client_credentials");
		map.add("scope", "uma_authorization");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		ResponseEntity<AATResponse> response = statRest.exchange(umaConfig.getToken_endpoint(), HttpMethod.POST, request,
				AATResponse.class);

	   return response;
	}

	/**
	 * get inital RTP
	 * @param aatToken
	 * @return
	 */
	private ResponseEntity<RPTResponse> requestRptToken(String aatToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + aatToken);		
		headers.set("scope", "scim_access");
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		ResponseEntity<RPTResponse> response = statRest.exchange(umaConfig.getRpt_endpoint(), HttpMethod.POST,  new HttpEntity<>(headers),
				RPTResponse.class);

		return response;
	}

	/**
	 * register User endpoint
	 * @param rtpToken
	 * @return
	 */
	private ResponseEntity<RegisterResponse> registerUserEndpoint(String rtpToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + rtpToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		ResponseEntity<RegisterResponse> response = statRest.exchange(userEndpoint, HttpMethod.GET,  new HttpEntity<>(headers),
				RegisterResponse.class);
		
		return response;
	}

	/**
	 * get authorized RTP token
	 * @param aatToken
	 * @param ticketId
	 * @return
	 */
	private ResponseEntity<RPTResponse> requestAuthorizedRTP(String aatToken, String ticketId) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + aatToken);
		headers.set("scope", "scim_access");
		
		RegisterResponse res = new RegisterResponse();
		res.setTicket(ticketId);
		
		HttpEntity<RegisterResponse> request = new HttpEntity<>(res, headers);
		
		ResponseEntity<RPTResponse> response = statRest.exchange(umaConfig.getAuthorization_endpoint(), 
				HttpMethod.POST, request, RPTResponse.class);
		
		return response;
	}

	/**
	 * get RTP token
	 * includes all steps, to obtain a rtp token for user scim access.
	 * @return
	 */
	private synchronized String getRTPToken()
	{
		ResponseEntity<AATResponse> res = requestAatToken();
		String aatToken = res.getBody().getAccess_token();		
		ResponseEntity<RPTResponse> rtpRes = requestRptToken(aatToken);
		
		ResponseEntity<RegisterResponse> ticketRes = registerUserEndpoint(rtpRes.getBody().getRpt());
		
		String rtp = requestAuthorizedRTP(aatToken, ticketRes.getBody().getTicket()).getBody().getRpt();
		
		return rtp;
	}
	 
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	private ResponseEntity<SearchResult> requestUserByUsername(String rtpToken, String username)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + rtpToken);
		
		String query = "?count=1&filter=username eq \"" + username + "\"";
		
		ResponseEntity<SearchResult> response = rest.exchange(userEndpoint+ query, HttpMethod.GET,  new HttpEntity<>(headers),
				SearchResult.class);
		
		return response;
	}
	
	/**
	 * 
	 * @param rtpToken
	 * @param username
	 * @return
	 */
	private boolean checkUserNameExists(String rtpToken, String username)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + rtpToken);
		
		String query = "?filter=username eq \"" + username + "\"";
		
		ResponseEntity<SearchResult> response = rest.exchange(userEndpoint+ query, HttpMethod.GET,  new HttpEntity<>(headers),
				SearchResult.class);
		
		return response.getBody().getTotalResults() > 0;		
	}
	
	/**
	 * 
	 * @param rtpToken
	 * @param email
	 * @return
	 */
	private boolean checkEmailExists(String rtpToken, String email)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + rtpToken);
		
		String query = "?filter=mail eq \"" + email + "\"";
		
		ResponseEntity<SearchResult> response = rest.exchange(userEndpoint+ query, HttpMethod.GET,  new HttpEntity<>(headers),
				SearchResult.class);
		
		return response.getBody().getTotalResults() > 0;		
	}
	
	
	/**
	 * 
	 * @param unencodedPassword
	 * @param appUser
	 * @return
	 */
	private GluuUser createGluuUser(String unencodedPassword, AppUser appUser)
	{
	    String fullname = appUser.getFirstname() + " " + appUser.getLastname();
		GluuUser user = new GluuUser();
		
		user.setUserName(appUser.getUsername());
		user.setDisplayName(fullname);
		user.addEmail(appUser.getEmail());
		
		if(unencodedPassword != null)
		{
			user.setPassword(unencodedPassword);
		}
	
		Name name = new Name();
		name.setFormatted(fullname);
		name.setGivenName(appUser.getFirstname());
		name.setFamilyName(appUser.getLastname());		
		user.setName(name);
		
		user.setEmployeeNumber(appUser.getPassword());
		
		return user;
	}
	
	/**
	 * 
	 * @param unencodedPassword
	 * @param appUser
	 */
	@Override
	public void createUser(String unencodedPassword, AppUser appUser) {
		 
		GluuUser user = createGluuUser(unencodedPassword, appUser);
        String rtpToken = getRTPToken();
        
        if(checkUserNameExists(rtpToken, appUser.getUsername()))
        {
            final StringBuilder defaultMessageBuilder = new StringBuilder("User with username ").append(appUser.getUsername()).append(" already exists.");
            throw new PlatformDataIntegrityException("error.msg.user.duplicate.username", defaultMessageBuilder.toString(), "username", appUser.getUsername());
        	
        }
        if(checkEmailExists(rtpToken, appUser.getEmail()) )
        {
            final StringBuilder defaultMessageBuilder = new StringBuilder("User with email ").append(appUser.getEmail()).append(" already exists.");
            throw new PlatformDataIntegrityException("error.msg.user.duplicate.email", defaultMessageBuilder.toString(), "email", appUser.getEmail());
        }
        
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + rtpToken);
		
        HttpEntity<GluuUser> request = new HttpEntity<>(user, headers);
		
		rest.exchange(userEndpoint, HttpMethod.POST, request, String.class);
	}
	
	
	/**
	 * 
	 * @param unencodedPassword
	 * @param originalUsername
	 * @param appUser
	 */
	@Override
	public void updateUser(String unencodedPassword, String originalUsername, AppUser appUser) {
		 
		final GluuUser user = createGluuUser(unencodedPassword, appUser);
        final String rtpToken = getRTPToken();
		
        final String newUsername = appUser.getUsername();
        final String newEmail = appUser.getEmail();
        
        // if username changes check if the new one already exists
        if(!originalUsername.equals(newUsername) && checkUserNameExists(rtpToken, newUsername))
        {
            final StringBuilder defaultMessageBuilder = new StringBuilder("User with username ").append(newUsername).append(" already exists.");
            throw new PlatformDataIntegrityException("error.msg.user.duplicate.username", defaultMessageBuilder.toString(), "username", newUsername);
        }
        
        //get user gluu id
        ResponseEntity<SearchResult> res =  requestUserByUsername(rtpToken, originalUsername);
        final GluuUser gluuUser = res.getBody().getResources().get(0);
        final String oldEmail = gluuUser.getEmails().get(0).getValue();
        
        if(!oldEmail.equals(newEmail) && checkEmailExists(rtpToken, newEmail))
        {
            final StringBuilder defaultMessageBuilder = new StringBuilder("User with email ").append(newEmail).append(" already exists.");
            throw new PlatformDataIntegrityException("error.msg.user.duplicate.email", defaultMessageBuilder.toString(), "email", newEmail);
        }
        
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + rtpToken);
		
        HttpEntity<GluuUser> request = new HttpEntity<>(user, headers);
		
		rest.exchange(userEndpoint + "/" + gluuUser.getId(), HttpMethod.PUT, request, String.class);
	}
	
	/**
	 * 
	 * @param username
	 */
	@Override
	public void deleteUser(String username) {
		 
        final String rtpToken = getRTPToken();
		
        //get user gluu id
        ResponseEntity<SearchResult> res = 
        		requestUserByUsername(rtpToken, username);
        String id = res.getBody().getResources().get(0).getId();
  
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + rtpToken);
		
        HttpEntity<GluuUser> request = new HttpEntity<>(headers);
		
		rest.exchange(userEndpoint + "/" + id, HttpMethod.DELETE, request, String.class);
	}
	
	

}
