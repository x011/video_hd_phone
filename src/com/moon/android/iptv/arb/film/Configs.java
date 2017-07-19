package com.moon.android.iptv.arb.film;

import android.os.Environment;

import com.ev.player.util.DeviceFun;
import com.moonclound.android.iptv.util.MACUtils;


public class Configs {
	
	//
	public static final String NORDSPWD = "0";
	public static final String ISDSPWD = "1";// 限制级影片
	/**Need change data start**/
	public static final boolean debug = false;
	public static final String APPID = "70002";//22203
	public static final String PKG_NAME = "com.ev.android.evodshd.plus";
	public static boolean isLastNeedPassword = true;  //最后一项输入密码
	/**Need change data end **/
	 
	public static String link;
	public static String chip;
	public static final int NETWORK_NOT_CONNECT = 0x200301;
	public static final int NETWORK_CONNECT = 0x200302;
	public static final int AUTH_STOP = 0x200303;
	public static final int AUTH_FAIL = 0x200304;
	public static final int SUCCESS = 0x200305;
	public static final int FAIL = 0x200306;
	public static final int VIDEO_PLAY = 0x200307;
	public static final int NETWORK_EXCEPTION = 0x200308;
	public static final int CONTENT_IS_NULL = 0x200309;
	public static final int AUTH_SUCCESS_READ_LIST_CACHE = 0x200310;
	public static final int CACHE_LIST_NEW = 0x300311;
	
	public static final class Handler{
		public static final int PROGRAM_DETAIL_NULL=0x300001;
		public static final int PROGRAM_DETAIL_OK=0x300002;
		public static final int PROGRAM_DETAIL_PARSE_FAILURE=0x300003;
	}
	 
	//PLAYER VERSION  file in /ASSETS/MoonPlayer_xx.apk
	//public static final String PLAYER_VERSION = "1.0";
	public static final String PLAYER_PKG = "com.moon.android.moonplayer";
	public static final String APK_NAME="update.apk";
	public static final String INTENT_PARAM = "intent_param1";
	public static final String INTENT_PARAM_2 = "intent_param2";
	
//	public static final String CONTENT_CACHE_DIR = Environment.getExternalStorageDirectory().toString()+"/test/";
//	public static final String CONTENT_CACHE_FILE = CONTENT_CACHE_DIR+"just"+APPID;
	public static final String CONTENT_CACHE_FILE = MyApplication.getApplication().getCacheDir() .getAbsolutePath() +"/just"+APPID;
	public static final String ALL_LIST_CACHE = MyApplication.getApplication().getCacheDir() .getAbsolutePath() +"/AllList"+APPID;
	
	public static class BroadCast{
		public static final String UPDATE_MSG = PKG_NAME + ".update";
		public static final String APP_GET_MSG = PKG_NAME + ".msg";
	}
	
	public static class URL{
		public static final String APP_ID=APPID;
		public static final String MAC=MACUtils.getMac();
		
		//vodchina1.ibcde.net(xiangYunZhiBo)
		//vod1.ibcde.net(yueGangShiJie)
		public static String HOST1="http://vodplus.etvhk.com/Api/";//正式库1
		public static final String HOST2="http://vodplus.etvb.hk/Api/";//正式库2
		public static final String HOST3="http://vodplus.iesaytv.com/Api/";//正式库3  http://hdvod.etvhk.com/Api/
		public static String HOST=HOST1;//正式库
//		 
//		public static final String HOST1="http://23.89.145.178:9011/Api/";//测试库1
//		public static final String HOST2="http://23.89.145.178:9011/Api/";//测试库1
//		public static final String HOST3="http://23.89.145.178:9011/Api/";//测试库1
//		public static String HOST=HOST1;//测试库
		  
		public static String getHot(){
			return HOST+"VideoApi/hot?";
		}
		/**获取列表缓存地址*/
		public static String getListCache(){
			return HOST+"VideoApi/AllLists?";
		}
		/**获取授权地址*/
		public static String getLeftMenuApi(){
			return HOST;
		} 
		/**获取授权地址*/
		public static String getAuthApi(){
			return HOST+"AppNew/auth?";
		}
		/**添加白名单地址*/
		public static String addWhiteListApi(){
			return "AppNew/getStream?";
		}
		/**获取二级菜单基地址，后面还要加CID*/
		public static String getSeconMenuApi(){
			return HOST;
		}
		/**获取节目列表基地址*/
		public static String getProgramApi(){
			return HOST;
		}
		/**获取剧集基地址*/
		public static String getDramaApi(){
			return HOST+"VideoApi/cli?";
		}
		/**get vod program detail info*/
		public static String getProgramDetailApi(){
			return HOST+"VideoApi/cliInfo?";
		}
		//-1 http
		public static String getAppMsgApi(){
			return "http://etvhk.com/AppMsg.php?mac="+"?appid="+ APPID + "&mac=" + MAC;
		}
		//-1 http
		public static String getAppUpdateApi(){
			return "http://etvhk.com/AppUp.php" +"?appid="+ APPID + "&mac=" + MAC;
		}
		public static String getAdApi(){
			return HOST +"VideoAd?";
		}
	}
	
	public static class Success{
		public static final int GET_LEFT_MENU=0x000001;
		public static final int GET_LEFT_MENU_AUTH=0x000002;
		public static final int GET_SECON_MENU=0x000003;
		public static final int GET_PROGRAM_LIST=0x000004;
		public static final int GET_DRAMA_LIST=0x000005;
		public static final int AUTH_OK=0x000006;
		
		public static final int GET_CACHE_LEFT_MENU=0x000007;
		
		public static final int GET_AD=0x000008;
		
		public static final int CLOSE_AD=0x000009;
		public static final int UPDATE_AD_TIME=0x000010;
		public static final int STOP_AD_TIME=0x001001;
	}
	
	public static class Failure{
		public static final int GET_LEFT_MENU=0x000011;
		public static final int GET_LEFT_MENU_AUTH=0x000012;
		public static final int GET_SECON_MENU=0x000013;
		public static final int GET_PROGRAM_LIST=0x000014;
		public static final int GET_DRAMA_LIST=0x000015;
		
		public static final int AUTH_WRONG=0x000016;
		public static final int GET_CACHE_LEFT_MENU=0x000017;
		public static final int NETWORK_EXCEPTION=0x000018;
		public static final int GSON_WRONG=0x000019;//Gson解析出错
		public static final int GET_LEFT_MENU_NET_WRONG=0x000020;
		
		public static final int GET_AD=0x000021;
	}
	
	/**
	 * 缓存目录
	 */
	public static class CachePath{
		private static final String ABSOLUTE_PATH = MyApplication.getApplication().getCacheDir() .getAbsolutePath();
		public static final String AUTH=ABSOLUTE_PATH+"/auth"+APPID;
		public static final String LEFT_MENU=ABSOLUTE_PATH+"/leftmenu"+APPID;
	}
	
	public static class Code{
		public static final String AUTH_OK="0";
	}
	
	public static class File{
		public static final int START_DOWNLOAD=0x000101;
		public static final String UPDATE_PATH="/data"
				+ Environment.getDataDirectory().getAbsolutePath() + "/"
				+ Configs.PKG_NAME+ "/";
	}
	
	public static class Other{
		/**改变多少次主机就提示用户手动重试*/
		public static final int HOST_CHANGE_TIME=6;
	}
	
	public static final class HeartBeat{
		public static final String URL="http://whlist.yourepg.com:9011";
		public static final String MAC=Configs.URL.MAC;
	}
}
