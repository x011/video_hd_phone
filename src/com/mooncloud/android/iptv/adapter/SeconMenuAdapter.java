package com.mooncloud.android.iptv.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ev.android.evodshd.R;
import com.moon.android.model.SeconMenu;

public class SeconMenuAdapter extends BaseAdapter<SeconMenu> {

	private int mSelected=-1;
	
	public SeconMenuAdapter(Context context, List<SeconMenu> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=mInflater.inflate(R.layout.item_secon_menu, null);
			initHolder(holder,convertView);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		initHolderData(convertView,holder,position);
		return convertView;
	}
	
	public void notifyDataSetChanged(int selected) {
		mSelected=selected;
		super.notifyDataSetChanged();
	}

	private void initHolderData(View view, ViewHolder holder, int position) {
		SeconMenu seconMenu = mList.get(position);
		holder.menuName.setText(seconMenu.getClassify());
		if(mSelected==position){
			view.setBackgroundResource(R.drawable.secon_menu_focus);
			holder.menuName.setTextColor(Color.parseColor("#F1F1F1"));
			TextPaint tp = holder.menuName.getPaint(); //这两行字体加粗
			tp.setFakeBoldText(true); 
		}else{
			view.setBackgroundResource(R.drawable.transport);
			TextPaint tp = holder.menuName.getPaint(); 
			tp.setFakeBoldText(false); //这两行字体不加粗
			holder.menuName.setTextColor(Color.parseColor("#707178"));
		}
	}

	private void initHolder(ViewHolder holder, View view) {
		holder.menuName=(TextView) view.findViewById(R.id.secon_menu_text);
	}

	static class ViewHolder{
		TextView menuName;
	}
}
