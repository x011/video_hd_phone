package com.moon.android.moonplayer.service;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.model.Ad;
import com.moonclound.android.iptv.util.Logger;

public class AdService {

	private Logger logger = Logger.getInstance();
	
	private Ad mAd;
	private Handler mAdHandler;
	
	
	public AdService(Handler adHandler) {
		super();
		this.mAdHandler = adHandler;
	}

	/**
	 * 必须先于getList()调用
	 * 
	 * @param sid 剧集分类ID
	 */
	public void initAd(final String cid) {
		
		String url = Configs.URL.getAdApi()+cid;

		FinalHttp finalHttp = new FinalHttp();
		logger.i("ad request url=" + url);
		finalHttp.get(url, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
//				mList = new Gson().fromJson(t, new TypeToken<List<Drama>>() {}.getType());
				logger.i("response ad json = "+t);
				if(!"[]".equals(t)){
					mAd=new Gson().fromJson(t, new TypeToken<Ad>(){}.getType());
				}
				logger.i("myAd="+mAd);
				mAdHandler.sendEmptyMessage(Configs.Success.GET_AD);
				
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mAdHandler.sendEmptyMessage(Configs.Success.GET_AD);
				//initAd(cid);
			}
		});
	}
	
	public Ad getAd() {
		return mAd;
	}
}
