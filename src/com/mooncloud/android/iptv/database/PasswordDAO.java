package com.mooncloud.android.iptv.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PasswordDAO {
	
	private DBHelper mDBHelper;
	private Context mContext;
	private SQLiteDatabase mSQLHelper;

	public PasswordDAO(Context context) {
		mContext = context;
		mDBHelper = new DBHelper(mContext);
	}
	
	public boolean isTableNull(){
		Cursor c = null;
		try {
			final SQLiteDatabase db = mDBHelper.getReadableDatabase();
			c = db.query(DBHelper.TABLE_VOD_PASSWORD, null, null, null, null, null,
					null);
			if(c.getCount() > 0) return false;
		} catch (Exception e) {
		}finally{
			if(null != c) c.close();
		}
		return true;
	}
	
	public boolean isExist(String password) {
		Cursor c = null;
		try {
			final SQLiteDatabase db = mDBHelper.getReadableDatabase();
			c = db.query(DBHelper.TABLE_VOD_PASSWORD, null, "password = ?",new String[]{password}, null, null,
					null);
			if(c.getCount() > 0) return true;
		} catch (Exception e) {
		}finally{
			if(null != c) c.close();
		}
		return false;
	}

	public void insert(String password) {
		String insertSql = "insert into " + DBHelper.TABLE_VOD_PASSWORD + "(password)" 
				+ " values('"+ password + "')";
		mSQLHelper = mDBHelper.getWritableDatabase();
		mSQLHelper.execSQL(insertSql);
		mSQLHelper.close();
		mDBHelper.close();
	}
	
	public void deleteAll(){
		String sql = "delete from " + DBHelper.TABLE_VOD_PASSWORD;
		mSQLHelper = mDBHelper.getWritableDatabase();
		mSQLHelper.execSQL(sql);
		mSQLHelper.close();
		mDBHelper.close();
	}
	
	
	
}
