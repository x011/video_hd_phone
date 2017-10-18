package com.moon.android.moonplayer.service;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ev.player.util.DeviceFun;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.iptv.arb.film.ProgramCache;
import com.moon.android.model.AuthInfo;
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
		System.out.println("1====");
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

//				findFromNet(false);
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

	String urlTest = "";
	private void doTest(String urlTest) {
		// TODO Auto-generated method stub
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.get(urlTest, testCallBack);
	}
	private AjaxCallBack<Object> testCallBack = new AjaxCallBack<Object>() {
		public void onSuccess(Object t) {
			System.out.println("++++dotest success="+t.toString());
			
		};
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			System.out.println("++++dotest failed="+strMsg);
			
		};
	};
	public void findFromNet(final boolean flag) {
		System.out.println("2====");
		FinalHttp finalHttp = new FinalHttp();
		AjaxParams params=new AjaxParams();
		//old
//		params.put("appid", Configs.URL.APP_ID);
//		params.put("mac", Configs.URL.MAC);
//		params.put("ver", Tools.getVerName(mContext));
//		params.put("cpuid", DeviceFun.GetCpuId(MyApplication.iptvAppl1ication));
//		params.put("cpukey", DeviceFun.GetFileCpu());
//		params.put("Model", android.os.Build.MODEL);
		
		//new
		String ver = SecurityModule.encryptParam(Tools.getVerName(mContext));
		String cpuid = SecurityModule.encryptParam(DeviceFun.GetCpuId(MyApplication.iptvAppl1ication));
		String cpukey = SecurityModule.encryptParam(DeviceFun.GetFileCpu());
		String Model = SecurityModule.encryptParam(android.os.Build.MODEL);
		String appid = SecurityModule.encryptParam(Configs.URL.APP_ID);
		String mac = SecurityModule.encryptParam(Configs.URL.MAC);
		String key = SecurityModule.getKeyParam();
		System.out.println("mac = "+mac);
		System.out.println("cpuid = "+cpuid);
		System.out.println("cpukey = "+cpukey);
		System.out.println("appid = "+appid);
		System.out.println("Model = "+Model);
		System.out.println("ver = "+ver);
		System.out.println("key = "+key);
		params.put("ver", ver);
		params.put("cpuid", cpuid);
		params.put("cpukey", cpukey);
		params.put("Model", Model);
		params.put("appid", appid);
		params.put("mac", mac);
		params.put("key", key);
		
		urlTest = Configs.URL.getAuthApi()+"appid="+appid+"&mac="+mac+"&cpuid="+cpuid+"&cpukey="+cpukey+"&Model="+Model+"&ver="+ver+"&key="+key;
		System.out.println("authUrl = "+urlTest);
		
		finalHttp.post(Configs.URL.getAuthApi(), params,new AjaxCallBack<String>() {
//		finalHttp.post("http://192.168.31.220:9011/Secret/AppNew/Auth?", params,new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				try {
					System.out.println("------------auth success="+t.toString());
					mAuthInfo = new Gson().fromJson(t, AuthInfo.class);
					// 初始化全局播放授权的link标识值
					// saveAllToCache(t);
//					db.SaveAuth(t);
					if (Configs.Code.AUTH_OK.equals(mAuthInfo.getCode())) {
						//test code
						doTest(urlTest);
						//test end
						//鉴权成功，发送白名单
						doAddWhiteList(mAuthInfo.getToken());
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
				System.out.println("------------auth failed : "+strMsg);
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
	 * 添加白名单
	 */
	public void doAddWhiteList(String token) {
		// TODO Auto-generated method stub
		if(token == null)
			return;
		AjaxParams params = new AjaxParams();
		
		//old
//		params.put("appid", Configs.URL.APP_ID);
//		params.put("mac", Configs.URL.MAC);
//		params.put("cpuid", DeviceFun.GetCpuId(MyApplication.iptvAppl1ication));
//		params.put("cpukey", DeviceFun.GetFileCpu());
//		params.put("Model", android.os.Build.MODEL);
//		params.put("token", MD5Utils.getMd5Value(token+DeviceFun.GetCpuId(MyApplication.iptvAppl1ication)+DeviceFun.GetFileCpu()+"xxx"));
//		Configs.URL.HOST1 = "http://vodplus.etvhk.com/Api/";
		//new
		params.put("key", SecurityModule.getKeyParam());
		params.put("appid", SecurityModule.encryptParam(Configs.URL.APP_ID));
		params.put("mac", SecurityModule.encryptParam(Configs.URL.MAC));
		params.put("cpuid", SecurityModule.encryptParam(DeviceFun.GetCpuId(MyApplication.iptvAppl1ication)));
		params.put("cpukey", SecurityModule.encryptParam(DeviceFun.GetFileCpu()));
		params.put("Model", SecurityModule.encryptParam(android.os.Build.MODEL));
		params.put("token", MD5Utils.getMd5Value(token+DeviceFun.GetCpuId(MyApplication.iptvAppl1ication)+DeviceFun.GetFileCpu()+"xxx"));
		//print
//		System.out.println("key="+SecurityModule.getKeyParam());
//		System.out.println("appid="+SecurityModule.encryptParam(Configs.URL.APP_ID));
//		System.out.println("mac="+SecurityModule.encryptParam(Configs.URL.MAC));
//		System.out.println("cpuid before="+DeviceFun.GetCpuId(MyApplication.iptvAppl1ication));
//		System.out.println("cpukey before="+DeviceFun.GetFileCpu());
//		System.out.println("Model="+SecurityModule.encryptParam(android.os.Build.MODEL));
//		System.out.println("token="+MD5Utils.getMd5Value(token+DeviceFun.GetCpuId(MyApplication.iptvAppl1ication)+DeviceFun.GetFileCpu()+"xxx"));
		new AjaxUtil().post(Configs.URL.addWhiteListApi(), params, new ajaxPostCallback2());
	}
	
	class ajaxPostCallback2 implements AjaxUtil.PostCallback{

		@Override
		public void Success(String t) {
			// TODO Auto-generated method stub
//			System.out.println("success t.tostring="+t.toString());
			System.out.println("------------add whiteList success   "+t.toString());
//			Log.d("whitere",t.toString());
			if(t.toString().equals("0") || t.toString()=="0"){
				MyApplication.white="0";
				 
			}
		}

		@Override
		public void Failure(String host, int errorNo) {
			// TODO Auto-generated method stub
			System.out.println("------------add whiteList failed");
			logger.i("添加白名单失败"+"host="+host+" errorNo="+errorNo);
		}
		
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
