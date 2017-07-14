package com.moon.android.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.ev.android.evodshd.plus.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.model.VodProgram;
import com.moonclound.android.iptv.util.MyDecode;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HotActivity extends Activity {
	public ImageView mImg;
	private DisplayImageOptions mOptions;
	public List<ImageView> mImgList;
	public List<TextView> mTvList;
	public List<LinearLayout> mLineList;
	public int[] mImgIdArr={R.id.hot_img1,R.id.hot_img2,R.id.hot_img3,R.id.hot_img4,R.id.hot_img5,R.id.hot_img6,R.id.hot_img7};
	public int[] mTvIdArr={R.id.hot_text1,R.id.hot_text2,R.id.hot_text3,R.id.hot_text4,R.id.hot_text5,R.id.hot_text6,R.id.hot_text7};
	public int[] mLineIdArr={R.id.hot_lin1,R.id.hot_lin2,R.id.hot_lin3,R.id.hot_lin4,R.id.hot_lin5,R.id.hot_lin6,R.id.hot_lin7};
	public List<VodProgram> mVodList=new ArrayList<VodProgram>();
	public LinearLayout mLine_home,mLine_history;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hot);
	
		mOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.pic_loading)
		.showImageForEmptyUri(R.drawable.pic_loading)
		.showImageOnFail(R.drawable.pic_loading).cacheInMemory(true)
		.cacheOnDisk(true).considerExifParams(true)
		
		// .displayer(new RoundedBitmapDisplayer(20))
		.build();
		initwidget();
		initList();
	}
	private void initList() {
		// TODO Auto-generated method stub
		FinalHttp fn=new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put("appid", Configs.URL.APP_ID);
		params.put("mac", Configs.URL.MAC);
		fn.post(Configs.URL.getHot(), params, new AjaxCallBack<Object>() {

			@Override
			public void onSuccess(Object t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				try {
					String json=new MyDecode().getjson(t.toString());
					Gson g=new Gson();
					mVodList=g.fromJson(json, new TypeToken<List<VodProgram>>(){}.getType());
					showList();
					Log.d("hot",json);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}

		
			  
		});
	}
	private void showList() {
		// TODO Auto-generated method stub
		for(int i=0;i<mVodList.size();i++){
			ImageLoader.getInstance().displayImage(mVodList.get(i).getH_logo(),mImgList.get(i),mOptions);
			mTvList.get(i).setText(mVodList.get(i).getName());
		}
	}
	@SuppressLint("NewApi")
	private void initwidget() {
		// TODO Auto-generated method stub
		//mImg=(ImageView) findViewById(R.id.hot_img1);
		//ImageLoader.getInstance().displayImage("http://pic.quliebiao.com:9011/Uploads/videoLogo/201607/5795c645c9311.jpg", mImg,mOptions);
		mImgList=new ArrayList<ImageView>();
		mTvList=new ArrayList<TextView>();
		mLineList=new ArrayList<LinearLayout>();
		for(int i=0;i<mImgIdArr.length;i++){
			ImageView img=(ImageView) findViewById(mImgIdArr[i]);
			img.setBackground(getResources().getDrawable(R.drawable.pic_loading));
			mImgList.add(img);
//			 Log.d("imgid",mImgIdArr[i]+"");
			
//			mImgList.add((ImageView)findViewById(mImgIdArr[i]));
		}
		for(int i=0;i<mTvIdArr.length;i++){
			mTvList.add((TextView)findViewById(mTvIdArr[i]));
		}
		for(int i=0;i<mLineIdArr.length;i++){
			mLineList.add((LinearLayout)findViewById(mLineIdArr[i]));
		}
		for(int i=0;i<mLineList.size();i++){
			mLineList.get(i).setOnClickListener(mLineClick);
		}
		mLine_home=(LinearLayout) findViewById(R.id.hot_homeLine);
		mLine_home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mLine_history=(LinearLayout) findViewById(R.id.hot_hisLine);
		mLine_history.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 finish();
				 startActivity(new Intent(HotActivity.this,HistoryActivity.class));
			}
		});
		
	}
	private OnClickListener mLineClick=new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			for(int i=0;i<mLineList.size();i++){
				if(arg0.getId()==mLineList.get(i).getId()){
					try {
						Intent intent = new Intent();
						intent.putExtra(Configs.INTENT_PARAM_2, 0);
						intent.setClass(HotActivity.this, VodsActivity.class);
						intent.putExtra(Configs.INTENT_PARAM, mVodList.get(i));
						HotActivity.this.startActivity(intent);
					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}
//				mLineList.get(i).setOnClickListener(mLineClick);
			}
		}
		
	};
}
