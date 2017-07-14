package com.mooncloud.android.iptv.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ev.android.evodshd.plus.R;
import com.moon.android.model.Drama;

public class VideoAdapter extends BaseAdapter{

	private LayoutInflater mInfalter;
	private List<Drama> mListVodVideo;
//	private FinalBitmap mFinalBitmap;
	public VideoAdapter(Context context,List<Drama> list){
		mInfalter = LayoutInflater.from(context);
		mListVodVideo = list;
//		mFinalBitmap = fb;
	}
	
	@Override
	public int getCount() {
		return mListVodVideo.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mListVodVideo.get(arg0);
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
			convertView = mInfalter.inflate(R.layout.vod_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.catgory_item_text);
//			holder.name = (TextView) convertView.findViewById(R.id.program_item_name);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		Drama program = mListVodVideo.get(position);
		holder.name.setText(program.getName());
//		mFinalBitmap.display(holder.image, program.getPic());
		convertView.setBackgroundResource(R.drawable.bg_vod);
		return convertView;
	}
	
	class Holder{
//		TextView image;
		TextView name;
	}

}
