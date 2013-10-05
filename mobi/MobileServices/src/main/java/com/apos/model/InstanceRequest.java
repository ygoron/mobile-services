/**
 * 
 */
package com.apos.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ygoron
 * 
 */

@XmlRootElement(name = "InstanceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class InstanceRequest {

	private String cuid;
	private int id;

	public String getCuid() {
		return cuid;
	}

	public void setCuid(String cuid) {
		this.cuid = cuid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
