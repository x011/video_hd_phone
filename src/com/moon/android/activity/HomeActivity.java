package com.moon.android.activity;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ev.android.evodshd.plus.R;
import com.moon.android.broadcast.MsgBroadcastReceiver;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.iptv.arb.film.MsgService;
import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.model.AuthInfo;
import com.moon.android.model.Navigation;
import com.moon.android.model.SeconMenu;
import com.moon.android.model.VodProgram;
import com.moon.android.moonplayer.service.NavigationService;
import com.moon.android.moonplayer.service.ProgramService;
import com.moon.android.moonplayer.service.SeconMenuService;
import com.mooncloud.android.iptv.adapter.LeftMenuAdapter;
import com.mooncloud.android.iptv.adapter.ProgramAdapter;
import com.mooncloud.android.iptv.adapter.SeconMenuAdapter;
import com.mooncloud.android.iptv.database.PasswordDAO;
import com.mooncloud.heart.beat.Beat;
import com.moonclound.android.iptv.util.ActivityUtils;
import com.moonclound.android.iptv.util.Logger;
import com.moonclound.android.iptv.util.StringUtil;
import com.moonclound.android.view.CustomToast;
import com.moonclound.android.view.PasswordDialog;

public class HomeActivity extends Activity implements OnClickListener {

	private Logger logger = Logger.getInstance();

	// 以下是获取授权信息相关
	private AuthInfo mAuthInfo = MyApplication.authInfo;

	// 以下是左侧菜单相关
	private Handler mLeftMenuHandler;
	private NavigationService mNavigationService;
	private ListView mLeftMenuListView;
	private List<Navigation> mLeftMenuList = new ArrayList<Navigation>();
	private LeftMenuAdapter mLeftMenuAdapter;
	/** 当前左侧菜单列表的cid选项,根据这个值确定加载哪个二级菜单 */
	private String mCurrentCid;

	// 以下是二级菜单相关变量
	private static final int SECON_MENU_COLUMN = 7;
	private Handler mSeconMenuAHandler;
	private GridView mGridSeconMenu;
	private SeconMenuAdapter mSeconMenuAdapter;
	private SeconMenuService mSeconMenuService;
	private List<SeconMenu> mSeconMenuList = new ArrayList<SeconMenu>();
	/** 当前二级菜单,每次点一级菜单后这个都默认为第一个选中 */
	private SeconMenu mCurrentSeconMenu;

	// 以下是节目列表相关
	private GridView mGridVodShow;
	List<VodProgram> mVodProgramList = new ArrayList<VodProgram>();
	private ProgramService mProgramService;
	private ProgramAdapter mProgramAdapter;
	public static final int VOD_GRID_COLUMN = 4;
	private Handler mProgramHandler;
	private int mTotalItemCount = 0;
	private int mCurrentSelection = 0;

	/** 页数提示,格式为 2/92,表示总共92条记录，当前为第二条 */
	private TextView mPagePrompt;
	private ImageView mLoadingAnim;

	// 限制级影片相关
	/** 是否已经输入过密码 */
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
		initAdapter();
		mNavigationService.initList(mAuthInfo);// 只有执行这句才会一级一级获取各种列表

		registerMyReceiver();
		startUpdatAndGetMsgService();
		// startActivity(new Intent(this,HistoryActivity.class));
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
	 * 初始化获取各种列表的服务类
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
					// 如果没有二级菜单，就给一个默认的二级菜单SeconMenu("全部",null,null);
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
						// 初始化二级菜单列表后，加载节目列表
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
					mGridVodShow.setSelection(0);
					mGridVodShow.requestFocus();
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
				logger.i("=====进入限制区=====");
				final PasswordDAO passwordDao = new PasswordDAO(getApplicationContext());
				// 最后一个为限制级片区，需加密
				// check 是否加密
				if (passwordDao.isTableNull()) {
					logger.i("=====检测到限制区未设置密码=====");
					setPassword(passwordDao, view, position);
				} else {
					logger.i("=====检测到限制区已经设置密码=====");
					// 输入密码
					final PasswordDialog pd = PasswordDialog.createDialog(HomeActivity.this);
					pd.setTitle(R.string.input_password);
					pd.hidePassword02().setPositiveListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							boolean isPass = passwordDao.isExist(pd.getPassword01());
							if (isPass) {
								isPassEver = true;
								// 验证通过
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
				logger.i("=====其它区=====");
				setViewVisible(mGridSeconMenu, false);
				mGridVodShow.setVisibility(View.INVISIBLE);
				mPagePrompt.setVisibility(View.INVISIBLE);
				changeLeftMenu(position);
			}

		}
	}

	private void setPassword(final PasswordDAO passwordDao, final View view, final int position) {
		passwordDao.deleteAll();
		// 如果没有设置密码
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

	// 翻页滚动效果，但效果没达到预期，暂时停做
	private class OnGridVodItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			/*
			 * int smoothDistance = view.getMeasuredHeight() - 30;//
			 * V代表整个GridView,垂直滚动举例 int duration = 1000;// 移动延迟
			 * 
			 * int nextSelection = mGridVodShow.getSelectedItemPosition(); int
			 * tempPosition = mTotalItemCount - mTotalItemCount %
			 * VOD_GRID_COLUMN - VOD_GRID_COLUMN;// 倒数第三行最后一个的位置
			 * 
			 * // 当滚动到第一页或者最后一页的时候就不滚动了 if (mCurrentSelection + VOD_GRID_COLUMN
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
	 * 修改View的显示状态
	 * 
	 * @param visible
	 *            true--显示 false--不显示
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
