package com.moon.android.iptv.arb.film;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;

import com.moonclound.android.iptv.util.AESSecurity;
import com.moonclound.android.iptv.util.Logger;
import com.ev.player.util.MACUtils;
import com.moonclound.android.iptv.util.MD5Util;

public class VodHistory {

	private Logger logger = Logger.getInstance();
	private static final String AUTHORITY = "com.moon.android.moonplayer.history.historyprovider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/history_table");
	private Context mContext;

	public VodHistory(Context context) {
		mContext = context;
	}

	/**
	 * @param subcatid
	 *            唯一的 id
	 * @return 0 index 1 play position
	 */
	public int[] getHistory(String subcatid) {
		ContentResolver resolver = mContext.getContentResolver();
		String contentUri = "content://" + AUTHORITY + "/history_table/"
				+ subcatid;
		logger.i(contentUri);
		Uri uri = Uri.parse(contentUri);
		Cursor cursor = resolver.query(uri, null, null, null, null);
		logger.i("cursor count = " + cursor.getCount());
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int playIndex = cursor.getColumnIndex("playIndex");
			int playPos = cursor.getColumnIndex("playPos");
			String playIndexS = cursor.getString(playIndex);
			String playPosS = cursor.getString(playPos);
			logger.i(playIndexS);
			logger.i(playPosS);
			try {
				int[] returnArray = new int[2];
				returnArray[0] = Integer.valueOf(playIndexS);
				returnArray[1] = Integer.valueOf(playPosS);
				return returnArray;
			} catch (Exception e) {
				logger.e(e.toString());
			}
		}
		cursor.close();
		return null;
	}

	/**
	 * link start
	 */
	public static final String LAST_LINK = "last_link";
	public static final String LAST_LINK_VALUE = "last_link_value";

	public static void saveLink(Context context, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				LAST_LINK, Context.MODE_PRIVATE);
		Editor addEditor = sharedPreferences.edit();
		addEditor.putString(LAST_LINK_VALUE, value);
		addEditor.commit();
	}

	public static String getLink(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				LAST_LINK, Context.MODE_PRIVATE);
		String value = sharedPreferences.getString(LAST_LINK_VALUE, null);
		return value;
	}

	/**
	 * link end
	 */

	/**
	 * key start
	 */
	public static final String LAST_KEY = "last_some";
	public static final String LAST_KEY_VALUE = "last_value";

	public static void saveKey(Context context, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				LAST_KEY, Context.MODE_PRIVATE);
		Editor addEditor = sharedPreferences.edit();
		addEditor.putString(LAST_KEY_VALUE, value);
		addEditor.commit();
	}

	public static String getKey(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				LAST_KEY, Context.MODE_PRIVATE);
		String value = sharedPreferences.getString(LAST_KEY_VALUE, null);
		return value;
	}

	/**
	 * key end
	 */

	/**
	 * 1001 验证通过 1002 验证不通过
	 * 
	 * @param context
	 * @return
	 */
	public static final long ONE_DAY = 24 * 60 * 60 * 1000;
	public static final long CACHE_TIME_OUT = ONE_DAY * 180;
	public static final String VOD_HISTORY_TIME = "vod_history2";
	public static final String PLAY_STATUS = "PLAY_STATUS";
	public static final String LAST_PLAY_TIME = "LAST_PLAY_TIME";
	public static final String CHECK_PASS = "1001";
	public static final String CHECK_NOT_PASS = "1002";

	public static boolean getAuthStatus(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				VOD_HISTORY_TIME, Context.MODE_PRIVATE);
		String status = sharedPreferences.getString(PLAY_STATUS, null);
		long time = sharedPreferences.getLong(LAST_PLAY_TIME, 0);
		long hasPassTime = Math.abs(System.currentTimeMillis() - time);
		if (null != status) {
			String statusCode = "null";
			statusCode = AESSecurity.decrypt(status,
					MD5Util.getStringMD5_16(MACUtils.getMac()));
			if (CHECK_PASS.equals(statusCode) && hasPassTime < CACHE_TIME_OUT)
				return true;
		}
		return false;
	}

	public static void saveCache(Context context, boolean playStatus) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				VOD_HISTORY_TIME, Context.MODE_PRIVATE);
		String checkStr = playStatus ? CHECK_PASS : CHECK_NOT_PASS;
		String storageStr = AESSecurity.encrypt(checkStr,
				MD5Util.getStringMD5_16(MACUtils.getMac()));
		Editor addEditor = sharedPreferences.edit();
		addEditor.putString(PLAY_STATUS, storageStr);
		addEditor.putLong(LAST_PLAY_TIME, System.currentTimeMillis());
		addEditor.commit();
	}

	public static long getCacheTime(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				LAST_KEY, Context.MODE_PRIVATE);
		long time = sharedPreferences.getLong(LAST_PLAY_TIME, 0);
		return time;
	}
}
