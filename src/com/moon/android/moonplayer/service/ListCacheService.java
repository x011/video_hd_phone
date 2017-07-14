package com.moon.android.moonplayer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ev.android.evodshd.plus.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moon.android.activity.GetListActivity;
import com.moon.android.activity.HomeActivity;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.iptv.arb.film.MyApplication;
import com.moon.android.model.AllListModel;
import com.moon.android.model.Navigation;
import com.moon.android.model.VodProgram;
import com.moon.android.model.SeconMenu;
import com.moonclound.android.iptv.util.DbUtil;
import com.moonclound.android.iptv.util.Logger;

public class ListCacheService {
	private Logger log = Logger.getInstance();
	private String mPath = Configs.ALL_LIST_CACHE;
	public static List<AllListModel> AllListCache = null;
	public String tag = "LisetCacheService";
	public int ReTryNum = 5;// 重试取缓存总次数;
	public int TryNumEd = 0;// 已重试数量

	public DbUtil db;
	public GetListActivity mIndex;
	public LinearLayout mview;
	public LinearLayout mtryView, merrorView;
	public ImageView mPro;
	public Button mBt_retye, mBt_getcache, mBt_getcache2;

	public static Map<String, VodProgram> vProgramMap = new HashMap<String, VodProgram>();

	public ListCacheService(GetListActivity index) {

		mIndex = index;
		db = new DbUtil(MyApplication.getApplication());
		initWidget();
		start();
	}

	private void initWidget() {

		mview = (LinearLayout) mIndex.findViewById(R.id.index_loadlist_view);
		mtryView = (LinearLayout) mIndex.findViewById(R.id.getlist_try_container);
		merrorView = (LinearLayout) mIndex.findViewById(R.id.getlist_error_container);
		mBt_retye = (Button) mIndex.findViewById(R.id.getlist_retry_bt);
		mBt_getcache = (Button) mIndex.findViewById(R.id.getlist_gecache_bt);
		mBt_getcache2 = (Button) mIndex.findViewById(R.id.getlist_gecache_bt2);
		mPro = (ImageView) mIndex.findViewById(R.id.getlist_image);
		mPro.setImageResource(R.anim.load_animation2);
		AnimationDrawable animationDrawable = (AnimationDrawable) mPro.getDrawable();
		animationDrawable.start();
		mBt_retye.setOnClickListener(retyeclick);
		mBt_getcache.setOnClickListener(mgetcachaclick);
		mBt_getcache2.setOnClickListener(mgetcachaclick);
	}

	public OnClickListener mgetcachaclick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			getCache();
		}

	};
	public OnClickListener retyeclick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			getFromNet();
		}

	};

	public void goHome() {

		DBUpdateList();// 执行list提取存储，以待查询

		mIndex.finish();
		mIndex.startActivity(new Intent(mIndex, HomeActivity.class));
	}

	private void showView(boolean isTry) {

		if (isTry) {
			mtryView.setVisibility(View.VISIBLE);
			merrorView.setVisibility(View.GONE);
		} else {

			mtryView.setVisibility(View.GONE);
			merrorView.setVisibility(View.VISIBLE);
			mBt_retye.requestFocus();
			Toast.makeText(mIndex, "pass001", Toast.LENGTH_LONG).show();
			getCache();
		}

	}

	private void showLoadListView() {

		mview.setVisibility(View.VISIBLE);
	}

	public static List<Navigation> GetNavigation() {
		if (AllListCache == null)
			return null;
		if (AllListCache.size() <= 0)
			return null;
		List<Navigation> re = new ArrayList<Navigation>();
		for (int i = 0; i < AllListCache.size(); i++) {
			AllListModel Item = AllListCache.get(i);
			re.add(new Navigation(Item.getCid(), Item.getName()));
		}
		if (re.size() > 0) {
			return re;
		} else {
			return null;
		}

	}

	public static List<SeconMenu> GetSecondMenuByCid(String Cid) {
		if (AllListCache == null)
			return null;
		if (AllListCache.size() <= 0)
			return null;
		// Log.d("---","传入ID："+Cid);
		for (int i = 0; i < AllListCache.size(); i++) {
			AllListModel Item = AllListCache.get(i);
			String ItemCid = Item.getCid();
			// Log.d("---","当前ID："+ItemCid);
			if (ItemCid.equals(Cid)) {
				return Item.getSubmenu();
			}
		}
		return null;
	}

	// public static List<VodProgram> GetVodPro(String Cid,SeconMenu seconMenu){
	// GetSecondMenuByCid(Cid);
	// return null;
	// }
	private void getCache() {
		String Alljson = db.GetAllList();
		if (Alljson != null) {

			String gson = Alljson;
			Log.d("alllistCache", gson);
			try {
				// String json=new MyDecode().getjson(gson);
				String json = gson;
				AllListCache = new Gson().fromJson(json, new TypeToken<List<AllListModel>>() {
				}.getType());
				goHome();
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else {
			Toast.makeText(mIndex, mIndex.getResources().getString(R.string.load_list_info), Toast.LENGTH_LONG).show();
		}
	}

	private void start() {
		// TODO Auto-generated method stub
		// showLoadListView();
		getFromNet();
		Log.d(tag, "开启获取所有列表缓存");

	}

	private void getFromNet() {
		// TODO Auto-generated method stub
		showView(true);
		FinalHttp finalHttp = new FinalHttp();
		Log.d(tag, "第" + TryNumEd + "次获取总列表地址：" + Configs.URL.getListCache());
		AjaxParams params = new AjaxParams();
		params.put("appid", Configs.URL.APP_ID);
		params.put("mac", Configs.URL.MAC);
		finalHttp.post(Configs.URL.getListCache(),params, new AjaxCallBack<String>() {

			@Override
			public void onSuccess(String t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				Log.d(tag, "网络获取节目单:" + t);
				try {
					// String json=new MyDecode().getjson(t);
					String json = t;
					AllListCache = new Gson().fromJson(json, new TypeToken<List<AllListModel>>() {
					}.getType());
					Log.d(tag, "左侧菜单数量:" + AllListCache.size());
					if (AllListCache.size() > 0) {
						// ProgramCache.save(mPath, t);
						db.SaveAllList(t);
						goHome();
						// mHandel.sendEmptyMessage(Configs.CACHE_LIST_NEW);
					}

				} catch (Exception e) {

					showView(false);
					// getFromNet();

				}

			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				showView(false);

			}
		});

	}

	// 新开线程，获取全部影视内容，并且更新数据库
	private void DBUpdateList() {
		Thread newThread = null;

		try {
			newThread = new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("Thread run start...");
					if (AllListCache == null)
						return;
					if (AllListCache.size() <= 0)
						return;
					for (int i = 0; i < AllListCache.size(); i++) {
						final AllListModel listModel = AllListCache.get(i);
						
						if (i == AllListCache.size() - 1 && Configs.isLastNeedPassword) {
							new Thread() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									for (int j = 0; j < listModel.submenu.size(); j++) {
										SeconMenu sMenu = listModel.submenu.get(j);
										if(Configs.debug){
											System.out.println(sMenu.content.size()+"===================");
										}
										for (int k = 0; k < sMenu.content.size(); k++) {
//											System.out.println(k);
											VodProgram vProgram = sMenu.content.get(k);
											vProgramMap.put(vProgram.getSid(), vProgram);
											// 写入数据库
											db.SaveProgramTag(vProgram.getSid(), vProgram.getKey(), Configs.ISDSPWD);
										}
									}
								}
							}.start();
						} else {
							new Thread() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									for (int j = 0; j < listModel.submenu.size(); j++) {
										SeconMenu sMenu = listModel.submenu.get(j);
										for (int k = 0; k < sMenu.content.size(); k++) {
											VodProgram vProgram = sMenu.content.get(k);
											vProgramMap.put(vProgram.getSid(), vProgram);
											// 写入数据库
											db.SaveProgramTag(vProgram.getSid(), vProgram.getKey(), Configs.NORDSPWD);
										}
									}
								}
							}.start();
						}

					}
				}
			});
			newThread.start();
		} catch (Exception e) {
			// TODO: handle exception
			if (newThread != null)
				newThread.interrupt();
		}
	}

	public static Map<String, VodProgram> getAllProgramMap() {
		return vProgramMap;
	}
}
