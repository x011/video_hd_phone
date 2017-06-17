package com.moon.android.model;


/**
 * Model--广告
 * @author Jervis
 *	
 */
public class Ad {
	
	/**
	 * 广告显示多少秒
	 */
	private String sec;
	
	private String adurl;
	
	public String getSec() {
		return sec;
	}
	public void setSec(String sec) {
		this.sec = sec;
	}
	public String getAdurl() {
		return adurl;
	}
	public void setAdurl(String adurl) {
		this.adurl = adurl;
	}
	
	@Override
	public String toString() {
		return "Ad [sec=" + sec + ", adurl=" + adurl + "]";
	}
	
}
