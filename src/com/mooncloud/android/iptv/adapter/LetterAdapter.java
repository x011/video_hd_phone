package com.mooncloud.android.iptv.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ev.android.evodshd.R;

import android.widget.BaseAdapter;

public class LetterAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<String> letterList;

	public LetterAdapter(Context context, List<String> list) {
		letterList = list;
		inflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return letterList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return letterList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.letter_grid_item, null);
			holder.text = (TextView) convertView.findViewById(R.id.le_tview);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.text.setText(letterList.get(position));
		return convertView;
	}

	class Holder {
		TextView text;
	}

}
