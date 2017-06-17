package com.mooncloud.heart.beat;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class Beat {
	private Beat() {

	}

	private LoginResult login_result;
	private static Beat beat = null;
	private String Url = "";
	private final Timer timer = new Timer();
	private TimerTask task;
	private int Retime = 10000;// 心跳包时间
	private String mac = null;

	// private int isLogin=0;//是否已登录 0 未登录 1已登录
	public final static int LOGIN_SUCCESS = 0;// 登录成功消息
	public final static int LOGIN_FAILURE = 1;// 登录失败消息
	private String BeatUrl = "/White/Beat";// 心跳地址
	private String AuthUrl = "/White/Auth";// 登录地址
	private String keyVlaue="nhsojedjif083ycG";

	public static Beat getInstance(String url, String mac) {
		if (beat == null) {
			beat = new Beat();
			beat.Url = url;
			beat.mac = mac;

		}
		return beat;
	}

	public void start() {
		this.start(null);
	}

	public void start(LoginResult Login_result) {
		this.login_result = Login_result;
		login();// 开始定时器后自动登录一次
		task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				send();
			}

		};
		timer.schedule(task, Retime, Retime);

	}

	/*
	 * 远程登录登录成功后才能正常发送心跳
	 */
	public boolean login() {
		new Thread(new Runnable() {
			public void run() {
				String returnDates = RequestUtil.getInstance().request(
						getAuthUrl());

			}
		}) {
		}.start();
		return false;
	}

	public void close() {
		timer.cancel();
	}

	private void send() {
		new Thread(new Runnable() {
			public void run() {
				String returnDates = RequestUtil.getInstance().request(
						getBeatUrl());
				// 返回1则为已登录，返回0或空为无session或连不上服务器
				if (returnDates.toString().equals("1")) {
					if (login_result != null)
						login_result.success(returnDates);
				} else {
					if (login_result != null)
						login_result.fail(returnDates);
					else{
						login();
					}
				}

				// Log.d("ww", returnDates);
			}

		}) {
		}.start();
	}

	public interface LoginResult {
		void fail(String error);
		void success(String success);
	}

	/* 获取心跳接口地址 */
	private String getBeatUrl() {
		return this.Url + this.BeatUrl + "?mac=" + this.mac;
	}

	/* 获取心跳登录接口地址 */
	private String getAuthUrl() {
		String time =Long.toString( new Date().getTime());
		return this.Url + this.AuthUrl + "?mac=" + this.mac + "&time=" + time
				+ "&key=" + createKey(time);
	}

	private String createKey(String time) {
        String keyStr=time+this.mac+this.keyVlaue;
        String AesKey=MD5.MD5Code(keyStr);
        String AesStr=Aes.encrypt(this.mac,AesKey);
        Log.d("ww",AesKey);
		try {
			return  java.net.URLEncoder.encode(AesStr,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return AesStr;
		}
	}
}
