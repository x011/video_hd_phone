package com.moonclound.android.iptv.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * PHP默认的加密方式  与  JAVA加密相互解析
 * 需要的包 commons-codec-1.3.jar
 * @author houhualiang
 *
 */
public class AESSecurity {
	
	public static final Logger log = Logger.getInstance();
	
	/**
	 * AES加密
	 * @param input 原文
	 * @param key  秘钥
	 * @return
	 */
	public static String encrypt(String input, String key) {
		byte[] crypted = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(input.getBytes());
		} catch (Exception e) {
			log.e(e.toString());
		}
		return new String(Base64.encodeBase64(crypted));
	}
	
	/**
	 * AES解密
	 * @param input 密文
	 * @param key 秘钥
	 * @return
	 */
	public static String decrypt(String input, String key) {
		byte[] output = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(Base64.decodeBase64(input.getBytes()));
		} catch (Exception e) {
			log.i(key);
			log.e(e.toString());
			output = "".getBytes();
		}
		return new String(output);
	}

	public static void main(String[] args) {
		String key = "1234567891234567";
		String data = "example";

		String miwen = "Cn1/7c8lduvU29w7Kcigww==";
		String key1 = "nhsojedjif083ycG";
		// System.out.println(Security.encrypt(data, key));

		System.out.println(AESSecurity.decrypt(miwen, key1));
	}
}