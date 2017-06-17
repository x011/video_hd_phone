package com.moon.android.model;

import java.util.List;

import android.view.SubMenu;

public class AllListModel {
         public String cid;
         public String name;
         public List<SeconMenu> submenu;
         
         public List<SeconMenu> getSubmenu() {
			return submenu;
		}
		public void setSubmenu(List<SeconMenu> submenu) {
			this.submenu = submenu;
		}
		public static class Submenu{
        	    public String classify;
        	    public String val;
        	    public String type;
        	    public List<Content> content;
        	    
        	    
        	    public String getClassify() {
					return classify;
				}


				public void setClassify(String classify) {
					this.classify = classify;
				}


				public String getVal() {
					return val;
				}


				public void setVal(String val) {
					this.val = val;
				}


				public String getType() {
					return type;
				}


				public void setType(String type) {
					this.type = type;
				}


				public List<Content> getContent() {
					return content;
				}


				public void setContent(List<Content> content) {
					this.content = content;
				}


				public static class Content{
        	    	public String sid;
        	    	public String name;
        	    	public String logo;
        	    	public String region;
        	    	public String regisseur;
        	    	public String status;
					public String getStatus() {
						return status;
					}
					public void setStatus(String status) {
						this.status = status;
					}
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
        	    	
        	    } 
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

         
}
