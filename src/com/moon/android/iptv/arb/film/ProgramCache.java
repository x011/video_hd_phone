package com.moon.android.iptv.arb.film;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import com.moonclound.android.iptv.util.AESSecurity;
import com.moonclound.android.iptv.util.Logger;
import com.ev.player.util.MACUtils;
import com.moonclound.android.iptv.util.MD5Util;

public class ProgramCache {
	
	static Logger logger = Logger.getInstance();
	
	public static boolean isExist(String path){
		logger.i("Program cache file = " + path);
		File file = new File(path);
		return file.exists();
	}
	
	public static boolean isDirectory(String path){
		File file = new File(path);
		return file.isDirectory();
	}
	
	public static boolean delFile(String path){
		File file = new File(path);
		if(file.exists())
			return file.delete();
		return false;
	}
	
	
	public static void save(String path,String saveGsonStr){
		File file = new File(path);
		if(file.isDirectory()) return;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			String ciphertext = AESSecurity.encrypt(saveGsonStr, MD5Util.getStringMD5_16(MACUtils.getMac()));
			fos.write(ciphertext.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		} finally {
			if(null != fos)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	@SuppressWarnings("resource")
	public static String getGsonString(String path){
		File file = new File(path);
		if(file.isDirectory()) return "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			String resultStr = "";
			String data = br.readLine(); 
			if(null != data)
				resultStr += data;
			while(data != null){  
				data = br.readLine(); 
				if(null != data)
					resultStr += data;
			} 
			logger.i("resultStr="+resultStr);
			String expressly = AESSecurity.decrypt(resultStr, MD5Util.getStringMD5_16(MACUtils.getMac()));
			logger.i("expressly="+expressly);
			return expressly;
		} catch (FileNotFoundException e) {
			logger.e(e.toString());
		}  catch (IOException e) {
			logger.e(e.toString());
		}  catch(Exception e){
			logger.e(e.toString());
		}
		return "";
	}
	
	
	public static boolean checkIsSame(String path,String gsonStr){
		File file = new File(path);
		String md5_01 = MD5Util.getFileMD5String(file);
		String cipherText = AESSecurity.encrypt(gsonStr, 
							MD5Util.getStringMD5_16(MACUtils.getMac()));
		String md5_02 = MD5Util.getStringMD5_32(cipherText);
		logger.i("md51 = " + md5_01 + "  md52 = " + md5_02);
		if(null == md5_01) return false;
		if(null == md5_02) return false;
		if(md5_01.equals(md5_02)) return true;
		return false;
	}
	
}
