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
package org.apache.fineract.gluu.jwk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//{"iss":"https://gluu.test.clane.com","aud":"@!1EB3.44FF.24F8.FA1A!0001!6E69.93E1!0008!CCEC.34F7.DDDE.9044",
//"exp":1490882778,"iat":1490879178,"acr":"auth_ldap_server","amr":"[]","nonce":"1qqjr4w",
//"auth_time":1490879176,
//"at_hash":"2iGxuWcOl-lQvA1vu_2TUA","oxValidationURI":"https://gluu.test.clane.com/oxauth/opiframe","oxOpenIDConnectVersion":"openidconnect-1.0","sub":"@!1EB3.44FF.24F8.FA1A!0001!6E69.93E1!0000!67FD.BBD4.64B6.645B"}
@JsonIgnoreProperties(ignoreUnknown = true)
public class JWTBody {
	private String sub;
	private String iss;
	private String aud;
	private String acr;
	private String nonce;
	private String at_hash;

	private long exp;
	private long iat;
	private long auth_time;

	public String getSub() {
		return this.sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getIss() {
		return this.iss;
	}

	public void setIss(String iss) {
		this.iss = iss;
	}

	public String getAud() {
		return this.aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getAcr() {
		return this.acr;
	}

	public void setAcr(String acr) {
		this.acr = acr;
	}

	public String getNonce() {
		return this.nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getAt_hash() {
		return this.at_hash;
	}

	public void setAt_hash(String at_hash) {
		this.at_hash = at_hash;
	}

	public long getExp() {
		return this.exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public long getIat() {
		return this.iat;
	}

	public void setIat(long iat) {
		this.iat = iat;
	}

	public long getAuth_time() {
		return this.auth_time;
	}

	public void setAuth_time(long auth_time) {
		this.auth_time = auth_time;
	}

}