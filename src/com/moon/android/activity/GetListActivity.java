package com.moon.android.activity;

import com.ev.android.evodshd.R;
import com.moon.android.moonplayer.service.ListCacheService;

import android.app.Activity;
import android.os.Bundle;

public class GetListActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_getlist);
		new ListCacheService(GetListActivity.this);
	}
}
