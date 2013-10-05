/**
 * 
 */
package com.apos.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Yuri Goron
 * 
 */
public class BILogonRequest {

	@JsonProperty("userName")
	private String cmsUser;

	@JsonProperty("password")
	private String cmsPassword;

	@JsonProperty("auth")
	private String authType;

	/**
	 * @return the cmsUser
	 */
	public String getCmsUser() {
		return cmsUser;
	}

	/**
	 * @param cmsUser
	 *            the cmsUser to set
	 */
	public void setCmsUser(String cmsUser) {
		this.cmsUser = cmsUser;
	}

	/**
	 * @return the cmsPassword
	 */
	public String getCmsPassword() {
		return cmsPassword;
	}

	/**
	 * @param cmsPassword
	 *            the cmsPassword to set
	 */
	public void setCmsPassword(String cmsPassword) {
		this.cmsPassword = cmsPassword;
	}

	/**
	 * @return the authType
	 */
	public String getAuthType() {
		return authType;
	}

	/**
	 * @param authType
	 *            the authType to set
	 */
	public void setAuthType(String authType) {
		this.authType = authType;
	}

}
