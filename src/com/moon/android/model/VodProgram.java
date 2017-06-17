package com.moon.android.model;

import java.io.Serializable;

public class VodProgram implements Serializable {
	private static final long serialVersionUID = 1L;
	private String sid;
	private String name;
	private String logo;
	private String region;
	private String regisseur;
	private String status;
	private String key;
	private String h_logo;
	
	private String isdspwd = "0";//1为限制级

	public String getIsdspwd() {
		return isdspwd;
	}

	public void setIsdspwd(String isdspwd) {
		this.isdspwd = isdspwd;
	}

	public String getH_logo() {
		return h_logo;
	}

	public void setH_logo(String h_logo) {
		this.h_logo = h_logo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	/** 剧情 */
	private String drama;

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getRegisseur() {
		return regisseur;
	}

	public void setRegisseur(String regisseur) {
		this.regisseur = regisseur;
	}

	public String getDrama() {
		return drama;
	}

	public void setDrama(String drama) {
		this.drama = drama;
	}

	@Override
	public String toString() {
		return "VodProgram2 [sid=" + sid + ", name=" + name + "]\n";
	}
}
