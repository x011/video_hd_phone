package com.moonclound.android.iptv.util;

import com.moon.android.iptv.arb.film.Configs;

public class HostUtil {

	
	/**多少次改变主机地址了*/
	public static int changeCount=0;
	/**
	 * 当切换主机,暂时提供三个主机
	 */
	public static void changeHost() {
		if(Configs.URL.HOST.equals(Configs.URL.HOST1)){
			Configs.URL.HOST=Configs.URL.HOST2;
		}else if(Configs.URL.HOST.equals(Configs.URL.HOST2)){
			Configs.URL.HOST=Configs.URL.HOST3;
		}else if(Configs.URL.HOST.equals(Configs.URL.HOST3)){
			Configs.URL.HOST=Configs.URL.HOST1;
		}
//		System.out.println("===================================================");
//		System.out.println("HOST="+Configs.URL.HOST);
//		Logger.getInstance().i("HOST="+Configs.URL.HOST);
		changeCount++;
	}
}
