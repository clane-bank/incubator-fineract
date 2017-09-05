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
public class UserInfo {

	private String user_name;
	private String name;
	private String id;
	private String sub;
	private String email;
	private String employee_number;
	private String employee_type;
	public String getUser_name() {
		return this.user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSub() {
		return this.sub;
	}
	public void setSub(String sub) {
		this.sub = sub;
	}
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmployee_number() {
		return this.employee_number;
	}
	public void setEmployee_number(String employee_number) {
		this.employee_number = employee_number;
	}
	public String getEmployee_type() {
		return this.employee_type;
	}
	public void setEmployee_type(String employee_type) {
		this.employee_type = employee_type;
	}
	@Override
	public String toString() {
		return "UserInfo [user_name=" + this.user_name + ", name=" + this.name + ", id=" + this.id + ", sub=" + this.sub
				+ ", email=" + this.email + ", employee_number=" + this.employee_number + ", employee_type="
				+ this.employee_type + "]";
	}
	
   
	
	
}
