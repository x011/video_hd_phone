package com.moon.android.iptv.arb.film;

import java.util.Timer;
import java.util.TimerTask;

import com.moonclound.android.iptv.util.Logger;
import com.moonclound.android.iptv.util.UpdateData;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MsgService extends Service{
	
	private Timer mMsgTimer = new Timer(true);
	public static final long MSG_WHEN_FIRST_GET = 2 * 1000;
	public static final long MSG_PERIOD = 600 * 1000;
	private Logger logger = Logger.getInstance();
	@Override
	public void onCreate() {
		super.onCreate();
		logger.i("get update and app message service create");
		mMsgTimer.schedule(mMsgTimeTask, MSG_WHEN_FIRST_GET, MSG_PERIOD);
		return;
	}
	
	private TimerTask mMsgTimeTask = new TimerTask(){
		@Override
		public void run() {
			String str = RequestDAO.getAppMessage();
			MyApplication.appMsg = str;
			if(!"".equals(str) && null != str){
				Intent intent = new Intent();
				intent.setAction(Configs.BroadCast.APP_GET_MSG);
				sendBroadcast(intent);
			}
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		try{
			mMsgTimer.cancel();
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

}
