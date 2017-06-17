package com.mooncloud.heart.beat;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Aes {
	/**
	 * 加密
	 * 
	 * @param content 需要加密的内容
	 * @param password 加密密码(16位)
	 * @return
	 */
	public static String encrypt(String content, String password) {
		String text = content; // 要加密的字符串
		String key = password;
		if (key == null)
			return null;
		if (key.length() != 16)
			return null;

		Key keySpec = new SecretKeySpec(key.getBytes(), "AES");

		try {
			Cipher cipher = Cipher.getInstance("AES");
			//SecureRandom random = new SecureRandom();
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] b = cipher.doFinal(text.getBytes());
			String ret = Base64.encode(b);
			return ret;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * 
	 * @param content 需要解密的内容
	 * @param password 解密密码(16位)
	 * @return
	 */
	public static String decrypt(String content, String password) {
		byte[] bytes = Base64.decode(content);
		Key key = new SecretKeySpec(password.getBytes(), "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] ret = cipher.doFinal(bytes);
			return new String(ret, "utf-8");
		} catch (NoSuchAlgorithmException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}