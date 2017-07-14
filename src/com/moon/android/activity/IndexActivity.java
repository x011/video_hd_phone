package com.moon.android.activity;

import java.io.File;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ev.android.evodshd.plus.R;
import com.forcetech.android.ForceTV;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.iptv.arb.film.StatusCodeMoon;
import com.moon.android.moonplayer.service.AuthService;
import com.moon.android.moonplayer.service.ListCacheService;
import com.moonclound.android.iptv.util.ActivityUtils;
import com.moonclound.android.iptv.util.Logger;
import com.moonclound.android.iptv.util.NetworkUtil;

public class IndexActivity extends Activity {
	private ImageView mImageLoad;
	private Logger logger = Logger.getInstance();

	public LinearLayout mContainerLoad;
	
	private LinearLayout mContainerError;
	private Button mBtnReload, mBtnCancel;
	public TextView mTextPrompt;
  
	private String mCachePah = Configs.CONTENT_CACHE_FILE;
    
	private AuthService mAuthService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new Thread(){
			@Override
			public void run() {
				new ForceTV().initForceClient();//not execute this,couldn't play video,keeping on loading video state
			}
		}.start();
		setContentView(R.layout.welcome_activity);
		initView();

		AnimationDrawable animationDrawable = (AnimationDrawable) mImageLoad.getDrawable();
		animationDrawable.start();
		checkNetwork();
	}

	private void initView() {
		mImageLoad = (ImageView) findViewById(R.id.welcome_image);
		mImageLoad.setImageResource(R.anim.load_animation);

		mContainerLoad = (LinearLayout) findViewById(R.id.load_container);
		mContainerError = (LinearLayout) findViewById(R.id.error_container);
		mBtnReload = (Button) findViewById(R.id.reload);
		mBtnCancel = (Button) findViewById(R.id.cancel);
		mTextPrompt = (TextView) findViewById(R.id.text_load_prompt);

		mBtnReload.setOnClickListener(mErrBtnClickListener);
		mBtnCancel.setOnClickListener(mErrBtnClickListener);
	}
	 public boolean deleteFile(String filePath) {
		    File file = new File(filePath);
		        if (file.isFile() && file.exists()) {
		        return file.delete();
		        }
		        return false;
	 }
	private OnClickListener mErrBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == mBtnReload) {
			//	deleteFile(Configs.CachePath.AUTH);
				showLoadContainer(true);
				mAuthService.findFromNet(true);
				//checkNetwork();
			} else if (v == mBtnCancel) {
				finish();
			}
		}
	};

	private void checkNetwork() {
		logger.i("network connect");
		if (!NetworkUtil.isConnectingToInternet(this))
			mHandler.sendEmptyMessage(Configs.NETWORK_NOT_CONNECT);
		else
			mHandler.sendEmptyMessage(Configs.NETWORK_CONNECT);
	}
    public void test(){
    	Log.d("test","ssssssss");
    }
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Configs.NETWORK_NOT_CONNECT:
				showDilog(StatusCodeMoon.getStatusMsg(
						StatusCodeMoon.NETWORK_NOT_CONNECT,
						getString(R.string.network_not_connect)));
				break;
			case Configs.NETWORK_CONNECT:
				logger.i("联网状 态开始");
//				new ListCacheService(mHandler);
				mAuthService = new AuthService(mHandler);//联网成功就获取授权
				mAuthService.initAuth();
				
				break;
			case Configs.Success.AUTH_OK:
				MyApplication.authInfo=mAuthService.getAuthInfo();//授权成功就把授权信息保存到Application范围
				//new ListCacheService(mHandler,IndexActivity.this);
				gotoHomeActivity();
				break;
			case Configs.Failure.AUTH_WRONG://授权失败
				showDilog(StatusCodeMoon.getStatusMsg(StatusCodeMoon.AUTH_FAIL,getString(R.string.auth_fail)));
				break;
			case Configs.Failure.NETWORK_EXCEPTION:
				int errorCode = msg.arg1;
				showDilog(StatusCodeMoon.getStatusMsg(errorCode,getString(R.string.network_exception)));
				break;
			case Configs.CACHE_LIST_NEW:
				Toast.makeText(IndexActivity.this, IndexActivity.this.getResources().getString(R.string.cache_new), Toast.LENGTH_LONG).show();
				break;
			}
			
		}
	};

	private void gotoHomeActivity() {
		finish();
		ActivityUtils.startActivity(this, GetListActivity.class);
	}

	private void showDilog(String msg) {
		showLoadContainer(false);
		mTextPrompt.setText(msg);
	}

	private void showLoadContainer(boolean isShow) {
		if (isShow) {
			mContainerLoad.setVisibility(View.VISIBLE);
			mContainerError.setVisibility(View.GONE);
		} else {
			mContainerLoad.setVisibility(View.GONE);
			mContainerError.setVisibility(View.VISIBLE);
			mBtnReload.requestFocus();
			mBtnReload.requestFocusFromTouch();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		System.exit(0);
	};
}
