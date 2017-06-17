package com.moon.android.model;

import java.util.List;

public class Navigation {

	private String cid;
	public List<SeconMenu> submenu;
	private String name;
    public Navigation(String cid,String name){
    	this.cid=cid;
    	this.name=name;
    	
    }
  
	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Navigation [cid=" + cid + ", name=" + name + "]";
	}
}
