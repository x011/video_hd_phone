package com.moonclound.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ev.android.evodshd.R;
import com.moon.android.model.VodProgramDetail;

public class ProgramDetailLayout extends LinearLayout {

	private Context mContext;
	
	private TextView mYear;
	private TextView mAuthor;
	private TextView mLeadingActor;
	private TextView mDrama;
	
	@SuppressLint("NewApi")
	public ProgramDetailLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext=context;
		initView();
	}

	public ProgramDetailLayout(Context context, AttributeSet attrs) {
		this(context, attrs , 0);
	}

	public ProgramDetailLayout(Context context) {
		this(context , null);
	}

	private void initView() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.linlayout_program_detail, this);
		mYear=(TextView)view.findViewById(R.id.drama_year);
		mAuthor=(TextView)view.findViewById(R.id.drama_author);
		mLeadingActor=(TextView)view.findViewById(R.id.drama_leading_actor);
		mDrama=(TextView)view.findViewById(R.id.drama_detail_info);
	}
	
	public void setData(VodProgramDetail programDetail){
		mYear.setText(programDetail.getYear());
		mAuthor.setText(programDetail.getRegisseur());
		mLeadingActor.setText(programDetail.getProtagonist());
		mDrama.setText(programDetail.getDrama());
	}
}
