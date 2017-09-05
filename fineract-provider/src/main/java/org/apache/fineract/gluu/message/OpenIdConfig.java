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
package org.apache.fineract.gluu.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenIdConfig {

	private String issuer; // ": "https://gluu.test.clane.com",
	private String authorization_endpoint; // "https://gluu.test.clane.com/oxauth/seam/resource/restv1/oxauth/authorize",
	private String token_endpoint; 	// "https://gluu.test.clane.com/oxauth/seam/resource/restv1/oxauth/token",
	private String userinfo_endpoint; // "https://gluu.test.clane.com/oxauth/seam/resource/restv1/oxauth/userinfo",
	private String clientinfo_endpoint; // "https://gluu.test.clane.com/oxauth/seam/resource/restv1/oxauth/clientinfo",
	private String end_session_endpoint; // "https://gluu.test.clane.com/oxauth/seam/resource/restv1/oxauth/end_session",
	private String jwks_uri; // "https://gluu.test.clane.com/oxauth/seam/resource/restv1/oxauth/jwks",
	private String validate_token_endpoint; // "https://gluu.test.clane.com/oxauth/seam/resource/restv1/oxauth/validate",
	
	public OpenIdConfig()
	{
		
	}

	public String getIssuer() {
		return this.issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getAuthorization_endpoint() {
		return this.authorization_endpoint;
	}

	public void setAuthorization_endpoint(String authorization_endpoint) {
		this.authorization_endpoint = authorization_endpoint;
	}

	public String getToken_endpoint() {
		return this.token_endpoint;
	}

	public void setToken_endpoint(String token_endpoint) {
		this.token_endpoint = token_endpoint;
	}

	public String getUserinfo_endpoint() {
		return this.userinfo_endpoint;
	}

	public void setUserinfo_endpoint(String userinfo_endpoint) {
		this.userinfo_endpoint = userinfo_endpoint;
	}

	public String getClientinfo_endpoint() {
		return this.clientinfo_endpoint;
	}

	public void setClientinfo_endpoint(String clientinfo_endpoint) {
		this.clientinfo_endpoint = clientinfo_endpoint;
	}

	public String getEnd_session_endpoint() {
		return this.end_session_endpoint;
	}

	public void setEnd_session_endpoint(String end_session_endpoint) {
		this.end_session_endpoint = end_session_endpoint;
	}

	public String getJwks_uri() {
		return this.jwks_uri;
	}

	public void setJwks_uri(String jwks_uri) {
		this.jwks_uri = jwks_uri;
	}

	public String getValidate_token_endpoint() {
		return this.validate_token_endpoint;
	}

	public void setValidate_token_endpoint(String validate_token_endpoint) {
		this.validate_token_endpoint = validate_token_endpoint;
	}

	@Override
	public String toString() {
		return "OpenIdConfig [issuer=" + this.issuer + ", authorization_endpoint=" + this.authorization_endpoint
				+ ", token_endpoint=" + this.token_endpoint + ", userinfo_endpoint=" + this.userinfo_endpoint
				+ ", clientinfo_endpoint=" + this.clientinfo_endpoint + ", end_session_endpoint="
				+ this.end_session_endpoint + ", jwks_uri=" + this.jwks_uri + ", validate_token_endpoint="
				+ this.validate_token_endpoint + "]";
	}
	
	

}
