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

import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.authentication.BadCredentialsException;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.Map;

/**
 * Represents a JSON Web Key (JWK) used to verify the signature of JWTs
 */
public class Jwks {
    private static final String PUBLIC_KEY_ALGORITHM = "RSA";

    private final String id;
    private final String type;
    private final String algorithm;
    private final String usage;
    private final String operations;
    private final String certificateUrl;
    private final List<String> certificateChain;
    private final String certificateThumbprint;
    private final Map<String, Object> additionalAttributes;

    /**
     * Creates a new Jwk
     * @param id kid
     * @param type kyt
     * @param algorithm alg
     * @param usage use
     * @param operations key_ops
     * @param certificateUrl x5u
     * @param certificateChain x5c
     * @param certificateThumbprint x5t
     * @param additionalAttributes additional attributes not part of the standard ones
     */
    public Jwks(String id, String type, String algorithm, String usage, String operations, String certificateUrl, List<String> certificateChain, String certificateThumbprint, Map<String, Object> additionalAttributes) {
        this.id = id;
        this.type = type;
        this.algorithm = algorithm;
        this.usage = usage;
        this.operations = operations;
        this.certificateUrl = certificateUrl;
        this.certificateChain = certificateChain;
        this.certificateThumbprint = certificateThumbprint;
        this.additionalAttributes = additionalAttributes;
    }

    static Jwks fromValues(Map<String, Object> map) {
        Map<String, Object> values = Maps.newHashMap(map);
        String kid = (String) values.remove("kid");
        String kty = (String) values.remove("kty");
        String alg = (String) values.remove("alg");
        String use = (String) values.remove("use");
        String keyOps = (String) values.remove("key_ops");
        String x5u = (String) values.remove("x5u");
        @SuppressWarnings("unchecked")
        List<String> x5c = (List<String>) values.remove("x5c");
        String x5t = (String) values.remove("x5t");
        if (kid == null || kty == null || alg == null) {
            throw new IllegalArgumentException("Attributes " + map + " are not from a valid jwk");
        }
        return new Jwks(kid, kty, alg, use, keyOps, x5u, x5c, x5t, values);
    }

    
    public String getId() {
        return id;
    }

    
    public String getType() {
        return type;
    }

    
    public String getAlgorithm() {
        return algorithm;
    }

    
    public String getUsage() {
        return usage;
    }

    
    public String getOperations() {
        return operations;
    }

    
    public String getCertificateUrl() {
        return certificateUrl;
    }

    
    public List<String> getCertificateChain() {
        return certificateChain;
    }

    
    public String getCertificateThumbprint() {
        return certificateThumbprint;
    }

    public Map<String, Object> getAdditionalAttributes() {
        return additionalAttributes;
    }

    /**
     * Returns a {@link PublicKey} if the {@code 'alg'} is {@code 'RSA'}
     * @return a public key
     * @throws Exception if the key cannot be built or the key type is not RSA
     */
    
    public PublicKey getPublicKey() throws BadCredentialsException {
        if (!PUBLIC_KEY_ALGORITHM.equalsIgnoreCase(type)) {
            return null;
        }
        try {
            KeyFactory kf = KeyFactory.getInstance(PUBLIC_KEY_ALGORITHM);
            BigInteger modulus = new BigInteger(1, Base64.decodeBase64(stringValue("n")));
            BigInteger exponent = new BigInteger(1, Base64.decodeBase64(stringValue("e")));
            return kf.generatePublic(new RSAPublicKeySpec(modulus, exponent));
        } catch (InvalidKeySpecException e) {
            throw new BadCredentialsException("Invalid public key", e);
        } catch (NoSuchAlgorithmException e) {
            throw new BadCredentialsException("Invalid algorithm to generate key", e);
        }
    }

    private String stringValue(String key) {
        return (String) additionalAttributes.get(key);
    }

	@Override
	public String toString() {
		return "Jwk [id=" + this.id + ", type=" + this.type + ", algorithm=" + this.algorithm + ", usage=" + this.usage
				+ ", operations=" + this.operations + ", certificateUrl=" + this.certificateUrl + ", certificateChain="
				+ this.certificateChain + ", certificateThumbprint=" + this.certificateThumbprint
				+ ", additionalAttributes=" + this.additionalAttributes + "]";
	}


    
}