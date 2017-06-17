package com.mooncloud.android.iptv.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseAdapter<T> extends android.widget.BaseAdapter {

	protected List<T>mList;
	protected LayoutInflater mInflater;
	protected Context mContext;
	
	public BaseAdapter(Context context,List<T> list){
		mContext=context;
		mInflater=LayoutInflater.from(mContext);
		mList=list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
}
