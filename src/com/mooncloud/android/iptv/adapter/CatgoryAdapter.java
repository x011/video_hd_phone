package com.mooncloud.android.iptv.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ev.android.evodshd.plus.R;
import com.moonclound.android.iptv.util.VodCategory;

public class CatgoryAdapter extends BaseAdapter{

	private LayoutInflater mInfalter;
	private List<VodCategory> mListCat;
	private Context mContext;
	public CatgoryAdapter(Context context,List<VodCategory> list){
		mInfalter = LayoutInflater.from(context);
		mListCat = list;
		mContext = context;
	}
	
	@Override
	public int getCount() {
		return mListCat.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mListCat.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		Holder holder = null;
		if(null == convertView){
			holder = new Holder();
			convertView = mInfalter.inflate(R.layout.catgory_item, null);
			holder.catName = (TextView) convertView.findViewById(R.id.catgory_item_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		VodCategory catgory = mListCat.get(position);
		holder.catName.setText(catgory.getName());
//		convertView.setBackgroundResource(R.drawable.bg_navigation);
		return convertView;
	}
	
	class Holder{
		TextView catName;
	}

}
