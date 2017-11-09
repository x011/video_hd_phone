package com.moon.android.moonplayer.service;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ev.player.util.DeviceFun;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.iptv.arb.film.ProgramCache;
import com.moon.android.model.AuthInfo;
import com.moon.android.model.KeyParam;
import com.moonclound.android.iptv.util.AESSecurity;
import com.moonclound.android.iptv.util.AjaxUtil;
import com.moonclound.android.iptv.util.DbUtil;
import com.moonclound.android.iptv.util.HostUtil;
import com.moonclound.android.iptv.util.Logger;
import com.moonclound.android.iptv.util.MD5Util;
import com.moonclound.android.iptv.util.MD5Utils;
import com.moonclound.android.iptv.util.SecurityModule;
import com.moonclound.android.iptv.util.Tools;

public class AuthService {

	private Logger logger = Logger.getInstance();

	private Handler mAuthHandler;

	private AuthInfo mAuthInfo;

	private String mAuthCachePath = Configs.CachePath.AUTH;
	public DbUtil db;

	private Context mContext;
	public AuthService(Handler handler, Context mContext) {
		mAuthHandler = handler;
		this.mContext = mContext;
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
	}

	/**
	 * 把授权信息和防盗链字段添加到Application范围
	 */
	private void initApplicationParam() {
		MyApplication.authInfo = mAuthInfo;
		Configs.link = mAuthInfo.getLink();
	}

	private void AuthIp(final String token) {
		// TODO Auto-generated method stub
		FinalHttp fn=new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put("key", SecurityModule.getKeyParam());
		params.put("appid", SecurityModule.encryptParam(Configs.URL.APP_ID));
		params.put("mac", SecurityModule.encryptParam(Configs.URL.MAC));
		params.put("cpuid", SecurityModule.encryptParam(DeviceFun.GetCpuId(MyApplication.iptvAppl1ication)));
		params.put("cpukey", SecurityModule.encryptParam(DeviceFun.GetFileCpu()));
		params.put("Model", SecurityModule.encryptParam(android.os.Build.MODEL));
		params.put("token", MD5Utils.getMd5Value(token+DeviceFun.GetCpuId(MyApplication.iptvAppl1ication)+DeviceFun.GetFileCpu()+"xxx"));
		fn.post(currentHost+Configs.URL.addWhiteListApi(), params, new AjaxCallBack<Object>() {
			
			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				if(SecurityModule.IsDebug)
					System.out.println("stream -- "+t.toString());
			}
			 @Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				if(SecurityModule.IsDebug)
					System.out.println("stream -- " +Configs.URL.addWhiteListApi()+" -- "+strMsg);
				super.onFailure(t, errorNo, strMsg);
				startNum ++;
				if(startNum>=endNum){
					startNum = 0;
					Toast.makeText(mContext, "Pass003", Toast.LENGTH_LONG).show();
				}else{
					AuthIp(token);
				}
			}
		});
		
	}
	
	//设置getkey,auth,stream的唯一域名
	private String currentHost = Configs.URL.HOST;
	private int startNum = 0;
	private int endNum = 3;
	public void findFromNet(final boolean flag) {
		FinalHttp finalHttp = new FinalHttp();
		AjaxParams params = new AjaxParams();
		SecurityModule.keyToServer = SecurityModule.getRandomStr(9);
		params.put("key", SecurityModule.keyToServer);
		finalHttp.post(SecurityModule.keyHost + SecurityModule.getGetKey(), params, new AjaxCallBack<Object>() {
			public void onSuccess(Object t) {
				currentHost = SecurityModule.keyHost;
				Configs.URL.HOST = currentHost;
				if(SecurityModule.IsDebug)
					System.out.println("key -- " + t.toString()+"\n"+"currentHost -- "+currentHost);
				try {
					KeyParam keyParam = new Gson().fromJson(t.toString(), new TypeToken<KeyParam>() {
					}.getType());
					String content = keyParam.getKey();
					String password = MD5Util.getStringMD5_32(SecurityModule.keyToServer + SecurityModule.appendStr);
					String val = AESSecurity.decrypt(content, password);
					SecurityModule.KeyGet = val;
					doAuthFromNet(flag);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			};
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				if(SecurityModule.IsDebug)
					System.out.println("key -- "+SecurityModule.keyHost + SecurityModule.getGetKey() + " -- " + strMsg);
				if(SecurityModule.keyHost.equals(Configs.URL.HOST1)){
					SecurityModule.keyHost = Configs.URL.HOST2;
				}else if(SecurityModule.keyHost.equals(Configs.URL.HOST2)){
					SecurityModule.keyHost = Configs.URL.HOST3;
				}else if(SecurityModule.keyHost.equals(Configs.URL.HOST3)){
					Toast.makeText(mContext, "Pass002", Toast.LENGTH_LONG).show();
					return;
				}
				findFromNet(flag);
			};
		});
	}
	private void doAuthFromNet(final boolean flag) {
		// TODO Auto-generated method stub
		FinalHttp finalHttp = new FinalHttp();
		AjaxParams params=new AjaxParams();
		String ver = SecurityModule.encryptParam(Tools.getVerName(mContext));
		String cpuid = SecurityModule.encryptParam(DeviceFun.GetCpuId(MyApplication.iptvAppl1ication));
		String cpukey = SecurityModule.encryptParam(DeviceFun.GetFileCpu());
		String Model = SecurityModule.encryptParam(android.os.Build.MODEL);
		String appid = SecurityModule.encryptParam(Configs.URL.APP_ID);
		String mac = SecurityModule.encryptParam(Configs.URL.MAC);
		String key = SecurityModule.getKeyParam();
		params.put("ver", ver);
		params.put("cpuid", cpuid);
		params.put("cpukey", cpukey);
		params.put("model", Model);
		params.put("appid", appid);
		params.put("mac", mac);
		params.put("key", key);
		finalHttp.post(currentHost+Configs.URL.getAuthApi(), params,new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				if(SecurityModule.IsDebug)
					System.out.println("auth -- " + t.toString());
				try {
					mAuthInfo = new Gson().fromJson(t, AuthInfo.class);
					// 鍒濆鍖栧叏灞�鎾斁鎺堟潈鐨刲ink鏍囪瘑鍊�
					// saveAllToCache(t);
					if(mAuthInfo == null){
						if (flag) {
							mAuthHandler
									.sendEmptyMessage(Configs.Failure.AUTH_WRONG);
						}
						return;
					}
					db.SaveAuth(t);
					if (Configs.Code.AUTH_OK.equals(mAuthInfo.getCode())) {
						startNum = 0;//重设循环次数
						AuthIp(mAuthInfo.getToken());
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
				if(SecurityModule.IsDebug)
					System.out.println("auth -- " +Configs.URL.getAuthApi()+" -- "+strMsg);
				startNum ++;
				if(startNum>=endNum){
					Message msg = new Message();
					msg.arg1 = errorNo;
					msg.what = Configs.Failure.NETWORK_EXCEPTION;
					mAuthHandler.sendMessage(msg);
				}else{
					doAuthFromNet(flag);
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
