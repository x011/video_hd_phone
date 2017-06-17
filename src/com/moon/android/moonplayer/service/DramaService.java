package com.moon.android.moonplayer.service;

import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.model.Drama;
import com.moonclound.android.iptv.util.Logger;

public class DramaService {

	private Logger logger = Logger.getInstance();

	private List<Drama> mList;
	private Handler mDramaHandler;

	public DramaService(Handler handler) {
		mDramaHandler = handler;
	}

	/**
	 * 必须先于getList()调用
	 * 
	 * @param sid 剧集分类ID
	 */
	public void initList(final String sid) {
		
		String url = Configs.URL.getDramaApi()+sid;

		FinalHttp finalHttp = new FinalHttp();
		logger.i("secondary menu url=" + url);
		finalHttp.get(url, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String t) {
				mList = new Gson().fromJson(t, new TypeToken<List<Drama>>() {}.getType());
				logger.i(mList.toString());
				mDramaHandler.sendEmptyMessage(Configs.Success.GET_DRAMA_LIST);
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				initList(sid);
			}
		});
	}

	/**
	 * 只有当Handler发送消息后才能调用
	 * 
	 * @return
	 */
	public List<Drama> getList() {
		return mList;
	}
}
