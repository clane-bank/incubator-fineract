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

import java.io.IOException;
import java.security.PublicKey;
import java.security.Signature;
import org.apache.commons.codec.binary.Base64;

import org.springframework.security.authentication.BadCredentialsException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWT {

	public enum Algorithm {
		RS256("SHA256withRSA"), 
		RS384("SHA384withRSA"), 
		RS512("SHA512withRSA"), 
		HS256("HmacSHA256"),
		HS384("HmacSHA384"), 
		HS512("HmacSHA512"), 
		ES256("SHA256withECDSA"), 
		ES384("SHA384withECDSA"), 
		ES512("SHA512withECDSA");

		private final String name;

		Algorithm(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private JWTHeader header;
	private JWTBody body;
	private String content;
	private String signature;

	public JWT(String id_token) throws JsonParseException, JsonMappingException, IOException {
		String[] values = id_token.split("\\.");
		ObjectMapper mapper = new ObjectMapper();
		header = mapper.readValue(Base64.decodeBase64(values[0]), JWTHeader.class);
		body = mapper.readValue(Base64.decodeBase64(values[1]), JWTBody.class);

		content = values[0] + "." + values[1];
		signature = values[2];
	}

	public boolean verifiySignature(PublicKey publicKey) throws BadCredentialsException {
		try {
			final Signature sig = Signature.getInstance(Algorithm.valueOf(header.getAlg()).getName());
			sig.initVerify(publicKey);
			sig.update(content.getBytes());

			final byte[] bytes = Base64.decodeBase64(signature);

			return sig.verify(bytes);

		} catch (Exception e) {
			throw new BadCredentialsException("Invalid signature!", e);
		}
	}

	public JWTHeader getHeader() {
		return this.header;
	}

	public void setHeader(JWTHeader header) {
		this.header = header;
	}

	public JWTBody getBody() {
		return this.body;
	}

	public void setBody(JWTBody body) {
		this.body = body;
	}

	public String getSignature() {
		return this.signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

}
