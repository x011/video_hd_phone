package com.moon.android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ev.android.evodshd.plus.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moon.android.broadcast.MsgBroadcastReceiver;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.iptv.arb.film.MsgService;
import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.iptv.arb.film.RequestDAO;
import com.moon.android.model.AuthInfo;
import com.moon.android.model.Drama;
import com.moon.android.model.KeyParam;
import com.moon.android.model.Navigation;
import com.moon.android.model.SeconMenu;
import com.moon.android.model.VodProgram;
import com.moon.android.moonplayer.service.AuthService;
import com.moon.android.moonplayer.service.NavigationService;
import com.moon.android.moonplayer.service.ProgramService;
import com.moon.android.moonplayer.service.SeconMenuService;
import com.mooncloud.android.iptv.adapter.LeftMenuAdapter;
import com.mooncloud.android.iptv.adapter.ProgramAdapter;
import com.mooncloud.android.iptv.adapter.SeconMenuAdapter;
import com.mooncloud.android.iptv.database.PasswordDAO;
import com.mooncloud.heart.beat.Beat;
import com.moonclound.android.iptv.util.AESSecurity;
import com.moonclound.android.iptv.util.ActivityUtils;
import com.moonclound.android.iptv.util.Logger;
import com.moonclound.android.iptv.util.MD5Util;
import com.moonclound.android.iptv.util.SecurityModule;
import com.moonclound.android.iptv.util.StringUtil;
import com.moonclound.android.iptv.util.UpdateData;
import com.moonclound.android.view.CustomToast;
import com.moonclound.android.view.PasswordDialog;

public class HomeActivity extends Activity implements OnClickListener {

	private Logger logger = Logger.getInstance();

	// 浠ヤ笅鏄幏鍙栨巿鏉冧俊鎭浉鍏�
	private AuthInfo mAuthInfo = MyApplication.authInfo;

	// 浠ヤ笅鏄乏渚ц彍鍗曠浉鍏�
	private Handler mLeftMenuHandler;
	private NavigationService mNavigationService;
	private ListView mLeftMenuListView;
	private List<Navigation> mLeftMenuList = new ArrayList<Navigation>();
	private LeftMenuAdapter mLeftMenuAdapter;
	/** 褰撳墠宸︿晶鑿滃崟鍒楄〃鐨刢id閫夐」,鏍规嵁杩欎釜鍊肩‘瀹氬姞杞藉摢涓簩绾ц彍鍗� */
	private String mCurrentCid;

	// 浠ヤ笅鏄簩绾ц彍鍗曠浉鍏冲彉閲�
	private static final int SECON_MENU_COLUMN = 7;
	private Handler mSeconMenuAHandler;
	private GridView mGridSeconMenu;
	private SeconMenuAdapter mSeconMenuAdapter;
	private SeconMenuService mSeconMenuService;
	private List<SeconMenu> mSeconMenuList = new ArrayList<SeconMenu>();
	/** 褰撳墠浜岀骇鑿滃崟,姣忔鐐逛竴绾ц彍鍗曞悗杩欎釜閮介粯璁や负绗竴涓�変腑 */
	private SeconMenu mCurrentSeconMenu;

	// 浠ヤ笅鏄妭鐩垪琛ㄧ浉鍏�
	private GridView mGridVodShow;
	List<VodProgram> mVodProgramList = new ArrayList<VodProgram>();
	private ProgramService mProgramService;
	private ProgramAdapter mProgramAdapter;
	public static final int VOD_GRID_COLUMN = 4;
	private Handler mProgramHandler;
	private int mTotalItemCount = 0;
	private int mCurrentSelection = 0;

	/** 椤垫暟鎻愮ず,鏍煎紡涓� 2/92,琛ㄧず鎬诲叡92鏉¤褰曪紝褰撳墠涓虹浜屾潯 */
	private TextView mPagePrompt;
	private ImageView mLoadingAnim;

	// 闄愬埗绾у奖鐗囩浉鍏�
	/** 鏄惁宸茬粡杈撳叆杩囧瘑鐮� */
	private boolean isPassEver = false;

	private MsgBroadcastReceiver mMsgReceiver;
	private Intent mMsgServiceIntent;
	private static final int TOAST_TEXT_SIZE = 24;

	private TextView mLoadSeconMenuFailedBtn;
	private TextView mLoadProgramFailedBtn;

	public LinearLayout mLine_his, mLine_hot, home_search;

	// add heart beat to check white list
	private Beat mHeartBeat = Beat.getInstance(Configs.HeartBeat.URL, Configs.HeartBeat.MAC);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		// addHeartBeat();
		//
		initHandler();
		initService();
		initView();
//		timeStart();
		initAdapter();
		mNavigationService.initList(mAuthInfo);// 鍙湁鎵ц杩欏彞鎵嶄細涓�绾т竴绾ц幏鍙栧悇绉嶅垪琛�

		registerMyReceiver();
		startUpdatAndGetMsgService();
		
		// startActivity(new Intent(this,HistoryActivity.class));
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				System.out.println("--update");
				UpdateData localUpdateData = RequestDAO.checkUpate(MyApplication.getApplication());
//				Log.d("RequestDAOUPDATA", localUpdateData.toString());
				if (null != localUpdateData) {
					Intent intent = new Intent();
					intent.setAction(Configs.BroadCast.UPDATE_MSG);
					MyApplication.updateData = localUpdateData;
					sendBroadcast(intent);
				}
			}
		}.start();
	}
	
	Handler xxmHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
		
	};
	 
	private int restart=10;//重试次数
	private int nowindex=0;//当前次数
	private void timeStart() {
		// TODO Auto-generated method stub
		Timer authtime=new Timer();
		authtime.schedule(new TimerTask() {
			
			@Override
			public void run() { 
				// TODO Auto-generated method stub
				if(MyApplication.white.equals("1") && nowindex<restart){
//					Log.d("timer",MyApplication.white);
					AuthService mAuthService = new AuthService(xxmHandler, HomeActivity.this);//联网成功就获取授权
					mAuthService.findFromNet(false);
					nowindex++;
					
				}
				
				
			}
		}, 1,8000);
	}
	private void addHeartBeat() {
		mHeartBeat.start(new Beat.LoginResult() {
			@Override
			public void success(String success) {

			}

			@Override
			public void fail(String error) {
				mHeartBeat.login();
			}
		});
	}

	private void startUpdatAndGetMsgService() {
		mMsgServiceIntent = new Intent(this, MsgService.class);
		startService(mMsgServiceIntent);
	}

	private void registerMyReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Configs.BroadCast.APP_GET_MSG);
		intentFilter.addAction(Configs.BroadCast.UPDATE_MSG);
		mMsgReceiver = new MsgBroadcastReceiver(this);
		registerReceiver(mMsgReceiver, intentFilter);
	}

	/**
	 * 鍒濆鍖栬幏鍙栧悇绉嶅垪琛ㄧ殑鏈嶅姟绫�
	 */
	private void initService() {
		mNavigationService = new NavigationService(mLeftMenuHandler);
		mSeconMenuService = new SeconMenuService(mSeconMenuAHandler);
		mProgramService = new ProgramService(mProgramHandler);
	}

	private void initAdapter() {
		initLeftMenuAdapter();
		initSeconMenuAdapter();
		initProgramAdapter();
	}

	private void initLeftMenuAdapter() {
		// mLeftMenuList.clear();
		// Log.d("11",mLeftMenuList.size()+"");
		mLeftMenuAdapter = new LeftMenuAdapter(this, mLeftMenuList);
		mLeftMenuListView.setAdapter(mLeftMenuAdapter);

	}

	private void initSeconMenuAdapter() {
		mSeconMenuAdapter = new SeconMenuAdapter(this, mSeconMenuList);
		mGridSeconMenu.setAdapter(mSeconMenuAdapter);
	}

	private void initProgramAdapter() {
		mProgramAdapter = new ProgramAdapter(this, mVodProgramList);
		mGridVodShow.setAdapter(mProgramAdapter);
	}

	private void initView() {

		mLeftMenuListView = (ListView) findViewById(R.id.main_cat_list);
		mLeftMenuListView.setNextFocusUpId(R.id.main_cat_list);
		mLeftMenuListView.setOnItemClickListener(new OnLeftMenuItemClickListener());
		mLeftMenuListView.setOnItemSelectedListener(new OnLeftMenuItemSelectedListener());

		mGridSeconMenu = (GridView) findViewById(R.id.secon_menu_grid);
		mGridSeconMenu.setNumColumns(SECON_MENU_COLUMN);
		mGridSeconMenu.setOnItemClickListener(new OnSeconMenuItemClickListener());

		mGridVodShow = (GridView) findViewById(R.id.main_subcat_grid);
		mGridVodShow.setNumColumns(VOD_GRID_COLUMN);
		mGridVodShow.setOnKeyListener(new OnGridVodKeyListener());
		mGridVodShow.setOnItemClickListener(new OnGridVodItemClickListener());
		mGridVodShow.setOnItemSelectedListener(new OnGridVodItemSelectedListener());

		mLoadingAnim = (ImageView) findViewById(R.id.loading_anim);
		((AnimationDrawable) mLoadingAnim.getDrawable()).start();
		mPagePrompt = (TextView) findViewById(R.id.main_page);

		mLoadSeconMenuFailedBtn = (TextView) findViewById(R.id.failed_to_get_seconmenu);
		mLoadSeconMenuFailedBtn.setOnClickListener(this);

		mLoadProgramFailedBtn = (TextView) findViewById(R.id.failed_to_get_program);
		mLoadProgramFailedBtn.setOnClickListener(this);

		mLine_hot = (LinearLayout) findViewById(R.id.home_hotLine);
		home_search = (LinearLayout) findViewById(R.id.home_search);
		home_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(HomeActivity.this, SearchActivity.class));
			}
		});
		mLine_his = (LinearLayout) findViewById(R.id.home_hisLine);
		mLine_his.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
			}
		});
		mLine_hot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(HomeActivity.this, HotActivity.class));
			}
		});
		
		
		/*focus */
		mLeftMenuListView.setNextFocusRightId(R.id.main_subcat_grid);
		mGridSeconMenu.setNextFocusLeftId(R.id.main_cat_list);
		mGridSeconMenu.setNextFocusDownId(R.id.main_subcat_grid);
		
		mGridVodShow.setNextFocusUpId(R.id.secon_menu_grid);
		mGridVodShow.setNextFocusLeftId(R.id.main_cat_list);
		
		
	
	}

	@SuppressLint("HandlerLeak")
	private void initHandler() {

		mLeftMenuHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Configs.Success.GET_LEFT_MENU:

					List<Navigation> list = mNavigationService.findAll();

					mLeftMenuList.clear();
					mLeftMenuList.addAll(list);
					mLeftMenuAdapter.notifyDataSetChanged(0);
					mLeftMenuListView.requestFocus();	
					if (mLeftMenuList.size() > 0) {
						mCurrentCid = mLeftMenuList.get(0).getCid();
						mSeconMenuService.initList(mCurrentCid);
					}
					break;
				}
			}

		};

		mSeconMenuAHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Configs.Success.GET_SECON_MENU:

					setViewVisible(mLoadingAnim, false);
					setViewVisible(mGridSeconMenu, true);
					setViewVisible(mLoadSeconMenuFailedBtn, false);
					setViewVisible(mPagePrompt, true);

					List<SeconMenu> list = mSeconMenuService.getList();
					// 濡傛灉娌℃湁浜岀骇鑿滃崟锛屽氨缁欎竴涓粯璁ょ殑浜岀骇鑿滃崟SeconMenu("鍏ㄩ儴",null,null);
					if (list.size() == 0) {
						list.add(new SeconMenu());
					}

					mSeconMenuList.clear();
					mSeconMenuList.addAll(list);
					mSeconMenuAdapter.notifyDataSetChanged(0);

					if (mSeconMenuList.size() > 0) {

						setViewVisible(mLoadingAnim, true);
						mGridVodShow.setVisibility(View.INVISIBLE);
						mPagePrompt.setVisibility(View.INVISIBLE);

						mCurrentSeconMenu = mSeconMenuList.get(0);
						// 鍒濆鍖栦簩绾ц彍鍗曞垪琛ㄥ悗锛屽姞杞借妭鐩垪琛�
						mProgramService.initList(mCurrentCid, mCurrentSeconMenu);
					}

					break;
				case Configs.Failure.GET_SECON_MENU:
					setViewVisible(mLoadingAnim, false);
					mGridVodShow.setVisibility(View.INVISIBLE);
					setViewVisible(mLoadSeconMenuFailedBtn, true);
					setViewVisible(mGridSeconMenu, false);
					mPagePrompt.setVisibility(View.INVISIBLE);
					break;
				}
			}
		};

		mProgramHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Configs.Success.GET_PROGRAM_LIST:

					setViewVisible(mGridSeconMenu, true);
					setViewVisible(mLoadingAnim, false);
					setViewVisible(mGridVodShow, true);
					setViewVisible(mLoadProgramFailedBtn, false);
					setViewVisible(mPagePrompt, true);

					List<VodProgram> list = mProgramService.getList();
					mVodProgramList.clear();
					mVodProgramList.addAll(list);
					mProgramAdapter.notifyDataSetChanged();
					
				
//					mGridVodShow.setSelection(0);
//					mGridVodShow.requestFocus();
					mTotalItemCount = mProgramAdapter.getCount();
					mCurrentSelection = 0;
					resetPagePrompt(mCurrentSelection, mTotalItemCount);
				
					break;
				case Configs.Failure.GET_PROGRAM_LIST:
					setViewVisible(mLoadingAnim, false);
					mGridVodShow.setVisibility(View.INVISIBLE);
					setViewVisible(mLoadProgramFailedBtn, true);
					setViewVisible(mGridSeconMenu, false);
					break;
				}
			}

		};
	}

	private void resetPagePrompt(int mSelectedPosition, int mTotalItemCount) {
		mPagePrompt.setText((mSelectedPosition + 1) + "/" + mTotalItemCount);
	}

	private class OnLeftMenuItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}

	}

	private class OnLeftMenuItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

			// if is Cloud Vod App ,last left menu will be limited
			if (position == mLeftMenuList.size() - 1 && !isPassEver && Configs.isLastNeedPassword) {
				logger.i("=====杩涘叆闄愬埗鍖�=====");
				final PasswordDAO passwordDao = new PasswordDAO(getApplicationContext());
				// 鏈�鍚庝竴涓负闄愬埗绾х墖鍖猴紝闇�鍔犲瘑
				// check 鏄惁鍔犲瘑
				if (passwordDao.isTableNull()) {
					logger.i("=====妫�娴嬪埌闄愬埗鍖烘湭璁剧疆瀵嗙爜=====");
					setPassword(passwordDao, view, position);
				} else {
					logger.i("=====妫�娴嬪埌闄愬埗鍖哄凡缁忚缃瘑鐮�=====");
					// 杈撳叆瀵嗙爜
					final PasswordDialog pd = PasswordDialog.createDialog(HomeActivity.this);
					pd.setTitle(R.string.input_password);
					pd.hidePassword02().setPositiveListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							boolean isPass = passwordDao.isExist(pd.getPassword01());
							if (isPass) {
								isPassEver = true;
								// 楠岃瘉閫氳繃
								changeLeftMenu(position);
								// changeNavColor(view);
								// changeCat(position);
								pd.dismiss();
							} else {
								new CustomToast(HomeActivity.this, getString(R.string.password_wrong), TOAST_TEXT_SIZE)
										.show();
							}
						}
					}).setModifyListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							boolean isPass = passwordDao.isExist(pd.getPassword01());
							if (isPass) {
								pd.dismiss();
								setPassword(passwordDao, view, position);
							} else {
								new CustomToast(HomeActivity.this, getString(R.string.password_wrong), TOAST_TEXT_SIZE)
										.show();
							}
						}
					}).show();
				}
			} else {
				logger.i("=====鍏跺畠鍖�=====");
				setViewVisible(mGridSeconMenu, false);
				mGridVodShow.setVisibility(View.INVISIBLE);
				mPagePrompt.setVisibility(View.INVISIBLE);
				changeLeftMenu(position);
			}

		}
	}

	private void setPassword(final PasswordDAO passwordDao, final View view, final int position) {
		passwordDao.deleteAll();
		// 濡傛灉娌℃湁璁剧疆瀵嗙爜
		final PasswordDialog pd = PasswordDialog.createDialog(HomeActivity.this);
		pd.setTitle(R.string.password_setting);
		pd.hideModify().setPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pd1 = pd.getPassword01();
				String pd2 = pd.getPassword02();
				if (!StringUtil.isBlank(pd1) && !StringUtil.isBlank(pd2)) {
					if (pd1.equals(pd2)) {
						passwordDao.insert(pd1);
						changeLeftMenu(position);
						// changeNavColor(view);
						// changeCat(position);
						pd.dismiss();
					} else {
						new CustomToast(HomeActivity.this, getString(R.string.password_not_match), TOAST_TEXT_SIZE)
								.show();
					}
				} else {
					new CustomToast(HomeActivity.this, getString(R.string.password_not_null), TOAST_TEXT_SIZE).show();
				}
			}
		}).show();
	}

	public void changeLeftMenu(int position) {
		mLeftMenuAdapter.notifyDataSetChanged(position);

		mCurrentCid = mLeftMenuList.get(position).getCid();

		setViewVisible(mLoadingAnim, true);
		mSeconMenuService.initList(mCurrentCid);
	}

	private class OnSeconMenuItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mSeconMenuAdapter.notifyDataSetChanged(position);

			mCurrentSeconMenu = mSeconMenuList.get(position);
			setViewVisible(mLoadingAnim, true);
			mPagePrompt.setVisibility(View.INVISIBLE);
			mGridVodShow.setVisibility(View.INVISIBLE);
			mProgramService.initList(mCurrentCid, mCurrentSeconMenu);
		}
	}

	private class OnGridVodKeyListener implements OnKeyListener {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			mCurrentSelection = mGridVodShow.getSelectedItemPosition();
			return false;
		}

	}

	// 缈婚〉婊氬姩鏁堟灉锛屼絾鏁堟灉娌¤揪鍒伴鏈燂紝鏆傛椂鍋滃仛
	private class OnGridVodItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			/*
			 * int smoothDistance = view.getMeasuredHeight() - 30;//
			 * V浠ｈ〃鏁翠釜GridView,鍨傜洿婊氬姩涓句緥 int duration = 1000;// 绉诲姩寤惰繜
			 * 
			 * int nextSelection = mGridVodShow.getSelectedItemPosition(); int
			 * tempPosition = mTotalItemCount - mTotalItemCount %
			 * VOD_GRID_COLUMN - VOD_GRID_COLUMN;// 鍊掓暟绗笁琛屾渶鍚庝竴涓殑浣嶇疆
			 * 
			 * // 褰撴粴鍔ㄥ埌绗竴椤垫垨鑰呮渶鍚庝竴椤电殑鏃跺�欏氨涓嶆粴鍔ㄤ簡 if (mCurrentSelection + VOD_GRID_COLUMN
			 * == nextSelection && mCurrentSelection < tempPosition) {
			 * mGridVodShow.smoothScrollBy(smoothDistance, duration); } else if
			 * (mCurrentSelection - VOD_GRID_COLUMN == nextSelection &&
			 * mCurrentSelection >= VOD_GRID_COLUMN * 2) {
			 * mGridVodShow.smoothScrollBy(-smoothDistance, duration); }
			 */

			resetPagePrompt(position, mTotalItemCount);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}

	}

	private class OnGridVodItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			VodProgram vodProgram = mVodProgramList.get(position);
			// ActivityUtils.startActivity(HomeActivity.this,
			// VodsActivity.class,Configs.INTENT_PARAM, vodProgram);

			Intent intent = new Intent();
			intent.putExtra(Configs.INTENT_PARAM_2, mCurrentCid);
			intent.putExtra("isAuto", "0");
			intent.setClass(HomeActivity.this, VodsActivity.class);
			intent.putExtra(Configs.INTENT_PARAM, vodProgram);
			HomeActivity.this.startActivity(intent);
		}

	}

	/**
	 * 淇敼View鐨勬樉绀虹姸鎬�
	 * 
	 * @param visible
	 *            true--鏄剧ず false--涓嶆樉绀�
	 */
	public void setViewVisible(View view, boolean visible) {
		if (visible == true) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBackPressed() {
		showExitWindow();
	}

	private PopupWindow mExitPopupWindow;

	@SuppressLint("NewApi")
	private void showExitWindow() {
		LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.p_exit_pop, null);

		Point point = new Point();
		Display display = getWindowManager().getDefaultDisplay();
		display.getSize(point);
		int width = point.x;
		int height = point.y;
		mExitPopupWindow = new PopupWindow(view, width, height, true);
		mExitPopupWindow.showAsDropDown(view, 0, 0);
		mExitPopupWindow.setOutsideTouchable(false);
		Button sure = (Button) view.findViewById(R.id.p_eixt_sure);
		Button cancel = (Button) view.findViewById(R.id.p_eixt_cancel);
		cancel.requestFocus();
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exitPopDismiss();
			}
		});
		sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exitPopDismiss();
				clearCache();
				android.os.Process.killProcess(android.os.Process.myPid());
			}

		});
	}

	private void clearCache() {
		MyApplication.programCache.clear();
		MyApplication.seconMenuCache.clear();
	}

	private void exitPopDismiss() {
		if (null != mExitPopupWindow && mExitPopupWindow.isShowing())
			mExitPopupWindow.dismiss();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mMsgReceiver);
		stopService(mMsgServiceIntent);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		v.setVisibility(View.GONE);
		setViewVisible(mLoadingAnim, true);
		switch (v.getId()) {
		case R.id.failed_to_get_program:
			mProgramService.initList(mCurrentCid, mCurrentSeconMenu);
			break;
		case R.id.failed_to_get_seconmenu:
			mSeconMenuService.initList(mCurrentCid);
			break;
		}
	}
}
