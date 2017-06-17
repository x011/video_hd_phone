package com.moonclound.android.iptv.util;

public class StringUtil {

	/**
	 * is null or is ""
	 * @param parameter
	 * @return
	 */
	public static boolean isBlank(String parameter){
		return (null == parameter) || ("".equals(parameter.trim()));
	}
	
	public static String deleteSpace(String s){
		return s.replace(" ", "");
	}
}
