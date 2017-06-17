package com.moonclound.android.iptv.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CopyOfDbUtilbak extends SQLiteOpenHelper{
    public SQLiteDatabase db;
    public boolean debug=false;
    private static final String name = "VodCache"; //数据库名称
    private static final int version = 1; //数据库版本
    public String AllListJSONID="100";//全局节目列表ID
    public String AUTHJSONID="101";//全局节目列表ID
	public CopyOfDbUtilbak(Context context) {
		super(context, name, null, version);

		// TODO Auto-generated constructor stub
	}
    public SQLiteDatabase getDb(){
    	return getReadableDatabase();
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		   
		   db.execSQL("CREATE TABLE IF NOT EXISTS subclass (id integer primary key autoincrement, sid INTEGER UNIQUE, json varchar(255),detail varchar(255))");  
		   db.execSQL("CREATE TABLE IF NOT EXISTS JsonList (id integer primary key autoincrement, jsonId INTEGER UNIQUE, json varchar(255))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
    public void SaveAuth(String str){
    	try {
			db=getReadableDatabase();
			db.execSQL("REPLACE INTO JsonList(jsonId,json) values(?,?)",new Object[]{AUTHJSONID,str});
			if(debug){
		     	   
		     	   Cursor debugRow = db.rawQuery("select * from JsonList where jsonId = ?", new String[]{AUTHJSONID});
		     	   debugRow.moveToFirst();
		     	   Log.d("鉴权Row(id)", debugRow.getInt(0)+"");
		     	   Log.d("鉴权Row(jsonid)", debugRow.getInt(1)+"");
		     	   Log.d("鉴权Row(json)", debugRow.getString(2)+"");
		     
		        }
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
    public String GetAuth(){
    	try {
			db=getReadableDatabase();
			Cursor Row = db.rawQuery("select * from JsonList where jsonId = ?", new String[]{AUTHJSONID} );
			if(Row.moveToFirst()){
				String json=Row.getString(2);
				if(debug){
					Log.d("鉴权缓存","获取鉴权缓存成功:"+json);
				}
				db.close();
				return json;
			}else{
				if(debug){
					Log.d("鉴权缓存","获取鉴权缓存失败");
				}
				db.close();
				return null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
    }
	public void SaveAllList(String str){
		try {
			db=getReadableDatabase();
			db.execSQL("REPLACE INTO JsonList(jsonId,json) values(?,?)",new Object[]{AllListJSONID,str});
			if(debug){
		     	   
		     	   Cursor debugRow = db.rawQuery("select * from JsonList where jsonId = ?", new String[]{AllListJSONID});
		     	   debugRow.moveToFirst();
		     	   Log.d("Row(id)", debugRow.getInt(0)+"");
		     	   Log.d("Row(jsonid)", debugRow.getInt(1)+"");
		     	   Log.d("Row(json)", debugRow.getString(2)+"");
		     
		        }
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	
	} 
	public String GetAllList(){
		try {
			db=getReadableDatabase();
			Cursor Row = db.rawQuery("select * from JsonList where jsonId = ?", new String[]{AllListJSONID} );
			if(Row.moveToFirst()){
				String json=Row.getString(2);
				if(debug){
					Log.d("全局缓存","获取全局缓存成功:"+json);
				}
				db.close();
				return json;
			}else{
				if(debug){
					Log.d("全局缓存","获取全局缓存失败");
				}
				db.close();
				return null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		
		
	}
	public String GetVodDetail(String Sid){
		try {
			db=getReadableDatabase();
			Cursor cursor = db.rawQuery("select * from subclass where sid = ?", new String[]{Sid});
			
			if(cursor.moveToFirst()){
				 String wJson = cursor.getString(3);//获取第三列的值
				 db.close();
				 return wJson;
			}
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	
		return null;
	}
	public String GetVodJson(String Sid){
		try {
			db=getReadableDatabase();
			Cursor cursor = db.rawQuery("select * from subclass where sid = ?", new String[]{Sid});
			
			if(cursor.moveToFirst()){
				 String wJson = cursor.getString(2);//获取第三列的值
				 db.close();
				 return wJson;
			}
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	
		return null;
	}
	public void SaveVod(String Sid,String json,String detail){
		db=getReadableDatabase();

		Cursor cursor = db.rawQuery("select * from subclass where sid = ?", new String[]{Sid});
		if(!cursor.moveToFirst()){
			Log.d("--","无查询结果，执行插入数据库操作");
			db.execSQL("INSERT into subclass(sid,json,detail) values(?,?,?)",new Object[]{Sid,json,detail});
		}else{
			Log.d("--","SID已存在，执行更新JSON操作");
		    Object[] obj=new Object[]{};
	        String upSql="UPDATE subclass SET ";
	        if(json!=null && detail !=null){
	        	upSql+="json=? and detail=?";
	        	obj=new Object[]{json,detail,Sid};
	        }else if(json!=null){
	        	upSql+="json=?";
	        	obj=new Object[]{json,Sid};
	        }else if(detail !=null){
	        	upSql+="detail=?";
	        	obj=new Object[]{detail,Sid};
	        }
	        upSql+=" where sid=?";
//	        db.replace(table, nullColumnHack, initialValues)
	        if(debug){
	      	   Log.d("--upsql---",upSql);
	        }
	     
			db.execSQL(upSql,obj);
		}
	    if(debug){
     	   
     	   Cursor debugRow = db.rawQuery("select * from subclass where sid = ?", new String[]{Sid});
     	   debugRow.moveToFirst();
     	   Log.d("Row(id)", debugRow.getInt(0)+"");
     	   Log.d("Row(sid)", debugRow.getInt(1)+"");
     	   Log.d("Row(json)", debugRow.getString(2)+"");
     	   Log.d("Row(detail)", debugRow.getString(3)+"");
        }
		db.close();
		
//			   int id = cursor.getInt(0); //获取第一列的值,第一列的索引从0开始
//			   String wSid = cursor.getString(1);//获取第二列的值
//			   String wJson = cursor.getString(2);//获取第三列的值
//			   Log.d("ss","id:"+id+"--"+"Sid:"+wSid+"----"+"json:"+wJson);
			   
	
//		Log.d("Sid",json);
	}
           
}
