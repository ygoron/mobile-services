/**
 * 
 */
package com.apos.model;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Yuri Goron
 * 
 */
@XmlRootElement(name = "SessionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class BILogonToken {

	@JsonProperty("logonToken")
	@XmlElement(name = "logonToken")
	private String cmsToken;

	/**
	 * @return the cmsToken
	 */
	public String getCmsToken() {
		return cmsToken;
	}

	/**
	 * @param cmsToken
	 *            the cmsToken to set
	 */
	public void setCmsToken(String cmsToken) {
		this.cmsToken = cmsToken;
	}
}
