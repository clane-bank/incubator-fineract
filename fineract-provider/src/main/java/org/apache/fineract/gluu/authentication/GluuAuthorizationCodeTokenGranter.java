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

import org.apache.fineract.gluu.message.AccessInfo;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class GluuAuthorizationCodeTokenGranter extends AbstractTokenGranter {

	private final GluuAuthenticationProvider authenticationProvider;

	public static final String GRANT_TYPE = "gluu";
	
	public GluuAuthorizationCodeTokenGranter(
			AuthorizationServerTokenServices tokenServices,
			ClientDetailsService clientDetailsService,
			OAuth2RequestFactory requestFactory, 
			GluuAuthenticationProvider authenticationProvider) {

		super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
		this.authenticationProvider = authenticationProvider;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

		System.out.println("GET OAUTH2 AUTHENTICATION");
		logger.warn("GET OAUTH2 AUTHENTICATION");
	
		// get access token from gluu server
		AccessInfo gluuAccess = authenticationProvider.getGluuService().getAccessToken(
				tokenRequest.getRequestParameters().get("access_code"),
				tokenRequest.getRequestParameters().get("redirect_uri"),
				tokenRequest.getRequestParameters().get("gluu_scope"));

		Authentication userAuth;
		try {
			userAuth = authenticationProvider.authenticate(
					new PreAuthenticatedAuthenticationToken(gluuAccess.getId_token(), gluuAccess.getAccess_token()));
		} catch (AccountStatusException ase) {
			throw new InvalidGrantException(ase.getMessage());
		} catch (AuthenticationException e) {
			throw new InvalidGrantException(e.getMessage());
		}

		if (userAuth == null || !userAuth.isAuthenticated()) {
			throw new InvalidGrantException("Could not authenticate on gluu service");
		}

		OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);

		logger.warn("storedOAuth2Request : " + storedOAuth2Request);
		
		return new OAuth2Authentication(storedOAuth2Request, userAuth);
	}

}
