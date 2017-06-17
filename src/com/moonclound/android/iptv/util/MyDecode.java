package com.moonclound.android.iptv.util;

import java.util.HashMap;

import android.util.Log;

public class MyDecode {
     public static String getjson(String aesstr){
    	 HashMap<String,String> hash=new HashMap<String, String>();
    	 hash.put("F","0");
    	 hash.put("G","1");
    	 hash.put("H","2");
    	 hash.put("I","3");
    	 hash.put("J","4");
    	 hash.put("R","5");
    	 hash.put("T","6");
    	 hash.put("Y","7");
    	 hash.put("U","8");
    	 hash.put("M","9");
    	 String key=aesstr.substring(aesstr.length()-8,aesstr.length());
    	 String S_AES_STR=aesstr.substring(0,aesstr.length()-8);
    	 String DEXkye="";
    	 for (int i = 0; i < key.length(); i++) {
             char  item =  key.charAt(i);
             if(hash.get(item+"")==null){
            	 DEXkye+=item+"";
             }else{
            	 DEXkye+=hash.get(item+"");
             }
//             Log.d("item",item+"");
         }
    	 int numKey=Integer.parseInt(DEXkye, 16);  
    	 String EndKey=MD5Util.getStringMD5_32(numKey+"qweasdzxc");
//    	 Log.d("key",key); 
//    	 Log.d("DEXkye",DEXkye);
    	 String EedJson=AESSecurity.decrypt(S_AES_STR, EndKey);
//    	 Log.d("S_AES_STR",S_AES_STR+"");
//    	 Log.d("EedJson",EedJson+"");
    	 return EedJson;
     }
     
}
