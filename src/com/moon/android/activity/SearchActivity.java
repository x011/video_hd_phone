package com.moon.android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ev.android.evodshd.plus.R;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.model.VodProgram;
import com.moon.android.moonplayer.service.ListCacheService;
import com.mooncloud.android.iptv.adapter.LetterAdapter;
import com.mooncloud.android.iptv.adapter.ProgramSearchAdapter;
import com.mooncloud.android.iptv.database.PasswordDAO;
import com.moonclound.android.iptv.util.DbUtil;
import com.moonclound.android.iptv.util.Logger;
import com.moonclound.android.iptv.util.StringUtil;
import com.moonclound.android.view.CustomToast;
import com.moonclound.android.view.PasswordDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchActivity extends Activity {

	private Logger logger = Logger.getInstance();
	private static final int TOAST_TEXT_SIZE = 24;
	public Context mContext;
	TextView tv_page;
	EditText enter_txt;
	private GridView letterGv, searchGV;
	List<String> letterList;
	LetterAdapter lAdapter;
	public static final int VOD_SEARCH_GRID_COLUMN = 4;
	private ProgramSearchAdapter mProgramAdapter;
	List<VodProgram> mVodProgramList = new ArrayList<VodProgram>();
	private int mTotalItemCount = 0;
	private int mCurrentSelection = 0;
	private String DEFAULT_TXT = "";
	LinearLayout search_nullres;
	private DbUtil db;
	Map<String, VodProgram> vodProgramMap;
	LinearLayout linear_back, linear_space, linear_clear;
	ImageView image_back, image_space, image_clear;
	TextView txt_back, txt_space, txt_clear;
	TextView result_search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		mContext = SearchActivity.this;

		DEFAULT_TXT = this.getResources().getString(R.string.search_txt);
		db = new DbUtil(mContext);
		vodProgramMap = ListCacheService.getAllProgramMap();

		initLetterList();
		initwidget();
		initData();
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 555:
				notifyDataByTag(enter_txt.getText().toString());
				break;

			default:
				break;
			}
		};
	};
	
	private void initData() {
		// TODO Auto-generated method stub
		lAdapter = new LetterAdapter(mContext, letterList);
		letterGv.setAdapter(lAdapter);
		mProgramAdapter = new ProgramSearchAdapter(mContext, mVodProgramList);
		searchGV.setAdapter(mProgramAdapter);

		letterGv.requestFocus();
		letterGv.setSelection(0);

	}

	private void initwidget() {
		// TODO Auto-generated method stub

		// 搜索输入框
		enter_txt = (EditText) findViewById(R.id.enter_txt);
		enter_txt.setOnFocusChangeListener(onFocusChangeListener);
		enter_txt.addTextChangedListener(textWatcher);
		// 三个按钮
		linear_back = (LinearLayout) findViewById(R.id.linear_back);
		linear_space = (LinearLayout) findViewById(R.id.linear_space);
		linear_clear = (LinearLayout) findViewById(R.id.linear_clear);
		linear_back.setOnFocusChangeListener(linearOnFocusChangeListener);
		linear_space.setOnFocusChangeListener(linearOnFocusChangeListener);
		linear_clear.setOnFocusChangeListener(linearOnFocusChangeListener);
		linear_back.setOnClickListener(linearOnClickListener);
		linear_space.setOnClickListener(linearOnClickListener);
		linear_clear.setOnClickListener(linearOnClickListener);
		image_back = (ImageView) findViewById(R.id.image_back);
		image_space = (ImageView) findViewById(R.id.image_space);
		image_clear = (ImageView) findViewById(R.id.image_clear);
		txt_back = (TextView) findViewById(R.id.txt_back);
		txt_space = (TextView) findViewById(R.id.txt_space);
		txt_clear = (TextView) findViewById(R.id.txt_clear);
		// 字母控件
		letterGv = (GridView) findViewById(R.id.letter_grid);
		letterGv.setOnItemClickListener(onItemClickListener);
		// txt
		result_search = (TextView) findViewById(R.id.result_search);
		// 搜索页码
		tv_page = (TextView) findViewById(R.id.tv_page);
		// 搜索结果控件
		searchGV = (GridView) findViewById(R.id.search_grid);
		searchGV.setNumColumns(VOD_SEARCH_GRID_COLUMN);
		searchGV.setOnKeyListener(new OnGridVodKeyListener());
		searchGV.setOnItemClickListener(new OnGridVodItemClickListener());
		searchGV.setOnItemSelectedListener(new OnGridVodItemSelectedListener());

		search_nullres = (LinearLayout) findViewById(R.id.search_nullres);

	}

	private OnClickListener linearOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.linear_back:
				clearDefaultTxt();
				// int index = enter_txt.getSelectionStart();// 获取Edittext光标所在位置
				// String str = enter_txt.getText().toString();
				// if (!str.equals("")) {// 判断输入框不为空，执行删除
				// enter_txt.getText().delete(index - 1, index);
				// }
				int index = enter_txt.length();
				if (index < 1)
					return;
				enter_txt.getText().delete(index - 1, index);
				break;
			case R.id.linear_space:
				clearDefaultTxt();
				enter_txt.append(" ");
				break;
			case R.id.linear_clear:
				clearDefaultTxt();
				enter_txt.setText("");
				break;
			default:
				break;
			}
		}
	};
	private OnFocusChangeListener linearOnFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean bool) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.linear_back:
				if (bool) {
					txt_back.setTextAppearance(mContext, R.style.text_20_ffffff);
					image_back.setImageResource(R.drawable.icon_back_selected);
				} else {
					txt_back.setTextAppearance(mContext, R.style.text_20_5fb7e0);
					image_back.setImageResource(R.drawable.icon_back);
				}
				break;

			case R.id.linear_space:
				if (bool) {
					txt_space.setTextAppearance(mContext, R.style.text_20_ffffff);
					image_space.setImageResource(R.drawable.icon_space_selected);
				} else {
					txt_space.setTextAppearance(mContext, R.style.text_20_5fb7e0);
					image_space.setImageResource(R.drawable.icon_space);
				}
				break;
			case R.id.linear_clear:
				if (bool) {
					txt_clear.setTextAppearance(mContext, R.style.text_20_ffffff);
					image_clear.setImageResource(R.drawable.icon_clear_selected);
				} else {
					txt_clear.setTextAppearance(mContext, R.style.text_20_5fb7e0);
					image_clear.setImageResource(R.drawable.icon_clear);
				}
				break;
			default:
				break;
			}
		}
	};

	private class OnGridVodItemSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			resetPagePrompt(position, mTotalItemCount);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}

	private class OnGridVodItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
			final VodProgram vodProgram = mVodProgramList.get(position);

//			System.out.println("vodProgram.isprotect = " + vodProgram.getIsdspwd());
			if (vodProgram.getIsdspwd().equals(Configs.ISDSPWD)) {
				logger.i("=====进入限制区=====");
				final PasswordDAO passwordDao = new PasswordDAO(getApplicationContext());
				// 最后一个为限制级片区，需加密
				// check 是否加密
				if (passwordDao.isTableNull()) {
					logger.i("=====检测到限制区未设置密码=====");
					setPassword(passwordDao);
				} else {
					logger.i("=====检测到限制区已经设置密码=====");
					// 输入密码
					final PasswordDialog pd = PasswordDialog.createDialog(mContext);
					pd.setTitle(R.string.input_password);
					pd.hidePassword02().setPositiveListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							boolean isPass = passwordDao.isExist(pd.getPassword01());
							if (isPass) {
								// 验证通过
								pd.dismiss();
								senIntent(vodProgram);
							} else {
								new CustomToast(mContext, getString(R.string.password_wrong), TOAST_TEXT_SIZE).show();
							}
						}
					}).setModifyListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							boolean isPass = passwordDao.isExist(pd.getPassword01());
							if (isPass) {
								pd.dismiss();
								setPassword(passwordDao);
							} else {
								new CustomToast(mContext, getString(R.string.password_wrong), TOAST_TEXT_SIZE).show();
							}
						}
					}).show();
				}
			} else {
				senIntent(vodProgram);
			}
		}

	}
	
	private void senIntent(VodProgram vodProgram){
		Intent intent = new Intent();
		intent.putExtra("isAuto", "0");
		intent.setClass(mContext, VodsActivity.class);
		intent.putExtra(Configs.INTENT_PARAM, vodProgram);
		mContext.startActivity(intent);
	}

	private void setPassword(final PasswordDAO passwordDao) {
		passwordDao.deleteAll();
		// 如果没有设置密码
		final PasswordDialog pd = PasswordDialog.createDialog(mContext);
		pd.setTitle(R.string.password_setting);
		pd.hideModify().setPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pd1 = pd.getPassword01();
				String pd2 = pd.getPassword02();
				if (!StringUtil.isBlank(pd1) && !StringUtil.isBlank(pd2)) {
					if (pd1.equals(pd2)) {
						passwordDao.insert(pd1);
						pd.dismiss();
					} else {
						new CustomToast(mContext, getString(R.string.password_not_match), TOAST_TEXT_SIZE).show();
					}
				} else {
					new CustomToast(mContext, getString(R.string.password_not_null), TOAST_TEXT_SIZE).show();
				}
			}
		}).show();
	}

	private class OnGridVodKeyListener implements OnKeyListener {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			mCurrentSelection = searchGV.getSelectedItemPosition();
			return false;
		}

	}

	private void resetPagePrompt(int mSelectedPosition, int mTotalItemCount) {
		tv_page.setText((mSelectedPosition + 1) + "/" + mTotalItemCount);
	}

	private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean bool) {
			// TODO Auto-generated method stub
			if (bool)
				clearDefaultTxt();
		}
	};
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
			// TODO Auto-generated method stub
			clearDefaultTxt();
			enter_txt.append(letterList.get(pos));

		}
	};

	private void clearDefaultTxt() {
		if (enter_txt.getText().toString().equals(DEFAULT_TXT)) {
			enter_txt.setText("");
			enter_txt.setTextColor(Color.parseColor("#ffffff"));
		}
	}

	private void showView(boolean isbool) {

		result_search.setText(R.string.resultsearch_txt);
		if (isbool) {
			searchGV.setVisibility(View.VISIBLE);
			search_nullres.setVisibility(View.GONE);
		} else {
			searchGV.setVisibility(View.GONE);
			search_nullres.setVisibility(View.VISIBLE);
		}

	}

	// 初始化搜索字符
	private void initLetterList() {
		// TODO Auto-generated method stub
		letterList = new ArrayList<String>();
		letterList.add("A");
		letterList.add("B");
		letterList.add("C");
		letterList.add("D");
		letterList.add("E");
		letterList.add("F");
		letterList.add("G");
		letterList.add("H");
		letterList.add("I");
		letterList.add("J");
		letterList.add("K");
		letterList.add("L");
		letterList.add("M");
		letterList.add("N");
		letterList.add("O");
		letterList.add("P");
		letterList.add("Q");
		letterList.add("R");
		letterList.add("S");
		letterList.add("T");
		letterList.add("U");
		letterList.add("V");
		letterList.add("W");
		letterList.add("X");
		letterList.add("Y");
		letterList.add("Z");
		letterList.add("0");
		letterList.add("1");
		letterList.add("2");
		letterList.add("3");
		letterList.add("4");
		letterList.add("5");
		letterList.add("6");
		letterList.add("7");
		letterList.add("8");
		letterList.add("9");
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
//			notifyDataByTag(enter_txt.getText().toString());
			mHandler.sendEmptyMessage(555);
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			// System.out.println("before "+enter_txt.getText().toString());
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			// System.out.println("cu "+enter_txt.getText().toString());
		}

	};

	// 模糊查询,刷新UI
	private void notifyDataByTag(String Tag) {
		mVodProgramList.clear();
		try {
			Map<String, String> map = db.GetProgramListByTags(Tag);// key：影片sid
																	// value：是否为限制级
			for (String key : map.keySet()) {
				if (vodProgramMap.containsKey(key)) {
					VodProgram vProggram = vodProgramMap.get(key);
					if (map.get(key).equals(Configs.ISDSPWD))
						vProggram.setIsdspwd(Configs.ISDSPWD);
					mVodProgramList.add(vProggram);
				}
			}
//			System.out.println("result size = "+mVodProgramList.size());
			if (mVodProgramList == null) {
				showView(false);
				return;
			}
			if (mVodProgramList.size() <= 0) {
				showView(false);
				return;
			}
			showView(true);
			mProgramAdapter.notifyDataSetChanged();
			mTotalItemCount = mProgramAdapter.getCount();
			mCurrentSelection = 0;
			resetPagePrompt(mCurrentSelection, mTotalItemCount);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
