package com.mooncloud.android.iptv.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ev.android.evodshd.R;
import com.ev.player.model.Model_history;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moon.android.model.VodProgram;
import com.nostra13.universalimageloader.core.DisplayImageOptions;




@SuppressLint("ResourceAsColor")
public class HistoryAdapter extends BaseAdapter {

	private Context mContext;
	private DisplayImageOptions mOptions;
	private List<Model_history> mlist;
	 private int clickpos=-1;
	public HistoryAdapter(Context context, List<Model_history> list) {
		mlist = list;
		mContext=context;
		mOptions = new DisplayImageOptions.Builder()
//		.showImageOnLoading(R.drawable.pic_loading)
//		.showImageForEmptyUri(R.drawable.pic_loading)
//		.showImageOnFail(R.drawable.pic_loading).cacheInMemory(true)
		.cacheOnDisk(true).considerExifParams(true)
		// .displayer(new RoundedBitmapDisplayer(20))
		.build();

	}
	public void clickChang(int pos){
    	this.clickpos=pos;
    	notifyDataSetChanged();
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mlist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Holder holder = null;
		Model_history item = mlist.get(position);
		if (null == convertView) {
			holder = new Holder();
		
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_history, null);
			//holder.image = (ImageView) convertView.findViewById(R.id.index_img);
			holder.content=(TextView) convertView.findViewById(R.id.history_item_content);
//			holder.w=(TextView) convertView.findViewById();
//			holder.num=(TextView) convertView.findViewById(R.id.list_item_num);
//			holder.logo=(ImageView) convertView.findViewById(R.id.left_menu_ico);
			convertView.setTag(holder);

			//R.
		} else {
			holder = (Holder) convertView.getTag();
		}
		String vodjson=item.getJson();
		try {
			Gson g=new Gson();
			VodProgram voditem=new VodProgram();
			voditem=g.fromJson(vodjson,new TypeToken<VodProgram>(){}.getType());
			String text=(position+1)+". 《"+voditem.getName()+"》 观看到 第"+(Integer.parseInt(item.getPlayIndex())+1)+"集  "+getTime(Integer.parseInt(item.getPlayPos()));
			holder.content.setText(text);
		} catch (Exception e) {
			// TODO: handle exception
		}

//		if(position==clickpos){
//			holder.date.setTextColor(mContext.getResources().getColor(R.color.yellow_half));
//			holder.w.setTextColor(mContext.getResources().getColor(R.color.yellow_half));
//		}else{
//			holder.date.setTextColor(mContext.getResources().getColor(R.color.white));
//			holder.w.setTextColor(mContext.getResources().getColor(R.color.white));
////			 holder.date.setTextColor((ColorStateList) mContext.getResources().getColorStateList(R.drawable.left_color_selector) );
////			 holder.w.setTextColor((ColorStateList) mContext.getResources().getColorStateList(R.drawable.left_color_selector) );
//		}
//		holder.date.setText(item.getDate());
//		holder.w.setText(item.getW());
		return convertView;

	}
	private static String getTime(int millisecond) {
		int j = millisecond / 1000;
		int k = j / 60;
		int m = k / 60;
		int n = j % 60;
		int i1 = k % 60;
		Object[] arrayOfObject = new Object[3];
		arrayOfObject[0] = Integer.valueOf(m);
		arrayOfObject[1] = Integer.valueOf(i1);
		arrayOfObject[2] = Integer.valueOf(n);
		return String.format("%02d:%02d:%02d", arrayOfObject);
	}
	class Holder {
	   
		TextView content,w;
		ImageView logo;
	}



}
