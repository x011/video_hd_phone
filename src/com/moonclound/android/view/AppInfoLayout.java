package com.moonclound.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ev.android.evodshd.plus.R;
import com.moon.android.model.VodProgramDetail;
import com.moonclound.android.iptv.util.MACUtils;
import com.moonclound.android.iptv.util.Tools;

public class AppInfoLayout extends LinearLayout {

	private Context mContext;
	
	private TextView mTv_Ver,mTv_Mac;

	
	@SuppressLint("NewApi")
	public AppInfoLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;
		initView();
	}

	public AppInfoLayout(Context context, AttributeSet attrs) {
		this(context, attrs , 0);
	}

	public AppInfoLayout(Context context) {
		this(context , null);
	}

	private void initView() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.appinfo_view, this);
		mTv_Ver=(TextView) view.findViewById(R.id.appinfo_text_ver);
		mTv_Mac=(TextView) view.findViewById(R.id.appinfo_text_mac);
		mTv_Ver.setText("Version:"+Tools.getVerName(mContext));
		mTv_Mac.setText("Mac:"+MACUtils.getMac());
	}
	
}
