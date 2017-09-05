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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {
	private int totalResults;
	private int itemsPerPage;
	private int startIndex;
	private List<GluuUser> Resources = new ArrayList<>();;

	public int getTotalResults() {
		return this.totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	public int getItemsPerPage() {
		return this.itemsPerPage;
	}

	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	public int getStartIndex() {
		return this.startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	@JsonProperty("Resources")
	public List<GluuUser> getResources() {
		return this.Resources;
	}

	@JsonProperty("Resources")
	public void setResources(List<GluuUser> resources) {
		this.Resources = resources;
	}

	@Override
	public String toString() {
		return "SearchResult [totalResults=" + this.totalResults + ", itemsPerPage=" + this.itemsPerPage
				+ ", startIndex=" + this.startIndex + ", Resources=" + this.Resources + "]";
	}

	
}
