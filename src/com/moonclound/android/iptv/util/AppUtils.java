package com.moonclound.android.iptv.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

public class AppUtils {

	public static boolean isAppInstalled(Context context, String pkgName) {
		PackageManager pm = context.getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			installed = false;
		}
		return installed;
	}

	/**
	 * @param activity
	 * @return true为平板(可打电话)，false为机顶盒
	 */
	public static boolean isTablet4(Activity activity) {
		TelephonyManager telephony = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephony.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
			return true;
		} else {
			return false;
		}
	}
}
