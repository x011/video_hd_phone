package com.moon.android.iptv.arb.film;

import org.apache.http.HttpStatus;

public class StatusCodeMoon implements HttpStatus{
	
	public static int NETWORK_NOT_CONNECT = 601;
	public static int AUTH_FAIL = 701;
	public static int AUTH_STOP = 701;
	public static int CONTENT_IS_NULL = 801;
	
	
	
	
	
	public static String getStatusMsg(int statusCode, String promptMsg){
		return "[code:"+statusCode+"]  "+promptMsg;
	}
	
}
