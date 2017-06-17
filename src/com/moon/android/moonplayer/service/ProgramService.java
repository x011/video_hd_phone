package com.moon.android.moonplayer.service;

import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.model.SeconMenu;
import com.moon.android.model.VodProgram;
import com.moonclound.android.iptv.util.HostUtil;
import com.moonclound.android.iptv.util.Logger;
import com.moonclound.android.iptv.util.ProgramCacheUtil;

public class ProgramService {

	private Logger logger = Logger.getInstance();

	private List<VodProgram> mList;
	private Handler mProgramHandler;

	public ProgramService(Handler handler) {
		mProgramHandler = handler;
	}

	/**
	 * 必须先于getList()调用
	 * 
	 * @param cid
	 * @param seconMenu
	 */
	public void initList(final String cid, final SeconMenu seconMenu) {
//		Log.d("--",seconMenu.getContent().size()+"");
		if(seconMenu.getContent()!=null){
			mList=seconMenu.getContent();
			mProgramHandler.sendEmptyMessage(Configs.Success.GET_PROGRAM_LIST);
		}else{
			
//			List<VodProgram> list=ProgramCacheUtil.getFromCache(cid, seconMenu.getClassify());
//			if(list==null){
//				findAllFromNet(cid, seconMenu);
//			}else{
//				mList=list;
//				mProgramHandler.sendEmptyMessage(Configs.Success.GET_PROGRAM_LIST);
//			}
			
		}
//		List<VodProgram> list=ProgramCacheUtil.getFromCache(cid, seconMenu.getClassify());
//		if(list==null){
//			findAllFromNet(cid, seconMenu);
//		}else{
//			mList=list;
//			mProgramHandler.sendEmptyMessage(Configs.Success.GET_PROGRAM_LIST);
//		}
		
	}

	public void findAllFromNet(final String cid, final SeconMenu seconMenu){
		
		String url = null;
		if (seconMenu.getType() != null) {
			url = Configs.URL.getProgramApi() + cid + "&" + seconMenu.getType() + "=" + seconMenu.getVal();
		}else{
			url = Configs.URL.getProgramApi() + cid;
		}
		
		FinalHttp finalHttp = new FinalHttp();
		logger.i("secondary menu url=" + url);
		finalHttp.get(url, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				try {
					mList = new Gson().fromJson(t,new TypeToken<List<VodProgram>>() {}.getType());
					ProgramCacheUtil.saveToCache(cid, seconMenu.getClassify(), mList);
					logger.i(mList.toString());
					mProgramHandler.sendEmptyMessage(Configs.Success.GET_PROGRAM_LIST);
				} catch (JsonSyntaxException e) {
					logger.i("节目列表Json解析异常");
					mProgramHandler.sendEmptyMessage(Configs.Failure.GET_PROGRAM_LIST);
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				logger.e("网络连接异常异常");
				super.onFailure(t, errorNo, strMsg);
				
				HostUtil.changeHost();
				
				if(HostUtil.changeCount==Configs.Other.HOST_CHANGE_TIME){
					mProgramHandler.sendEmptyMessage(Configs.Failure.GET_PROGRAM_LIST);
					HostUtil.changeCount=0;
				}else{
					findAllFromNet(cid, seconMenu);
				}
			}
		});
	}
	/**
	 * 只有当Handler发送消息后才能调用
	 * 
	 * @return
	 */
	public List<VodProgram> getList() {
		return mList;
	}
}
