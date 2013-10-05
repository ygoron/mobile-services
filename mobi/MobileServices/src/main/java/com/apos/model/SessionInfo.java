/**
 * 
 */
package com.apos.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yuri Goron
 * 
 */
@XmlRootElement(name = "SessionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class SessionInfo extends Response {

	private String mobileServiceVersion;
	private int biPlatformVersion;

	public int getBiPlatformVersion() {
		return biPlatformVersion;
	}

	public void setBiPlatformVersion(int biPlatformVersion) {
		this.biPlatformVersion = biPlatformVersion;
	}

	public String getMobileServiceVersion() {
		return mobileServiceVersion;
	}

	public void setMobileServiceVersion(String mobileServiceVersion) {
		this.mobileServiceVersion = mobileServiceVersion;
	}

}
