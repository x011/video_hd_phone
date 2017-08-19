package com.moon.android.iptv.arb.film;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;

import com.moon.android.model.AuthInfo;
import com.moon.android.model.Drama;
import com.moon.android.model.SeconMenu;
import com.moon.android.model.VodProgram;
import com.moonclound.android.iptv.util.UpdateData;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApplication extends Application {
	
	public static String appMsg = "";
	public static MyApplication iptvAppl1ication;
	public static UpdateData updateData;
	public static String white="1";//是否添加白名单成功全局变量 0为成功1为失败
	public static AuthInfo authInfo;

	/**将已经呈现过的节目列表保存到Application范围*/
	public static Map<String,Map<String,List<VodProgram>>>programCache=new HashMap<String, Map<String,List<VodProgram>>>();
	
	/**将已经呈现过的二级菜单列表保存到Application范围*/
	public static Map<String,List<SeconMenu>>seconMenuCache=new HashMap<String, List<SeconMenu>>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		iptvAppl1ication = this;
		
		initImageLoader(getApplication());
	}
	
	public static MyApplication getApplication() {
		return iptvAppl1ication;
		
	}
	
	
	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO)
//				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

}
