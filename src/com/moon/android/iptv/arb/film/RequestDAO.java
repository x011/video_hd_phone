package com.moon.android.iptv.arb.film;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moonclound.android.iptv.util.AppMessageData;
import com.moonclound.android.iptv.util.Logger;
import com.moonclound.android.iptv.util.RequestUtil;
import com.moonclound.android.iptv.util.Tools;
import com.moonclound.android.iptv.util.UpdateData;

public class RequestDAO {
	
	private static Logger logger = Logger.getInstance();
	private static RequestDAO reuqestDAO;
	private RequestDAO(){}
	
	public static RequestDAO getInstance(){
		new RequestDAOInstance();
		return reuqestDAO;
	}
	
	private static class RequestDAOInstance{
		public RequestDAOInstance(){
			reuqestDAO = new RequestDAO();
		}
	}
	
	public static UpdateData checkUpate(Context context) {
		try {
			String str1 = Configs.URL.getAppUpdateApi()+"&version=" + Tools.getVerCode(context);
			String str2 = RequestUtil.getInstance().request(str1);
			Log.d("updataHttpre:",str2);
			if (isBlank(str2))
				return null;
			UpdateData localUpdateData = (UpdateData) new Gson().fromJson(str2,
					new TypeToken<UpdateData>() {
					}.getType());
			if ((localUpdateData != null) && (!localUpdateData.equals(""))) {
				if ((localUpdateData.getCode() != null) && (!localUpdateData.getCode().equals(""))) {
					return localUpdateData;
				}
				return null;
			}
		} catch (Exception e) {
			logger.e(e.toString());
			return null;
		}
		return null;
	}
	
	public static String getAppMessage() {
		try {
			String url = Configs.URL.getAppMsgApi();
			logger.i("AppMessage API = "+url);
			String json = RequestUtil.getInstance().request(url);
			boolean bool = isBlank(json);
			if (bool)
				return "";
			String msg = ((AppMessageData) new Gson().fromJson(json,
					new TypeToken<AppMessageData>() {
					}.getType())).getMsg();
			logger.i("push msg : "+msg);
			return msg;
		} catch (Exception e) {
			logger.e(e.toString());
		}
		return "";
	}
	
	
	private static boolean isBlank(String paramString) {
		return (paramString == null) || (paramString.trim().equals(""));
	}
}
