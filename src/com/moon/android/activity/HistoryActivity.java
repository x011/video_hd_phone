package com.moon.android.activity;

import java.util.List;

import com.ev.android.evodshd.plus.R;
import com.ev.player.history.HistoryDBHelper;
import com.ev.player.model.Model_history;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moon.android.iptv.arb.film.Configs;
import com.moon.android.model.VodProgram;
import com.mooncloud.android.iptv.adapter.HistoryAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;










import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class HistoryActivity extends Activity {
	public ListView mlistView;
	public List<Model_history> mlist;
	public HistoryAdapter madapter;
	private HistoryDBHelper db;
	private ImageView mImg;
	private DisplayImageOptions options;
	public LinearLayout mLine_home,mLine_hot;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		initdata();
		initwidget();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		upList();
		super.onResume();
	}
    private void upList(){
    	mlist=db.GetHistory();
    	madapter=new HistoryAdapter(HistoryActivity.this, mlist);
    	mlistView.setAdapter(madapter);
    }
	private void initdata() {
		// TODO Auto-generated method stub
		db=new HistoryDBHelper(HistoryActivity.this);
		mlist=db.GetHistory();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.pic_loading)
		.showImageForEmptyUri(R.drawable.pic_loading)
		.showImageOnFail(R.drawable.pic_loading).cacheInMemory(true)
		.cacheOnDisk(true).considerExifParams(true)
		// .displayer(new RoundedBitmapDisplayer(20))
		.build();
		
	}
	private void initwidget() {
		// TODO Auto-generated method stub
		
		mlistView=(ListView) findViewById(R.id.history_list);
		madapter=new HistoryAdapter(HistoryActivity.this, mlist);
		mImg=(ImageView) findViewById(R.id.history_img);
		mlistView.setAdapter(madapter);
		mlistView.setOnItemSelectedListener(mlistSelect);
		mlistView.setOnItemClickListener(mlistClick);
		mLine_home=(LinearLayout) findViewById(R.id.his_homeLine);
		mLine_home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mLine_hot=(LinearLayout) findViewById(R.id.his_hotLine);
		mLine_hot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(HistoryActivity.this,HotActivity.class));
				finish();
			}
		});
	}
	public OnItemClickListener mlistClick=new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			 Model_history item=mlist.get(arg2);
				String vodjson=item.getJson();
				try {
					Gson g=new Gson();
					VodProgram voditem=new VodProgram();
					voditem=g.fromJson(vodjson,new TypeToken<VodProgram>(){}.getType());
					Intent intent = new Intent();
				
					intent.putExtra(Configs.INTENT_PARAM_2, item.getCid());
				
					intent.setClass(HistoryActivity.this, VodsActivity.class);
					intent.putExtra(Configs.INTENT_PARAM, voditem);
					intent.putExtra("isAuto", "1");
					HistoryActivity.this.startActivity(intent);
				} catch (Exception e) {
					// TODO: handle exception
				}
		}
		
	};
	public OnItemSelectedListener mlistSelect=new OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Model_history item=mlist.get(arg2);
			
			String vodjson=item.getJson();
			try {
				Gson g=new Gson();
				VodProgram voditem=new VodProgram();
				voditem=g.fromJson(vodjson,new TypeToken<VodProgram>(){}.getType());
				ImageLoader.getInstance().displayImage(voditem.getLogo(), mImg,
						options);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
}
