package com.moon.android.model;

public class VodProgramDetail {

	private String logo;//VodProgram Exist this property,will not use
	private String showname;//VodProgram Exist this property,will not use
	private String year;
	private String drama;
	/**导演*/
	private String regisseur;
	/**主角*/
	private String protagonist;
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getShowname() {
		return showname;
	}
	public void setShowname(String showname) {
		this.showname = showname;
	}
	public String getRegisseur() {
		return regisseur;
	}
	public void setRegisseur(String regisseur) {
		this.regisseur = regisseur;
	}
	public String getProtagonist() {
		return protagonist;
	}
	public void setProtagonist(String protagonist) {
		this.protagonist = protagonist;
	}
	
	public String getDrama() {
		return drama;
	}
	public void setDrama(String drama) {
		this.drama = drama;
	}
	@Override
	public String toString() {
		return "VodProgramDetail [logo=" + logo + ", year=" + year + ", drama="
				+ drama + ", showname=" + showname + ", regisseur=" + regisseur
				+ ", protagonist=" + protagonist + "]";
	}
}
