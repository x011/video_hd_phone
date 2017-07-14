package com.mooncloud.android.iptv.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudtv.sdk.utils.Logger;
import com.ev.android.evodshd.plus.R;
import com.moon.android.model.VodProgram;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProgramSearchAdapter extends BaseAdapter<VodProgram>{

private DisplayImageOptions mOptions;
	
	public ProgramSearchAdapter(Context context, List<VodProgram> list) {
		super(context, list);
		initDisplayImageOptions();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.program_search_item, null);
			initHolder(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		initHolderData(holder,convertView, position);

		return convertView;
	}

	@SuppressLint("NewApi")
	private void initHolderData(ViewHolder holder, View view, int position) {
		VodProgram program = mList.get(position);
		holder.name.setText(program.getName());
		Logger.i("pic URL " + program.getLogo());
		
	
	
		try {
			if(program.getStatus().equals("0")){
				holder.status.setVisibility(View.GONE);
			}
			if(program.getStatus().equals("1")){
				holder.status.setBackground(mContext.getResources().getDrawable(R.drawable.video_new));
				holder.status.setVisibility(View.VISIBLE);
				
			}
			if(program.getStatus().equals("2")){
				holder.status.setBackground(mContext.getResources().getDrawable(R.drawable.video_ing));
				holder.status.setVisibility(View.VISIBLE);
				
			}
			if(program.getStatus().equals("3")){
				holder.status.setBackground(mContext.getResources().getDrawable(R.drawable.video_end));
				holder.status.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		ImageLoader.getInstance().displayImage(program.getLogo(), holder.image,mOptions);
		
	}

	private void initHolder(ViewHolder holder, View view) {
		holder.image = (ImageView) view.findViewById(R.id.program_search_item_image);
		holder.name = (TextView) view.findViewById(R.id.program_search_item_name);
		holder.status=(ImageView) view.findViewById(R.id.program_search_img_status);
	}

	static class ViewHolder {
		ImageView image;
		TextView name;
		ImageView status;
	}
 
	private void initDisplayImageOptions() {
		mOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.pic_loading)
				.showImageForEmptyUri(R.drawable.pic_loading)
				.showImageOnFail(R.drawable.pic_loading).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				
				// .displayer(new RoundedBitmapDisplayer(20))
				.build();
	}
}