package com.mooncloud.android.iptv.adapter;

import java.util.List;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ev.android.evodshd.plus.R;
import com.moon.android.model.Navigation;

public class LeftMenuAdapter extends BaseAdapter<Navigation> {

	private int mSelected=-1;
	
	public LeftMenuAdapter(Context context, List<Navigation> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=mInflater.inflate(R.layout.catgory_item, null);
			initHolder(holder,convertView);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		initHolderData(holder,position);
		return convertView;
	}
	
	@SuppressLint("NewApi")
	private void initHolderData(ViewHolder holder, int position) {
		Navigation navigation = mList.get(position);
		holder.menuName.setText(navigation.getName());
		if(mSelected==position){
			
			holder.menuName.setTextSize(35f);
			
			ObjectAnimator animX=ObjectAnimator.ofFloat(holder.menuName, "scaleX", 1f,1.01f);
			ObjectAnimator animY=ObjectAnimator.ofFloat(holder.menuName, "scaleY", 1f,1.01f);
			AnimatorSet set=new AnimatorSet();
			
			set.playTogether(animX,animY);
			set.start();
			
			holder.menuName.setTextColor(Color.parseColor("#F1F1F1"));
		}else{
			holder.menuName.setTextSize(30f);
			holder.menuName.setTextColor(Color.parseColor("#707178"));
		}
	}

	private void initHolder(ViewHolder holder, View view) {
		holder.menuName=(TextView) view.findViewById(R.id.catgory_item_text);
	}
	
	public void notifyDataSetChanged(int selected) {
		mSelected=selected;
		super.notifyDataSetChanged();
	}



	static class ViewHolder{
		TextView menuName;
	}
}
