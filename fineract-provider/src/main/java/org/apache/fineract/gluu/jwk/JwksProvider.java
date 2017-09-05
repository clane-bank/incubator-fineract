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


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;

/**
 * Jwk provider that loads them from a {@link URL}
 */
public class JwksProvider {
    
	final private Map<String, Object> jwkValues;
	
	public JwksProvider(String jsonString) throws BadCredentialsException
	{
	 try {
            final JsonFactory factory = new JsonFactory();
            final JsonParser parser = factory.createParser(jsonString);
            final TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
            jwkValues =  new ObjectMapper().reader().readValue(parser, typeReference);
        } catch (IOException e) 
        {
            throw new BadCredentialsException("Cannot parse jwks from string " + jsonString, e);
        }
	}

    private List<Jwks> getAll() throws BadCredentialsException {
        List<Jwks> jwks = Lists.newArrayList();
        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> keys = (List<Map<String, Object>>) jwkValues.get("keys");

        if (keys == null || keys.isEmpty()) {
            throw new BadCredentialsException("No keys found!");
        }

        try {
            for (Map<String, Object> values: keys) {
                jwks.add(Jwks.fromValues(values));
            }
        } catch(IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to parse jwk from json", e);
        }
        return jwks;
    }

    public Jwks get(String keyId) throws BadCredentialsException {
        final List<Jwks> jwks = getAll();
        for (Jwks jwk: jwks) {
            if (keyId.equals(jwk.getId())) {
                return jwk;
            }
        }
        throw new BadCredentialsException("No key found in  with key id " + keyId);
    }
}