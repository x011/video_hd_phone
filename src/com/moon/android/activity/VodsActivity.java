package com.moon.android.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ev.android.evodshd.R;
import com.ev.player.MyPlayerActivity;
import com.ev.player.history.HistoryDAO;
import com.ev.player.history.HistoryItem;
import com.ev.player.model.VodVideo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.model.Ad;
import com.moon.android.model.Drama;
import com.moon.android.model.VodProgram;
import com.moon.android.model.VodProgramDetail;
import com.moon.android.moonplayer.service.AdService;
import com.moon.android.moonplayer.service.ProgramDetailService;
import com.mooncloud.android.iptv.adapter.VideoAdapter;
import com.moonclound.android.iptv.util.DbUtil;
import com.moonclound.android.iptv.util.Logger;
import com.moonclound.android.iptv.util.MyDecode;
import com.moonclound.android.iptv.util.StringUtil;
import com.moonclound.android.view.CustomToast;
import com.moonclound.android.view.ProgramDetailLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class VodsActivity extends Activity implements OnKeyListener {
	public DbUtil db;
	private Logger logger = Logger.getInstance();
	public static final int VOD_GRID_COLUMN = 6;
	public static final int VOD_GRID_ROW = 5;
	public static final int VOD_PER_PAGE = VOD_GRID_COLUMN * VOD_GRID_ROW;
	private VodProgram mVodProgram;
	private TextView mTextVodName;
	private GridView mGridVod;
	private LinearLayout mLinearPage_bt;
	private ImageButton mIB_PageUp,mIB_PageDown;
	private List<Drama> mListVodVideo;
	private ImageView mImageView;
	private int mCurrentPage;
	private int mTotalPage;
	private TextView mTextPage;
	private TextView mTextCurrentPage;
	private RelativeLayout mContainerPrompt;
	private ImageView mLoadingAnim;
	private LinearLayout mContainerPromtReload;
	private Button mBtnRetry;
	private TextView mVodAnthology;
	private TextView mTextVodNull;
	private LinearLayout mContainerPromptHistory;
	private TextView mTextPromptHistory;
	private Button mBtnLastPlay;

	private TextView mTextStar;
	private TextView mTextDirector;
	private TextView mTextShowTime;
	private TextView mTextIntroduce;

	private Button mBtnPlay;
	private LinearLayout mContainerIntroduce;
	private LinearLayout mContainerPageNum;
	/*
	 * 判断是否加载完成列表，如果加载完成，则显示历史播放的按钮
	 */
	private boolean isLoadList = false;
	private DisplayImageOptions options;

	private VodProgramDetail mVodProgramDetail;
	private Handler mVodDetailHandler;
	private ProgramDetailService mProgramDetailService;

	private ProgramDetailLayout mProgramDetailLayout;

	private Handler mAdHandler;
	private PopupWindow mAdPopup;
	private View mAdView;
	private AdService mAdService;
	private Ad mAd;
	private String mCid;
	private ImageView mAdImage;
	private TextView mAdTimePrompt;
	private int mSecondRemain;
	private Timer mSecondTimer;

	// private Button mBt_collect,mBt_cacel_coll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getResources().getString(R.string.screen_type).equals("600")){
			setContentView(R.layout.activity_vods_600);
			mLinearPage_bt = (LinearLayout) findViewById(R.id.page_bt);
			mIB_PageUp = (ImageButton) findViewById(R.id.page_up);
			mIB_PageDown = (ImageButton) findViewById(R.id.page_down);
			mIB_PageUp.setOnClickListener(mPageClickListener);
			mIB_PageDown.setOnClickListener(mPageClickListener);
		}else{
			setContentView(R.layout.activity_vods);
		}

		initHandler();
		initOption();
		getIntentData();
		initWidget();
		initAd();
		getVodData();
		getVodDetailData();
		// isColl();
		// new Thread(mHistoryLoadR).start();
	}

	private OnClickListener mPageClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.page_up:
				if (mCurrentPage == 0) 
					return;
				mCurrentPage--;
				fillVodGrid(mCurrentPage);
				break;
			case R.id.page_down:
				if (mCurrentPage + 1 == mTotalPage)
					return;
				mCurrentPage++;
				fillVodGrid(mCurrentPage);
				break;
			default:
				break;
			}
		}
	};
	private void isColl() {
		// TODO Auto-generated method stub
		Boolean iscoll = db.isSaveColl(mVodProgram.getSid());
		// Log.d("isColl",iscoll.toString());
		// if(iscoll){
		// //不在收藏夹
		// mBt_cacel_coll.setVisibility(View.VISIBLE);
		// mBt_collect.setVisibility(View.GONE);
		//
		// }else{
		// //在收藏夹
		// mBt_cacel_coll.setVisibility(View.GONE);
		// mBt_collect.setVisibility(View.VISIBLE);
		// }

	}

	private void initAd() {
		mAdService = new AdService(mAdHandler);
	}

	@SuppressLint("HandlerLeak")
	private void initHandler() {
		mVodDetailHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Configs.Handler.PROGRAM_DETAIL_OK:
					mVodProgramDetail = mProgramDetailService.getVodProgramDetail();
					mProgramDetailLayout.setData(mVodProgramDetail);
					// resetProgramName();//there is no need
					break;
				case Configs.Handler.PROGRAM_DETAIL_PARSE_FAILURE:
				case Configs.Handler.PROGRAM_DETAIL_NULL:
					new CustomToast(VodsActivity.this, getString(R.string.get_program_detail_failure)).show();
					break;
				}
			}
		};

		mAdHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Configs.Success.GET_AD:
					mAd = mAdService.getAd();
					playAfterAd();
					break;
				case Configs.Failure.GET_AD:
					break;
				case Configs.Success.CLOSE_AD:
					closeAd();
					break;
				case Configs.Success.UPDATE_AD_TIME:
					mAdTimePrompt.setText("" + mSecondRemain);
					mSecondRemain--;
					break;
				case Configs.Success.STOP_AD_TIME:
					mSecondTimer.cancel();
					break;
				}
			}
		};
	}

	/**
	 * calling after loading program detail info
	 */
	@Deprecated
	public void resetProgramName() {
		if (mVodProgramDetail != null) {
			if (!"-1".equals(mVodProgramDetail.getYear().trim())) {
				if (mVodProgram != null) {
					mTextVodName.setText(mVodProgram.getName() + "  (" + mVodProgramDetail.getYear() + ")");
				}
			}
		}
	}

	private void getVodDetailData() {
		mProgramDetailService = new ProgramDetailService(mVodDetailHandler);
		mProgramDetailService.initList(Configs.URL.getProgramDetailApi() + mVodProgram.getSid(), mVodProgram.getSid());
	}

	private void initWidget() {
		mTextVodName = (TextView) findViewById(R.id.vod_name);
		mGridVod = (GridView) findViewById(R.id.vod_grid);
		mImageView = (ImageView) findViewById(R.id.vods_image);
		mTextPage = (TextView) findViewById(R.id.vod_page_show);
		mTextCurrentPage = (TextView) findViewById(R.id.vod_current_page);
		mContainerPrompt = (RelativeLayout) findViewById(R.id.vod_load_prompt);
		mLoadingAnim = (ImageView) findViewById(R.id.loading_anim);
		((AnimationDrawable) mLoadingAnim.getDrawable()).start();
		mContainerPromtReload = (LinearLayout) findViewById(R.id.vod_prompt);
		mBtnRetry = (Button) findViewById(R.id.vod_reload);
		mVodAnthology = (TextView) findViewById(R.id.vod_anthology);
		mTextVodNull = (TextView) findViewById(R.id.vod_no_vod);
		mTextStar = (TextView) findViewById(R.id.film_detail_starring);
		mTextDirector = (TextView) findViewById(R.id.film_detail_Director);
		mTextShowTime = (TextView) findViewById(R.id.film_detail_Showtimes);
		mTextIntroduce = (TextView) findViewById(R.id.film_detail_brief);
		mBtnPlay = (Button) findViewById(R.id.vod_play_btn);
		// mBt_collect=(Button) findViewById(R.id.vod_collect);
		// mBt_cacel_coll=(Button) findViewById(R.id.vod_cacel_collect);
		mBtnPlay.setTextColor(getResources().getColorStateList(R.color.color_btn_play));
		mContainerIntroduce = (LinearLayout) findViewById(R.id.container_film_introduce);
		mContainerPageNum = (LinearLayout) findViewById(R.id.container_page_num);
		mContainerPromptHistory = (LinearLayout) findViewById(R.id.container_prompt_hisotry);
		mTextPromptHistory = (TextView) findViewById(R.id.text_promtp_history);
		mBtnLastPlay = (Button) findViewById(R.id.replay);

		mBtnLastPlay.setOnClickListener(mBtnReplayCl);
		mBtnPlay.setOnClickListener(mBtnPlayClickListener);
		mGridVod.setNumColumns(VOD_GRID_COLUMN);
		mGridVod.setOnKeyListener(this);
		mGridVod.setOnItemClickListener(mListitemClickListener);
		if (null != mVodProgram)
			mTextVodName.setText(mVodProgram.getName());
		mBtnRetry.setOnClickListener(mVodRetryClickListener);
		showNetImage(mVodProgram.getLogo());
		// mBt_collect.setOnClickListener(addCollClick);
		// mBt_cacel_coll.setOnClickListener(delCollClick);
		mProgramDetailLayout = (ProgramDetailLayout) findViewById(R.id.program_detail_layout);
		// http://pic4.nipic.com/20090908/1771106_122015033242_2.jpg
		mAdView = LayoutInflater.from(this).inflate(R.layout.layout_ad, null);
		mAdImage = (ImageView) mAdView.findViewById(R.id.program_ad_image);
		mAdTimePrompt = (TextView) mAdView.findViewById(R.id.program_ad_countdown);
		mAdPopup = new PopupWindow(mAdView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		mAdPopup.setBackgroundDrawable(new ColorDrawable());
		// mAdPopup.set
		db = new DbUtil(VodsActivity.this);
	}

	private OnClickListener delCollClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			try {
				Gson g = new Gson();
				String json = g.toJson(mVodProgram);
				db.DelColl(mVodProgram.getSid());
				isColl();
				Toast.makeText(VodsActivity.this, "已取消收藏", Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	};
	private OnClickListener addCollClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			try {
				Gson g = new Gson();
				String json = g.toJson(mVodProgram);
				db.SaveColl(mVodProgram.getSid(), mCid, json);
				isColl();
				Toast.makeText(VodsActivity.this, "已加入收藏", Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	};

	private void showAd() {
		logger.i("show()");
		mSecondRemain = Integer.parseInt(mAd.getSec());
		ImageLoader.getInstance().displayImage(mAd.getAdurl(), mAdImage, options);
		mAdPopup.showAtLocation(mAdView, Gravity.CENTER, 0, 0);
		mSecondTimer = new Timer();
		mSecondTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.i("Ad Second Remain = " + mSecondRemain);
				if (mSecondRemain >= 0 && mAdPopup.isShowing()) {
					mAdHandler.sendEmptyMessage(Configs.Success.UPDATE_AD_TIME);
				} else {
					mAdHandler.sendEmptyMessage(Configs.Success.STOP_AD_TIME);
				}
			}
		}, 0, 1000);
	}

	private void closeAd() {
		mAdPopup.dismiss();
	}

	private OnClickListener mBtnReplayCl = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			isPlayHistory = true;
			int playIndex = 0;
			if (null != mListVodVideo && mArrayHistory[0] < mListVodVideo.size())
				playIndex = mArrayHistory[0];
			startPlay(playIndex);
		}
	};

	int[] mArrayHistory = new int[] { 0, 0 };
	private static final int HISTORY_SUCCESS = 0x201;

	private void showHistory() {

		logger.i("show history");
		HistoryDAO historyDAO = new HistoryDAO(this);
		logger.i("mVodProgram.getSid()");
		HistoryItem item = historyDAO.isExist(mVodProgram.getSid());
		if (null != item) {
			int playIndex = Integer.valueOf(item.getPlayIndex());
			int playPos = Integer.valueOf(item.getPlayPos());
			mArrayHistory[0] = playIndex;
			mArrayHistory[1] = playPos;
			if (null != mListVodVideo && playIndex < mListVodVideo.size()) {
				mHandler.sendEmptyMessage(HISTORY_SUCCESS);
			}
		}
	}

	private String getHistoryPrommpt(int[] s) {
		String promptText = getString(R.string.history_prompt);
		int index = 0;
		int millonSecond = 0;
		try {
			index = Integer.valueOf(s[0]);
			millonSecond = Integer.valueOf(s[1]);
		} catch (Exception e) {
			logger.e(e.toString());
		}
		if (null == mListVodVideo || index >= mListVodVideo.size())
			return "";

		try {
			String vodIndexName = mListVodVideo.get(index).getName();
			String vodShowIndexName = isNumeric(vodIndexName)
					? getString(R.string.di) + vodIndexName + getString(R.string.ji) : vodIndexName;
			String vodName = new StringBuilder(promptText).append(vodShowIndexName).append(" ")
					.append(getTime(millonSecond)).toString();
			return vodName;
		} catch (Exception e) {
			logger.e(e.toString());
		}
		return "";
	}

	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	private String getTime(int millisecond) {
		int j = millisecond / 1000;
		int k = j / 60;
		int m = k / 60;
		int n = j % 60;
		int i1 = k % 60;
		Object[] arrayOfObject = new Object[3];
		arrayOfObject[0] = Integer.valueOf(m);
		arrayOfObject[1] = Integer.valueOf(i1);
		arrayOfObject[2] = Integer.valueOf(n);
		return String.format("%02d:%02d:%02d", arrayOfObject);
	}

	private OnClickListener mBtnPlayClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (null != mListVodVideo && mListVodVideo.size() >= 1) {
				startPlay(0);
			}
		}
	};

	private OnClickListener mVodRetryClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mLoadingAnim.setVisibility(View.VISIBLE);
			mContainerPromtReload.setVisibility(View.GONE);
			// reload
			if (null != mVodProgram) {
				FinalHttp finalHttp = new FinalHttp();
				String url = Configs.URL.getDramaApi() + mVodProgram.getSid();
				logger.i("request ur = " + url);
				finalHttp.get(StringUtil.deleteSpace(url), mLoadVodsCallBack);
			}
		}
	};

	private void fillVodGrid(int whichPage) {
		List<Drama> listVodVideo = new ArrayList<Drama>();
		int totalSize = mListVodVideo.size();
		int startPos = whichPage * VOD_PER_PAGE;
		int endPos = whichPage * VOD_PER_PAGE + VOD_PER_PAGE;
		if (endPos >= totalSize)
			endPos = totalSize;
		for (int i = startPos; i < endPos; i++) {
			listVodVideo.add(mListVodVideo.get(i));
		}
		VideoAdapter vodAdapter = new VideoAdapter(getApplicationContext(), listVodVideo);
		mGridVod.setAdapter(vodAdapter);
		mGridVod.requestFocus();
		String constantPage = getString(R.string.page);
		mTextPage.setText("/" + mTotalPage + constantPage);
		mTextCurrentPage.setText(String.valueOf(mCurrentPage + 1));
	}

	private void showNetImage(String imageUrl) {
		ImageLoader.getInstance().displayImage(imageUrl, mImageView, options);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			/*
			 * 只有加载完成列表以后，才去历史记录了 防止第一次进入还未加载完成，点击了历史记录报错
			 */
			if (isLoadList) {
				showHistory();
			}
		} catch (Exception e) {
			logger.e(e.toString());
		}
	}

	private boolean isPlayHistory = false;
	private int mPlayPos = 0;
	private OnItemClickListener mListitemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			isPlayHistory = false;
			mPlayPos = mCurrentPage * VOD_PER_PAGE + position;

			mAdService.initAd(mCid);
		}
	};

	private void playAfterAd() {
		Timer timer = new Timer();
		logger.i("ad is null ? = " + mAd);
		if (mAd != null) {
			showAd();
			long delay = Long.parseLong(mAd.getSec()) * 1000;
			logger.i("ad show time = " + delay);
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					if (mSecondRemain <= 0) {
						startPlay(mPlayPos);
						mAdHandler.sendEmptyMessage(Configs.Success.CLOSE_AD);
					}
				}
			}, delay);
		} else {
			startPlay(mPlayPos);
		}

	}

	private void startPlay(int position) {
		List<VodVideo> listVOdVideo = new ArrayList<VodVideo>();
		for (Drama v : mListVodVideo) {
			VodVideo video = new VodVideo();
			video.setChannelId(v.getChannelId());
			Log.d("getChannelId:", v.getChannelId() + "");
			video.setLink(v.getLink());
			Log.d("getLink():", v.getLink() + "");
			video.setName(v.getName());
			Log.d("getName():", v.getName() + "");
			video.setStreamip(v.getStreamip());
			Log.d("getStreamip():", v.getStreamip() + "");
			video.setSubcatid(mVodProgram.getSid());
			video.setType("mp4");
			video.setUrl(v.getUrl());
			Log.d("getUrl():", v.getUrl() + "");
			listVOdVideo.add(video);
			logger.i(v.toString());
		}

		ComponentName componetName = new ComponentName(this, MyPlayerActivity.class);
		System.out.println("MyPlayerActivity");
		try {
			Intent intent = new Intent();
			if (mAd != null) {
				intent.putExtra(Configs.INTENT_PARAM_2, mAd.getAdurl());
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("programLogo", mVodProgram.getLogo());
			intent.putExtra("programName", mVodProgram.getName());
			intent.putExtra("Cid", mCid);
			Gson gson = new Gson();
			String ProgramJson = gson.toJson(mVodProgram);

			intent.putExtra("mVodProgram", ProgramJson);
			intent.putExtra("videolist", (Serializable) listVOdVideo);
			intent.putExtra("hasHistory", isPlayHistory);
			intent.putExtra("index", position);
			intent.putExtra("appmsg", MyApplication.appMsg + "+++" + Configs.APPID);
			if (isPlayHistory) {
				intent.putExtra("history_vod_index", mArrayHistory[0]);
				intent.putExtra("history_vod_pos", mArrayHistory[1]);
			}
			intent.setComponent(componetName);
			startActivity(intent);
		} catch (Exception e) {
			logger.e(e.toString());
		}
	}

	private void getVodData() {
		FinalHttp finalHttp = new FinalHttp();
		if (null != mVodProgram) {
			String url = Configs.URL.getDramaApi() + mVodProgram.getSid();
			logger.i("节目列表点击后请求剧集的URL=" + url);
			if (null != mVodProgram.getSid())
				finalHttp.get(StringUtil.deleteSpace(url), mLoadVodsCallBack);
			else {
				new CustomToast(VodsActivity.this, getString(R.string.data_err)).show();
				if (null != mLoadingAnim)
					mLoadingAnim.setVisibility(View.GONE);
			}
		}
	}

	private String isAutoPlay;

	private void getIntentData() {
		Intent intent = getIntent();
		isAutoPlay = intent.getStringExtra("isAuto");
		mCid = intent.getStringExtra(Configs.INTENT_PARAM_2);
		mVodProgram = (VodProgram) intent.getSerializableExtra(Configs.INTENT_PARAM);
	}

	private AjaxCallBack<Object> mLoadVodsCallBack = new AjaxCallBack<Object>() {
		public void onSuccess(Object t) {
			super.onSuccess(t);
			logger.i((String) t);
			String vodStr = (String) t;
			// Log.d("vodStr:", vodStr);
			vodStr = new MyDecode().getjson(vodStr);
			if (StringUtil.isBlank(vodStr)) {

				if (!dbCacheList()) {
					mHandler.sendEmptyMessage(Configs.CONTENT_IS_NULL);
				}

				return;
			}
			String arrayJson = (String) t;

			// Log.d("arrayJson:",arrayJson);
			if (null == arrayJson) {

				if (!dbCacheList()) {
					mHandler.sendEmptyMessage(Configs.CONTENT_IS_NULL);
				}

				return;
			}
			try {
				logger.i("Video json = " + arrayJson);
				arrayJson = new MyDecode().getjson(arrayJson);
				parseVodJson(arrayJson);
				Log.d("sid", mVodProgram.getSid() + "");
				mHandler.sendEmptyMessage(Configs.SUCCESS);
				db.SaveVod(mVodProgram.getSid(), (String) t, null);
			} catch (Exception e) {
				logger.e(e.toString());

				if (!dbCacheList()) {
					mHandler.sendEmptyMessage(Configs.FAIL);
				}

			}
		};

		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
			if (!dbCacheList()) {
				mHandler.sendEmptyMessage(Configs.FAIL);
			}

		};
	};

	public boolean dbCacheList() {
		String dbCache = db.GetVodJson(mVodProgram.getSid());
		if (dbCache != null) {
			Log.d("--", dbCache);
			try {
				String json = new MyDecode().getjson(dbCache);
				parseVodJson(json);
				mHandler.sendEmptyMessage(Configs.SUCCESS);
			} catch (Exception e) {

				mHandler.sendEmptyMessage(Configs.FAIL);
			}
			return true;
		} else {
			Log.d("--", "无数据库缓存或查询出错");
			return false;
		}

	}

	private String getGsonParseArrayString(String orginGsonStr) {
		try {
			int startPos = orginGsonStr.indexOf("[");
			int endPos = orginGsonStr.length() - 1;
			String arrayJsonStr = orginGsonStr.substring(startPos, endPos);
			return arrayJsonStr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private void parseVodJson(String vodJson) {
		mListVodVideo = new ArrayList<Drama>();
		Gson gson = new Gson();
		List<Drama> listVodVideo = (List<Drama>) gson.fromJson(vodJson, new TypeToken<List<Drama>>() {
		}.getType());

		for (Drama vodVideo : listVodVideo) {
			String url = vodVideo.getUrl();
			String[] arrayOfString = url.split("://")[1].split("/");
			vodVideo.setStreamip(arrayOfString[0]);
			vodVideo.setChannelId(arrayOfString[1]);
			vodVideo.setLink(Configs.link);
			if (null != Configs.link)
				vodVideo.setLink(Configs.link);
			mListVodVideo.add(vodVideo);
		}
	}

	private boolean isFirst = true;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HISTORY_SUCCESS:
				mBtnLastPlay.setVisibility(View.VISIBLE);
				mTextPromptHistory.setText(getHistoryPrommpt(mArrayHistory));
				if (isAutoPlay.equals("1")) {
					if (isFirst) {
						isPlayHistory = true;
						int playIndex = 0;
						if (null != mListVodVideo && mArrayHistory[0] < mListVodVideo.size())
							playIndex = mArrayHistory[0];
						startPlay(playIndex);
						isFirst = false;
					}

				}
				logger.i("playpos = " + mArrayHistory[1]);
				if (null == mGridVod)
					logger.i("grid view is null");
				break;
			case Configs.SUCCESS:
				caculateTotalPage();
				mContainerPrompt.setVisibility(View.GONE);
				mGridVod.setVisibility(View.VISIBLE);
				if (mTotalPage>1){
					if (mLinearPage_bt!=null)
						mLinearPage_bt.setVisibility(View.VISIBLE);
				}
				mVodAnthology.setVisibility(View.VISIBLE);
				fillVodGrid(0);
				isLoadList = true;
				showHistory();
				break;
			case Configs.FAIL:
				mContainerPromtReload.setVisibility(View.VISIBLE);
				mLoadingAnim.setVisibility(View.GONE);
				mVodAnthology.setVisibility(View.GONE);
				mBtnRetry.requestFocus();
				break;
			case Configs.CONTENT_IS_NULL:
				mVodAnthology.setVisibility(View.GONE);
				mTextVodNull.setVisibility(View.VISIBLE);
				mContainerPrompt.setVisibility(View.GONE);
				break;
			}
		}

	};

	private void caculateTotalPage() {
		mTotalPage = mListVodVideo.size() / VOD_PER_PAGE;
		boolean isDivider = mListVodVideo.size() % VOD_PER_PAGE == 0;
		if (!isDivider)
			mTotalPage++;
	};

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (v == mGridVod && event.getAction() == KeyEvent.ACTION_DOWN) {
			int gridSelection = mGridVod.getSelectedItemPosition();
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (gridSelection + (VOD_PER_PAGE * (mCurrentPage - 1)) + 1 == mListVodVideo.size()) {
					return true;
				}
				if ((gridSelection + 1) % VOD_GRID_COLUMN == 0) {
					if (mCurrentPage + 1 == mTotalPage)
						return true;
					mCurrentPage++;
					fillVodGrid(mCurrentPage);
					int nextSelection = gridSelection - (VOD_GRID_COLUMN - 1);
					mGridVod.setSelection(nextSelection);
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (gridSelection % VOD_GRID_COLUMN == 0) {
					if (mCurrentPage == 0) {
						return true;
					} else {
						mCurrentPage--;
						fillVodGrid(mCurrentPage);
						int nextSelection = gridSelection + (VOD_GRID_COLUMN - 1);
						mGridVod.setSelection(nextSelection);
						return true;
					}
				}
				break;
			default:
				break;
			}
		}
		return false;
	}

	private void initOption() {
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.pic_loading)
				.showImageForEmptyUri(R.drawable.pic_loading).showImageOnFail(R.drawable.pic_loading)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				// .displayer(new RoundedBitmapDisplayer(20))
				.build();
	}
}
