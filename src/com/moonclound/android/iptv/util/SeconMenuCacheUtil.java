package com.moonclound.android.iptv.util;

import java.util.List;

import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.model.SeconMenu;

public class SeconMenuCacheUtil {

	public static List<SeconMenu> getFromCache(String cid){
		return MyApplication.seconMenuCache.get(cid);
	}
	
	public static void saveToCache(String cid,List<SeconMenu>seconMenuList){
		List<SeconMenu> list = MyApplication.seconMenuCache.get(cid);
		
		if(list==null){
			MyApplication.seconMenuCache.put(cid, seconMenuList);
		}
	}
}
