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
package org.apache.fineract.gluu.authentication;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.gluu.http.GluuLoggingInterceptor;
import org.apache.fineract.gluu.jwk.JWT;
import org.apache.fineract.gluu.jwk.Jwks;
import org.apache.fineract.gluu.jwk.JwksProvider;
import org.apache.fineract.gluu.message.AccessInfo;
import org.apache.fineract.gluu.message.OpenIdConfig;
import org.apache.fineract.gluu.message.UserInfo;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service(value = "gluuService")
@Scope("singleton")
@Profile("gluu")
public class GluuService implements InitializingBean{

	private final static Logger logger = LoggerFactory.getLogger(GluuService.class);

	@Value("${gluu.openid_config_uri}")
	private volatile String configUri;
	@Value("${gluu.client_id}")
	private volatile String clientId ;
	@Value("${gluu.client_secret}")
	private volatile String clientSecret ;

	private volatile OpenIdConfig clientConfig;
	private volatile JwksProvider jwks;
	private final RestTemplate rest;

	/**
	 * Init service : get config from gluu
	 * @throws Exception
	 */
	public GluuService() throws Exception {

		rest = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));

		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new GluuLoggingInterceptor());
		rest.setInterceptors(interceptors);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		if(StringUtils.isNotEmpty(configUri))	{
			clientConfig = loadOpenIdConfig();
			jwks = loadJWKs();
			logger.debug("clientConfig : " + clientConfig);
		}
	}
	
	/**
	 * initially load openid config from gluu server
	 * @return
	 */
	private OpenIdConfig loadOpenIdConfig() {
		ResponseEntity<OpenIdConfig> response = rest.exchange(configUri, HttpMethod.GET, null, OpenIdConfig.class);

		Assert.isTrue(response.getStatusCode().value() == 200);

		return response.getBody();
	}

	/**
	 * 	initially load JSON WEb Key configuration from gluu server
	 * @return
	 * @throws Exception
	 */
	private JwksProvider loadJWKs() throws Exception {
		ResponseEntity<String> response = rest.exchange(clientConfig.getJwks_uri(), HttpMethod.GET, null, String.class);

		Assert.isTrue(response.getStatusCode().value() == 200);

		return new JwksProvider(response.getBody());
	}

	/**
	 * parse JSON Web Key and verify signature
	 * @param id_token
	 * @return
	 * @throws BadCredentialsException
	 */
	public JWT parseIdToken(String id_token) throws BadCredentialsException {
		try {
			JWT jwt = new JWT(id_token);
			Jwks jwk = jwks.get(jwt.getHeader().getKid());

			// verify key's algorithm and signature
			if (jwk.getAlgorithm().equals(jwt.getHeader().getAlg()) && jwt.verifiySignature(jwk.getPublicKey())) {
				return jwt;
			}
		} catch (Exception e) {
			logger.error("Could not parse id token", e);
			throw new BadCredentialsException("Invalid authentication token!");
		}

		throw new BadCredentialsException("Invalid authentication token!");
	}

	/**
	 * 
	 * @param access_code
	 * @param redirect_uri
	 * @param scope
	 * @return
	 * @throws BadCredentialsException
	 */
	public AccessInfo getAccessToken(String access_code, String redirect_uri, String scope) throws BadCredentialsException
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Authorization", "Basic " + Base64.encodeBase64String(new String(clientId + ":" + clientSecret).getBytes()) );
		
		MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
		map.add("grant_type", "authorization_code");
		map.add("code", access_code);
		map.add("redirect_uri", redirect_uri);
		map.add("scope", scope);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		ResponseEntity<AccessInfo> response = rest.postForEntity( clientConfig.getToken_endpoint(), request , AccessInfo.class );
		
		AccessInfo info =  response.getBody();
		
        return info;
	}
	
	
	/**
	 * get User info with the given access token.
	 * To ensure token misuse we cache it and dont allow using same token twice.
	 * @param access_token
	 * @return
	 */
	public UserInfo getUserInfo(String access_token) {

		logger.debug("GET user info for access_token : " + access_token);

		ResponseEntity<UserInfo> response = rest.exchange(
				clientConfig.getUserinfo_endpoint() + "?access_token=" + access_token, HttpMethod.GET, null,
				UserInfo.class);

		Assert.isTrue(response.getStatusCode().value() == 200);

		return response.getBody();
	}

	/**
	 * 
	 * @param id_token
	 */
	public void endSession(String id_token)
	{
		logger.debug("End session for id_token : " + id_token);
		
		rest.getForObject(clientConfig.getEnd_session_endpoint() + "?id_token_hint=" + id_token, String.class);
	}


	

	
}
