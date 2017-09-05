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
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author jzi
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GluuUser {

	private String id;
	private Meta meta = new Meta();
	private String[] schemas = new String[] { "urn:ietf:params:scim:schemas:extension:gluu:2.0:User",
			"urn:ietf:params:scim:schemas:core:2.0:User" };
	private String userName;
	private String displayName;
	private Name name;
	private boolean active = true;
	private String password;
	private List<Email> emails = new ArrayList<>();
	private GluuUserExtension extension = new GluuUserExtension();

	public GluuUser() {

	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Meta getMeta() {
		return this.meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public String[] getSchemas() {
		return this.schemas;
	}

	public void setSchemas(String[] schemas) {
		this.schemas = schemas;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Name getName() {
		return this.name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Email> getEmails() {
		return this.emails;
	}

	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}

	@JsonProperty("urn:ietf:params:scim:schemas:extension:gluu:2.0:User")
	public GluuUserExtension getGluuUser() {
		return this.extension;
	}

	@JsonProperty("urn:ietf:params:scim:schemas:extension:gluu:2.0:User")
	public void setGluuUser(GluuUserExtension gluuUser) {
		this.extension = gluuUser;
	}

	@JsonIgnore
	public void setEmployeeNumber(String employeeNumber) {
		extension.setEmployeeNumber(employeeNumber);
	}

	@JsonIgnore
	public void addEmail(String email) {
		emails.add(new Email(email));
	}

	/**
	 * 
	 * @author jzi
	 *
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static final class Email {
		private String value;
		private String display;
		private boolean primary = true;
		private String type = "other";

		public Email() {

		}

		public Email(String email) {
			this.value = email;
			this.display = email;
		}

		public String getValue() {
			return this.value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getDisplay() {
			return this.display;
		}

		public void setDisplay(String display) {
			this.display = display;
		}

		public boolean isPrimary() {
			return this.primary;
		}

		public void setPrimary(boolean primary) {
			this.primary = primary;
		}

		public String getType() {
			return this.type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return "Email [value=" + this.value + ", display=" + this.display + ", primary=" + this.primary + ", type="
					+ this.type + "]";
		}
		
		

	}

	/**
	 * 
	 * @author jzi
	 *
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static final class Name {
		private String formatted;
		private String familyName;
		private String givenName;

		public String getFormatted() {
			return this.formatted;
		}

		public void setFormatted(String formatted) {
			this.formatted = formatted;
		}

		public String getFamilyName() {
			return this.familyName;
		}

		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}

		public String getGivenName() {
			return this.givenName;
		}

		public void setGivenName(String givenName) {
			this.givenName = givenName;
		}

		@Override
		public String toString() {
			return "Name [formatted=" + this.formatted + ", familyName=" + this.familyName + ", givenName="
					+ this.givenName + "]";
		}

		
	}

	/**
	 * 
	 * @author jzi
	 *
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static final class Meta {
		private String resourceType = "User";
		private String created;
		private String lastModified;

		public String getCreated() {
			return this.created;
		}

		public void setCreated(String created) {
			this.created = created;
		}

		public String getLastModified() {
			return this.lastModified;
		}

		public void setLastModified(String lastModified) {
			this.lastModified = lastModified;
		}

		public String getResourceType() {
			return this.resourceType;
		}

		public void setResourceType(String resourceType) {
			this.resourceType = resourceType;
		}

		@Override
		public String toString() {
			return "Meta [resourceType=" + this.resourceType + ", created=" + this.created + ", lastModified="
					+ this.lastModified + "]";
		}
		
		
	}

	/**
	 * 
	 * @author jzi
	 *
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static final class GluuUserExtension {
		private String employeeType = "INTERNAL";
		private String employeeNumber;

		public String getEmployeeType() {
			return this.employeeType;
		}

		public void setEmployeeType(String employeeType) {
			this.employeeType = employeeType;
		}

		public String getEmployeeNumber() {
			return this.employeeNumber;
		}

		public void setEmployeeNumber(String employeeNumber) {
			this.employeeNumber = employeeNumber;
		}

		@Override
		public String toString() {
			return "GluuUserExtension [employeeType=" + this.employeeType + ", employeeNumber=" + this.employeeNumber
					+ "]";
		}

		
	}

	@Override
	public String toString() {
		return "GluuUser [id=" + this.id + ", meta=" + this.meta + ", schemas=" + Arrays.toString(this.schemas)
				+ ", userName=" + this.userName + ", displayName=" + this.displayName + ", name=" + this.name
				+ ", active=" + this.active + ", password=" + this.password + ", emails=" + this.emails + ", extension="
				+ this.extension + "]";
	}
	
	
}
