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
package org.apache.fineract.gluu.http;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class StatefullClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {
	private final HttpContext httpContext;

	public StatefullClientHttpRequestFactory(HttpClient httpClient, HttpContext httpContext) {
		super(httpClient);
		this.httpContext = httpContext;
	}

	@SuppressWarnings("unused")
	@Override
	protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
		return this.httpContext;
	}
}
