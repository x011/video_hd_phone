package com.moonclound.android.iptv.util;

import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.model.KeyParam;

import android.os.Handler;
import android.widget.Toast;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class SecurityModule {

	private static final String KEYHOST = "http://vodplus.etvhk.com/Secret/AppNew/GetKey?"; //http://192.168.31.220:9011/Secret/AppNew/GetKey?
	public static String KeyGet = "1234567891234567";
	public static String KeyLocal = "1234567891234567";
	public static String KeyLocal2 = "1234567891234567";
	private static int figures = 6;// 随机数位数
	private static String appendStr = "666";// 秘钥追加字符
	public static final int KEY_SUCCESS = 1000;
	public static final int KEY_FAILED = 1001;
	private static int startNum = 0;
	private static int endNum = 10;

	public static void main(String[] args) {
		String keyParam = getRandomStr(figures);
		if (keyParam == null)
			keyParam = "0000000000000000";
		System.out.println("keyParam = " + keyParam);
		String val = replaceStr("FHG8888");
		System.out.println("val = " + val);
		System.out.println("123456".substring(0, 5));
		String vala = "aaa";
		StringBuilder sb = new StringBuilder(vala);
		sb.insert(1, "123");
		vala = sb.toString();
		System.out.println("vala = " + vala);
	}

	/**
	 * 随机一个数字，MD5加密
	 * 
	 * @param figures
	 *            位数
	 * @return
	 */
	public static String getRandomStr(int figures) {
		// TODO Auto-generated method stub
		String randomStr = (int) ((Math.random() * 9 + 1) / 10 * Math.pow(10, figures)) + "";
		randomStr = MD5Util.getStringMD5_16(randomStr);
		return randomStr;
	}

	public static String replaceStr(String value) {
		// TODO Auto-generated method stub
		HashMap<String, String> hash = new HashMap<String, String>();
		hash.put("0", "m");
		hash.put("1", "n");
		hash.put("2", "o");
		hash.put("3", "p");
		hash.put("4", "q");
		hash.put("5", "r");
		hash.put("6", "s");
		hash.put("7", "t");
		hash.put("8", "w");
		hash.put("9", "z");
		String DEXkye = "";
		for (int i = 0; i < value.length(); i++) {
			char item = value.charAt(i);
			if (hash.get(item + "") == null) {
				DEXkye += item + "";
			} else {
				DEXkye += hash.get(item + "");
			}
		}
		return DEXkye;
	}

	private static String keyToServer = "";
	private static Handler AESHandler;

	/**
	 * 从服务器获取秘钥
	 */
	public static void getKeyFromServer(Handler mHandler) {
		// TODO Auto-generated method stub
		startNum ++;
		AESHandler = mHandler;
		FinalHttp finalHttp = new FinalHttp();
		AjaxParams params = new AjaxParams();
		keyToServer = getRandomStr(9);
		System.out.println("keyToServer 0 = "+keyToServer);
		params.put("key", keyToServer);
		finalHttp.post(KEYHOST, params, getKeyCallBack);
	}

	private static AjaxCallBack<Object> getKeyCallBack = new AjaxCallBack<Object>() {
		public void onSuccess(Object t) {
			System.out.println("onSuccess t.tostring=" + t.toString());
			try {
				KeyParam keyParam = new Gson().fromJson(t.toString(), new TypeToken<KeyParam>() {
				}.getType());
				String content = keyParam.getKey();
				String password = MD5Util.getStringMD5_32(keyToServer + appendStr);
				System.out.println("content=" + content + " password=" + password);
				String val = AESSecurity.decrypt(content, password);
				System.out.println("aesval = " + val);
				KeyGet = val;

//				String key3 = (MD5Util.getStringMD5_32(KeyGet + getRandomStr(6))).substring(0, 8);
//				String key4 = replaceStr(key3);
//				KeyLocal = key3;
//				KeyLocal2 = key4;
//				System.out.println("keyvalue = " + KeyLocal);

				AESHandler.sendEmptyMessage(KEY_SUCCESS);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			System.out.println(startNum + " ================ onFailure strMsg=" + strMsg);
			if(startNum < endNum){
				if(AESHandler != null)
					getKeyFromServer(AESHandler);
			}else{
				AESHandler.sendEmptyMessage(KEY_FAILED);
			}
		};
	};

	/**
	 * 加密Param
	 * 
	 * @param param
	 * @return
	 */
	public static String encryptParam(String param) {
		// TODO Auto-generated method stub
		String key3 = (MD5Util.getStringMD5_32(KeyGet + getRandomStr(6))).substring(0, 8);
		String key4 = replaceStr(key3);
		KeyLocal = key3;
		KeyLocal2 = key4;
//		System.out.println("keyvalue = " + KeyLocal);
		
		String AESParam = AESSecurity.encrypt(param,
				MD5Util.getStringMD5_32(KeyLocal + KeyGet));
//		System.out.println("KeyLocal = " + KeyLocal);
//		System.out.println("KeyLocal2= " + KeyLocal2);
//		System.out.println("KeyGet = " + KeyGet);
//		System.out.println("AESParam = " + AESParam);
		StringBuilder sb = new StringBuilder(AESParam);
		sb.insert(1, KeyLocal2);
		AESParam = sb.toString();
//		System.out.println("AESmac = " + AESParam);
		return AESParam;
	}

	/**
	 * 获取key-param
	 * 
	 * @return
	 */
	public static String getKeyParam() {
		// TODO Auto-generated method stub
		System.out.println("keyToServer = "+keyToServer);
		return AESSecurity.encrypt(keyToServer, "1234567891234567");
	}
}