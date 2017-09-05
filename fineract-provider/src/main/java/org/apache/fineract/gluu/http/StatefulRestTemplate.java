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

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class StatefulRestTemplate extends RestTemplate {
	private final CloseableHttpClient httpClient;
	private final CookieStore cookieStore;
	private final HttpClientContext httpContext;
	private final StatefullClientHttpRequestFactory clientHttpRequestFactory;

	public StatefulRestTemplate() {
		super();
		
		cookieStore = new BasicCookieStore();
		httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
		
		httpContext = new HttpClientContext();
		httpContext.setCookieStore(cookieStore);
		clientHttpRequestFactory = new StatefullClientHttpRequestFactory(httpClient, httpContext);
		
		super.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	public HttpContext getHttpContext() {
		return httpContext;
	}

	public StatefullClientHttpRequestFactory getStatefulHttpClientRequestFactory() {
		return clientHttpRequestFactory;
	}
}


