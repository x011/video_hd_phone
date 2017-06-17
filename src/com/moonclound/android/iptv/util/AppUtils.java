package com.moonclound.android.iptv.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class AppUtils {
	
	public static boolean isAppInstalled(Context context,String pkgName) {
		PackageManager pm = context.getPackageManager();
		boolean installed =false;
		try {
			pm.getPackageInfo(pkgName,PackageManager.GET_ACTIVITIES);
			installed =true;
		} catch(PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			installed =false;
		}
		return installed;
	}

}
