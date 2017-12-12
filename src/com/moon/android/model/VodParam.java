package com.moon.android.model;

public class VodParam {

	private String sid;
	private String key;
	private String isdspwd;

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIsdspwd() {
		return isdspwd;
	}

	public void setIsdspwd(String isdspwd) {
		this.isdspwd = isdspwd;
	}

	public VodParam(String sid, String key, String isdspwd){
		this.sid = sid;
		this.key = key;
		this.isdspwd = isdspwd;
	}
}
