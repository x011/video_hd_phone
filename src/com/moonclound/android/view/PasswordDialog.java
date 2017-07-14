package com.moonclound.android.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ev.android.evodshd.plus.R;

public class PasswordDialog extends Dialog {

	private static PasswordDialog customDialog = null;

	public PasswordDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public PasswordDialog(Context context, int theme) {
		super(context, theme);
	}

	public PasswordDialog(Context context) {
		super(context);
	}

	public static PasswordDialog createDialog(Context context) {
		customDialog = new PasswordDialog(context, R.style.CustomProgressDialog);
		customDialog.setContentView(R.layout.custom_dialog);
		Button b = (Button) customDialog.findViewById(R.id.cancel);
		b.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				customDialog.dismiss();
			}
		});
		return customDialog;
	}
	
	public PasswordDialog setTitle(String titleS) {
		TextView title = (TextView) customDialog.findViewById(R.id.title);
		title.setText(titleS);
		return customDialog;
	}
	
	public void setTitle(int titleRes) {
		TextView title = (TextView) customDialog.findViewById(R.id.title);
		title.setText(titleRes);
	}
	
	public PasswordDialog hidePassword02() {
		EditText p2 = (EditText) customDialog.findViewById(R.id.password_02);
		p2.setVisibility(View.GONE);
		return customDialog;
	}
	
	public PasswordDialog hideModify() {
		Button b = (Button) customDialog.findViewById(R.id.modify);
		b.setVisibility(View.GONE);
		return customDialog;
	}
	
	public PasswordDialog setPositiveListener(android.view.View.OnClickListener cl){
		Button b = (Button) customDialog.findViewById(R.id.submit);
		b.setOnClickListener(cl);
		return customDialog;
	} 
	
	public PasswordDialog setNegativeListener(android.view.View.OnClickListener cl){
		Button b = (Button) customDialog.findViewById(R.id.cancel);
		b.setOnClickListener(cl);
		return customDialog;
	} 

	public PasswordDialog setModifyListener(android.view.View.OnClickListener cl){
		Button b = (Button) customDialog.findViewById(R.id.modify);
		b.setOnClickListener(cl);
		return customDialog;
	} 
	
	public String getPassword01(){
		EditText e = (EditText) customDialog.findViewById(R.id.password_01);
		return e.getText().toString();
	}
	
	public String getPassword02(){
		EditText e = (EditText) customDialog.findViewById(R.id.password_02);
		return e.getText().toString();
	}
}