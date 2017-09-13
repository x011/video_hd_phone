package com.moon.android.moonplayer.service;

import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.annotation.SuppressLint;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.iptv.arb.film.ProgramCache;
import com.moon.android.model.AuthInfo;
import com.moon.android.model.Navigation;
import com.moonclound.android.iptv.util.HostUtil;
import com.moonclound.android.iptv.util.Logger;
import com.ev.player.util.MACUtils;

@SuppressLint("HandlerLeak")
public class NavigationService {

	private Logger logger=Logger.getInstance();
	private List<Navigation>mList;
	
	private Handler mLeftMenuHandler;
	
	private String mLeftMenuCachePath=Configs.CachePath.LEFT_MENU;
	
	public NavigationService(Handler handler){
		mLeftMenuHandler=handler;
	}
	
	public void initList(final AuthInfo authInfo) {
		mList=ListCacheService.GetNavigation();
		if(mList==null){
			//findAllFromNet(authInfo,true);
		}else{
			logger.i("左侧菜单来自本地缓存");
			mLeftMenuHandler.sendEmptyMessage(Configs.Success.GET_LEFT_MENU);
		}
//		if(!ProgramCache.isExist(mLeftMenuCachePath)){
//			findAllFromNet(authInfo,true);
//		}else{
//			logger.i("左侧菜单来自本地缓存");
//			try {
//				mList=findAllFromCache();
//				mLeftMenuHandler.sendEmptyMessage(Configs.Success.GET_LEFT_MENU);
//			} catch (Exception e) {
//				logger.i("左侧菜单本地数据解析异常");
//				mLeftMenuHandler.sendEmptyMessage(Configs.Failure.GET_CACHE_LEFT_MENU);
//				e.printStackTrace();
//			}
//			findAllFromNet(authInfo,false);//再次检测列表是否改变，当改变后，下次进入应用就能刷新列表
//		}
	}

	/**
	 * @return 缓存中的菜单列表
	 * @param flag 是否把消息发送到外面去更新界面
	 */
	public void findAllFromNet(final AuthInfo authInfo,final boolean flag){
		logger.i("左侧菜单来自网络");
		FinalHttp finalHttp=new FinalHttp();
		finalHttp.get(Configs.URL.getLeftMenuApi(), new AjaxCallBack<String>(){
			@Override
			public void onSuccess(String t) {
				try {
					mList = new Gson().fromJson(t,  new TypeToken<List<Navigation>>() { }.getType());
					logger.i(mList.toString());
					
					saveAllToCache(t);//保存到缓存
					if(flag){
						mLeftMenuHandler.sendEmptyMessage(Configs.Success.GET_LEFT_MENU);
					}
				} catch (JsonSyntaxException e) {
					logger.e("左侧菜单网络数据解析异常");
					if(flag){
						mLeftMenuHandler.sendEmptyMessage(Configs.Failure.GSON_WRONG);
					}
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				logger.e("获取左侧菜单网络出错errorNo="+errorNo+"  strMsg="+strMsg);
				
				HostUtil.changeHost();
				if(HostUtil.changeCount==Configs.Other.HOST_CHANGE_TIME){//三个主机地址都有问题
					mLeftMenuHandler.sendEmptyMessage(Configs.Failure.GET_LEFT_MENU_NET_WRONG);
				}else{
					findAllFromNet(authInfo, flag);
				}
			}
		});
	}
	
	public List<Navigation> findAll(){
		return mList;
	}
	
	/**
	 * 把菜单列表保存到缓存
	 * @param json 
	 */
	public void saveAllToCache(String json){
		if (!ProgramCache.isDirectory(mLeftMenuCachePath)) {
			if (!ProgramCache.checkIsSame(mLeftMenuCachePath,json)) {
				ProgramCache.delFile(mLeftMenuCachePath);
				ProgramCache.save(mLeftMenuCachePath,json);
			}
		}
	}
	
	/**
	 * @return 缓存中的菜单列表
	 */
	private List<Navigation> findAllFromCache(){
		if (ProgramCache.isExist(mLeftMenuCachePath)) {
			String gson = ProgramCache.getGsonString(mLeftMenuCachePath);
			logger.i("Cache leftMenu Gson="+gson+"  MAC="+MACUtils.getMac());
			mList = new Gson().fromJson(gson, new TypeToken<List<Navigation>>() { }.getType());
		}
		return mList;
	}
	
	/**
	 * @return 鉴定缓存与网络数据一致性。false:不一致，true:一致
	 */
	public boolean validateCache(){
		
		return false;
	}
}
