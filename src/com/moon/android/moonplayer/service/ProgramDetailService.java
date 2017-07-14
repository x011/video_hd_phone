package com.moon.android.moonplayer.service;

import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.model.Drama;
import com.moon.android.model.VodProgramDetail;
import com.moonclound.android.iptv.util.DbUtil;
import com.moonclound.android.iptv.util.Logger;
import com.moonclound.android.iptv.util.StringUtil;

public class ProgramDetailService {

	private Logger logger = Logger.getInstance();
	private Handler mHandler;
	private VodProgramDetail vodProgramDetail;
	public DbUtil db;
	public String Sid;
	public ProgramDetailService(Handler handler) {
		super();
		db=new DbUtil(MyApplication.getApplication());
		mHandler = handler;
	}
	
	/**
	 * 必须先于getList()调用
	 * 
	 * @param cid
	 * @param seconMenu
	 */
	public void initList(String url,String Sid) {
		this.Sid=Sid;
		if(!dbCacheDetail()){
			FinalHttp finalHttp = new FinalHttp();
			AjaxParams params = new AjaxParams();
			params.put("appid", Configs.URL.APP_ID);
			params.put("mac", Configs.URL.MAC);
			params.put("sid", Sid);
			finalHttp.post(url,params,mLoadVodProgramDetail);
		}
	
		
	}
	public boolean dbCacheDetail() {
		String dbCache = db.GetVodDetail(Sid);
		if (dbCache != null) {
		
			try {
				List<VodProgramDetail> list=new Gson().fromJson(dbCache, new TypeToken<List<VodProgramDetail>>() {}.getType());
				vodProgramDetail=list.get(0);
				mHandler.sendEmptyMessage(Configs.Handler.PROGRAM_DETAIL_OK);
		        Log.d("ProgramDetail","详情启用缓存");
			} catch (Exception e) {
			
				return false;
			}
			return true;
		} else {
			Log.d("--", "无数据库缓存或查询出错");
			return false;
		}

	}
	private AjaxCallBack<Object> mLoadVodProgramDetail = new AjaxCallBack<Object>() {
		public void onSuccess(Object t) {
			super.onSuccess(t);
			logger.i((String)t);
			String vodStr = (String)t;
			if(StringUtil.isBlank(vodStr)){
				mHandler.sendEmptyMessage(Configs.CONTENT_IS_NULL);
				return;
			}
			String arrayJson = (String)t;
			if(null == arrayJson) {
				mHandler.sendEmptyMessage(Configs.Handler.PROGRAM_DETAIL_NULL);
				return;
			}
			try{
				logger.i("ProgramDetail json = " + arrayJson);
				List<VodProgramDetail> list=new Gson().fromJson(arrayJson, new TypeToken<List<VodProgramDetail>>() {}.getType());
				vodProgramDetail=list.get(0);
				mHandler.sendEmptyMessage(Configs.Handler.PROGRAM_DETAIL_OK);
				db.SaveVod(Sid, null, arrayJson);
			}catch(Exception e){
				logger.e(e.toString());
				mHandler.sendEmptyMessage(Configs.Handler.PROGRAM_DETAIL_PARSE_FAILURE);
			}
		};
		
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			mHandler.sendEmptyMessage(Configs.Handler.PROGRAM_DETAIL_PARSE_FAILURE);
		};
	};

	public VodProgramDetail getVodProgramDetail() {
		logger.i("VodProgramDetail:"+vodProgramDetail);
		return vodProgramDetail;
	}
	
}
