package com.moonclound.android.iptv.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.model.VodProgram;

public class ProgramCacheUtil {

	public static List<VodProgram> getFromCache(String cid, String classify) {
		
		Map<String, List<VodProgram>> map = MyApplication.programCache.get(cid);
		List<VodProgram> programList = null;
		if (map != null) {
			programList = map.get(classify);
		}
		return programList;
	}

	public static void saveToCache(String cid, String classify,
			List<VodProgram> programList) {

		if (MyApplication.programCache.get(cid) == null) {
			Map<String, List<VodProgram>> map = new HashMap<String, List<VodProgram>>();
			map.put(classify, programList);
			MyApplication.programCache.put(cid, map);
		} else {
			if (MyApplication.programCache.get(cid).get(classify) == null) {
				MyApplication.programCache.get(cid).put(classify, programList);
			}
		}
	}
}
