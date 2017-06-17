package com.moon.android.moonplayer.service;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.iptv.arb.film.ProgramCache;
import com.moon.android.model.AuthInfo;
import com.moonclound.android.iptv.util.DbUtil;
import com.moonclound.android.iptv.util.HostUtil;
import com.moonclound.android.iptv.util.Logger;

public class AuthService {

	private Logger logger = Logger.getInstance();

	private Handler mAuthHandler;

	private AuthInfo mAuthInfo;

	private String mAuthCachePath = Configs.CachePath.AUTH;
	public DbUtil db;

	public AuthService(Handler handler) {
		mAuthHandler = handler;
		db = new DbUtil(MyApplication.getApplication());
	}

	public void initAuth() {
		// 如果缓存中有，就从缓存中获取，没有就从网络获取
		String AuthStr = db.GetAuth();
		if (AuthStr == null) {
			findFromNet(true);
		} else {
			try {
				mAuthInfo = new Gson().fromJson(AuthStr, AuthInfo.class);
				
				if (Configs.Code.AUTH_OK.equals(mAuthInfo.getCode())) {

					mAuthHandler.sendEmptyMessage(Configs.Success.AUTH_OK);

					initApplicationParam();
				} else {

					mAuthHandler.sendEmptyMessage(Configs.Failure.AUTH_WRONG);

				}

				findFromNet(false);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		// if(!ProgramCache.isExist(mAuthCachePath)){
		// logger.i("授权来自网络");
		// findFromNet(true);
		// }else{
		// try {
		// mAuthInfo=findFromCache();
		// logger.i("授权信息:"+mAuthInfo.toString());
		// //url=http://vodchina2.ibcde.net:9011/Api/Video/menu?appid=2003&mac=002157f3b51c]截取到Api/
		// // int index=mAuthInfo.getUrl().indexOf("9011");
		// // Configs.URL.HOST=mAuthInfo.getUrl().substring(0, index+9);
		// logger.i("从授权初始化主机地址Host="+Configs.URL.HOST);
		// mAuthHandler.sendEmptyMessage(Configs.Success.AUTH_OK);
		// initApplicationParam();
		// findFromNet(false);
		// } catch (Exception e) {
		// mAuthHandler.sendEmptyMessage(Configs.Failure.AUTH_WRONG);
		// logger.e(e.toString());
		// }
		// }
	}

	/**
	 * 把授权信息和防盗链字段添加到Application范围
	 */
	private void initApplicationParam() {
		MyApplication.authInfo = mAuthInfo;
		Configs.link = mAuthInfo.getLink();
	}

	public void findFromNet(final boolean flag) {
		FinalHttp finalHttp = new FinalHttp();
		logger.i("当前主机为=" + Configs.URL.HOST);
		logger.i("授权API=" + Configs.URL.getAuthApi());
//		System.out.println("URL = "+Configs.URL.getAuthApi());
		AjaxParams params=new AjaxParams();
		params.put("Model", android.os.Build.MODEL);
		finalHttp.post(Configs.URL.getAuthApi(), params,new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				try {
					mAuthInfo = new Gson().fromJson(t, AuthInfo.class);
					// 初始化全局播放授权的link标识值
					// saveAllToCache(t);
					db.SaveAuth(t);
					if (Configs.Code.AUTH_OK.equals(mAuthInfo.getCode())) {
						if (flag) {

							mAuthHandler
									.sendEmptyMessage(Configs.Success.AUTH_OK);

						}
						initApplicationParam();
					} else {
						if (flag) {
							mAuthHandler
									.sendEmptyMessage(Configs.Failure.AUTH_WRONG);
						}
					}
				} catch (JsonSyntaxException e) {
					if (flag) {
						mAuthHandler
								.sendEmptyMessage(Configs.Failure.AUTH_WRONG);
					}
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				logger.i("网络连接异常,strMsg=" + strMsg + "  errorNo=" + errorNo
						+ "  flag=" + flag);

				HostUtil.changeHost();

				if (HostUtil.changeCount == Configs.Other.HOST_CHANGE_TIME) {// 三个主机地址都有问题
					Message msg = new Message();
					msg.arg1 = errorNo;
					msg.what = Configs.Failure.NETWORK_EXCEPTION;
					mAuthHandler.sendMessage(msg);
					HostUtil.changeCount = 0;
				} else {
					findFromNet(flag);
				}
			}
		});
	}

	/**
	 * 等待mAuthHandler发送消息后才能执行这个方法
	 * 
	 * @return
	 */
	public AuthInfo getAuthInfo() {
		return mAuthInfo;
	}

	/**
	 * 把授权保存到缓存
	 * 
	 * @param authInfoGson
	 */
	public void saveAllToCache(String authInfoGson) {
		if (!ProgramCache.isDirectory(mAuthCachePath)) {
			if (!ProgramCache.checkIsSame(mAuthCachePath, authInfoGson)) {
				ProgramCache.delFile(mAuthCachePath);
				ProgramCache.save(mAuthCachePath, authInfoGson);
			}

		}
	}

	/**
	 * @return 缓存中的菜单列表
	 */
	public AuthInfo findFromCache() {
		logger.i("授权来自本地缓存");
		if (ProgramCache.isExist(mAuthCachePath)) {
			String gson = ProgramCache.getGsonString(mAuthCachePath);
			logger.i("本地缓存授权gson=" + gson);
			mAuthInfo = new Gson().fromJson(gson, AuthInfo.class);
		}
		return mAuthInfo;
	}
}
