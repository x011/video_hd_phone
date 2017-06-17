package com.moonclound.android.iptv.util;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

public class MACUtils {
	public static String getMac2() {
		FileInputStream localFileInputStream;
		String mac = "";
		try{
			localFileInputStream = new FileInputStream(
					"/sys/class/net/eth0/address");
			byte[] arrayOfByte = new byte[17];
			localFileInputStream.read(arrayOfByte, 0, 17);
			mac = new String(arrayOfByte);
			localFileInputStream.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mac.toLowerCase();
//		return "002157f3b51c";
//		return "002157f3d8f6";
	}
	public static String getMac() {
		FileInputStream localFileInputStream;
		String str = "";
		try{
			localFileInputStream = new FileInputStream(
					"/sys/class/net/eth0/address");
			byte[] arrayOfByte = new byte[17];
			localFileInputStream.read(arrayOfByte, 0, 17);
			str = new String(arrayOfByte);
			localFileInputStream.close();
			
		}catch (IOException e) {
			e.printStackTrace();
			str=getWifiMac();
		}
		if(str.equals("") || str==""){
			str=getWifiMac();
		}
		
		if (str.contains(":"))
			str = str.replace(":", "").trim();
		if (str.contains("-"))
			str = str.replace("-", "").trim();
		return str.toLowerCase();
	}
	public static String getWifiMac(){
        String str="";
        String macSerial="";
        try {
            java.lang.Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        return macSerial;
    }
	 public static String loadFileAsString(String fileName) throws Exception {
	        FileReader reader = new FileReader(fileName);
	        String text = loadReaderAsString(reader);
	        reader.close();
	        return text;
	    }
	    public static String loadReaderAsString(Reader reader) throws Exception {
	        StringBuilder builder = new StringBuilder();
	        char[] buffer = new char[4096];
	        int readLength = reader.read(buffer);
	        while (readLength >= 0) {
	            builder.append(buffer, 0, readLength);
	            readLength = reader.read(buffer);
	        }
	        return builder.toString();
	    }
}
