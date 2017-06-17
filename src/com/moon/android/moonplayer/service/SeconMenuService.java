package com.moon.android.moonplayer.service;

import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.model.SeconMenu;
import com.moon.android.model.VodProgram;
import com.moonclound.android.iptv.util.HostUtil;
import com.moonclound.android.iptv.util.Logger;
import com.moonclound.android.iptv.util.SeconMenuCacheUtil;

public class SeconMenuService {

	private Logger logger=Logger.getInstance();
	
	private Handler mSeconMenuHandler;
	private List<SeconMenu>mList;
	
	public SeconMenuService(Handler handler){
		mSeconMenuHandler=handler;
	}

	/**
	 * 获取列表前必须初始化列表
	 */
	public void initList(String cid) {
		
		mList=ListCacheService.GetSecondMenuByCid(cid);
		if(mList==null){
//			Log.d("--","全局缓存不存在");
//			List<SeconMenu> seconMenuList = SeconMenuCacheUtil.getFromCache(cid);
//			if(seconMenuList==null){
//				findFromNet(cid);
//			}else{
//				mList=seconMenuList;
//				mSeconMenuHandler.sendEmptyMessage(Configs.Success.GET_SECON_MENU);
//			}
		}else{
			mSeconMenuHandler.sendEmptyMessage(Configs.Success.GET_SECON_MENU);
		}

		
	}
	
	public void findFromNet(final String cid){
		FinalHttp finalHttp=new FinalHttp();
//		String url=Configs.URL.API_SECON_MENU+mCid;
		String url=Configs.URL.getSeconMenuApi()+cid;
		logger.i("secondary menu url="+url);
		finalHttp.get(url, new AjaxCallBack<String>(){
			@Override
			public void onSuccess(String t) {
				try {
					mList = new Gson().fromJson(t,  new TypeToken<List<SeconMenu>>() { }.getType());
					SeconMenuCacheUtil.saveToCache(cid, mList);
					logger.i(mList.toString());
					mSeconMenuHandler.sendEmptyMessage(Configs.Success.GET_SECON_MENU);
				} catch (JsonSyntaxException e) {
					mSeconMenuHandler.sendEmptyMessage(Configs.Failure.GET_SECON_MENU);
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				HostUtil.changeHost();
				
				if(HostUtil.changeCount==Configs.Other.HOST_CHANGE_TIME){//三个主机地址都有问题
					mSeconMenuHandler.sendEmptyMessage(Configs.Failure.GET_SECON_MENU);
					HostUtil.changeCount=0;
				}else{
					findFromNet(cid);
				}
				
			}
		});
	}

	/**
	 * 该方法必须在Handler发送消息后执行，否则可能为null
	 * @return 二级菜单列表
	 */
	public List<SeconMenu> getList() {
		return mList;
	}
	
	public Map<String, VodProgram> getAllProMap(){
		return ListCacheService.getAllProgramMap();
	}
}
