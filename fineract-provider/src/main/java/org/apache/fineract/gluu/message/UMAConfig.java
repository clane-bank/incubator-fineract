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
public class UMAConfig {

	private String issuer; // ": "https://gluu.test.clane.com",
	private String authorization_endpoint; // "https://gluu.test.clane.com/oxauth/seam/resource/restv1/requester/perm",
	private String token_endpoint; 	// "https://gluu.test.clane.com/oxauth/seam/resource/restv1/oxauth/token",
	private String rpt_endpoint; // "https://gluu.test.clane.com/oxauth/seam/resource/restv1/requester/rpt",
	
	private String  introspection_endpoint; // "https://gluu.test.clane.com/oxauth/seam/resource/restv1/rpt/status",
	private String  resource_set_registration_endpoint; // : "https://gluu.test.clane.com/oxauth/seam/resource/restv1/host/rsrc/resource_set",
	private String  permission_registration_endpoint; //" : "https://gluu.test.clane.com/oxauth/seam/resource/restv1/host/rsrc_pr"
	private String  scope_endpoint; // : "https://gluu.test.clane.com/oxauth/seam/resource/restv1/uma/scopes",
	
	public UMAConfig()
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

	public String getRpt_endpoint() {
		return this.rpt_endpoint;
	}

	public void setRpt_endpoint(String rpt_endpoint) {
		this.rpt_endpoint = rpt_endpoint;
	}

	public String getIntrospection_endpoint() {
		return this.introspection_endpoint;
	}

	public void setIntrospection_endpoint(String introspection_endpoint) {
		this.introspection_endpoint = introspection_endpoint;
	}

	public String getResource_set_registration_endpoint() {
		return this.resource_set_registration_endpoint;
	}

	public void setResource_set_registration_endpoint(String resource_set_registration_endpoint) {
		this.resource_set_registration_endpoint = resource_set_registration_endpoint;
	}

	public String getPermission_registration_endpoint() {
		return this.permission_registration_endpoint;
	}

	public void setPermission_registration_endpoint(String permission_registration_endpoint) {
		this.permission_registration_endpoint = permission_registration_endpoint;
	}

	public String getScope_endpoint() {
		return this.scope_endpoint;
	}

	public void setScope_endpoint(String scope_endpoint) {
		this.scope_endpoint = scope_endpoint;
	}

	@Override
	public String toString() {
		return "UMAConfig [issuer=" + this.issuer + ", authorization_endpoint=" + this.authorization_endpoint
				+ ", token_endpoint=" + this.token_endpoint + ", rpt_endpoint=" + this.rpt_endpoint
				+ ", introspection_endpoint=" + this.introspection_endpoint + ", resource_set_registration_endpoint="
				+ this.resource_set_registration_endpoint + ", permission_registration_endpoint="
				+ this.permission_registration_endpoint + ", scope_endpoint=" + this.scope_endpoint + "]";
	}

	
	

}
