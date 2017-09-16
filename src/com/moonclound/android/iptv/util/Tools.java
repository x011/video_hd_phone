package com.moonclound.android.iptv.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.moon.android.iptv.arb.film.Configs;

public class Tools {
	private static String ScreenModeFile = "/sys/class/video/screen_mode";
	private static String TAG = "ScreenFile";
	private static List<String> apkList;
	private static List<String> musicList;
	private static List<String> photoList;
	private static List<String> videoList = new ArrayList();

	static {
		photoList = new ArrayList();
		musicList = new ArrayList();
		apkList = new ArrayList();
	}

	public static String ReadFile() {
		try {
			FileReader localFileReader = new FileReader("/sys/class/video/axis");
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader);
			String str = localBufferedReader.readLine();
			localBufferedReader.close();
			localFileReader.close();
			return str;
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
		return "";
	}

	// public static Bitmap decodeBitmap(Resources paramResources, int
	// paramInt1, int paramInt2, int paramInt3)
	// {
	// BitmapFactory.Options localOptions = new BitmapFactory.Options();
	// localOptions.inJustDecodeBounds = true;
	// BitmapFactory.decodeResource(paramResources, paramInt1, localOptions);
	// int i = (int)Math.ceil(localOptions.outWidth / paramInt2);
	// int j = (int)Math.ceil(localOptions.outHeight / paramInt3);
	// if ((i > 1) && (j > 1))
	// if (i <= j)
	// break label91;
	// for (localOptions.inSampleSize = i; ; localOptions.inSampleSize = j)
	// {
	// localOptions.inJustDecodeBounds = false;
	// label91: return BitmapFactory.decodeResource(paramResources, paramInt1,
	// localOptions);
	// }
	// }

	public static Bitmap decodeBitmap1(Resources paramResources, int paramInt1,
			int paramInt2, int paramInt3) {
		Bitmap localBitmap1 = BitmapFactory.decodeResource(paramResources,
				paramInt1);
		int i = localBitmap1.getWidth();
		int j = localBitmap1.getHeight();
		float f1 = paramInt2 / i;
		float f2 = paramInt3 / j;
		Matrix localMatrix = new Matrix();
		localMatrix.postScale(f1, f2);
		Bitmap localBitmap2 = Bitmap.createBitmap(localBitmap1, 0, 0, i, j,
				localMatrix, false);
		localBitmap1.recycle();
		return localBitmap2;
	}

	// private static String getMac(Context paramContext)
	// {
	// // try
	// // {
	// // String str = paramContext.createPackageContext("com.android.systemui",
	// 2).getSharedPreferences("mac_addr", 2).getString("macAddress",
	// "no macAddress");
	// // return str;
	// // }
	// // catch (PackageManager.NameNotFoundException
	// localNameNotFoundException)
	// // {
	// // localNameNotFoundException.printStackTrace();
	// // }
	// return "50465d0d442";
	// }

	public static String getMacAddress() {
		FileInputStream localFileInputStream;
		String str = "";
		try {
			localFileInputStream = new FileInputStream(
					"/sys/class/net/eth0/address");
			byte[] arrayOfByte = new byte[17];
			localFileInputStream.read(arrayOfByte, 0, 17);
			str = new String(arrayOfByte);
			localFileInputStream.close();
			if (str.contains(":"))
				str = str.replace(":", "").trim();
			if (str.contains("-"))
				str = str.replace("-", "").trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str.toLowerCase();
	}

	public static Map getMapFiles(String paramString, boolean paramBoolean) {
		videoList.clear();
		photoList.clear();
		musicList.clear();
		apkList.clear();
		HashMap localHashMap = new HashMap();
		listFile(new File(paramString), paramBoolean);
		if (videoList.size() > 0)
			localHashMap.put("video", videoList);
		if (photoList.size() > 0)
			localHashMap.put("photo", photoList);
		if (musicList.size() > 0)
			localHashMap.put("music", musicList);
		if (apkList.size() > 0)
			localHashMap.put("apk", apkList);
		return localHashMap;
	}

	// ERROR //
	// public static int getScreenMode()
	// {
	// // Byte code:
	// // 0: new 198 java/io/File
	// // 3: dup
	// // 4: getstatic 19 com/moon/moonlibrary/tools/Tools:ScreenModeFile
	// Ljava/lang/String;
	// // 7: invokespecial 199 java/io/File:<init> (Ljava/lang/String;)V
	// // 10: invokevirtual 225 java/io/File:exists ()Z
	// // 13: ifne +5 -> 18
	// // 16: iconst_0
	// // 17: ireturn
	// // 18: new 50 java/io/BufferedReader
	// // 21: dup
	// // 22: new 43 java/io/FileReader
	// // 25: dup
	// // 26: getstatic 19 com/moon/moonlibrary/tools/Tools:ScreenModeFile
	// Ljava/lang/String;
	// // 29: invokespecial 48 java/io/FileReader:<init> (Ljava/lang/String;)V
	// // 32: bipush 32
	// // 34: invokespecial 228 java/io/BufferedReader:<init>
	// (Ljava/io/Reader;I)V
	// // 37: astore_0
	// // 38: aload_0
	// // 39: invokevirtual 56 java/io/BufferedReader:readLine
	// ()Ljava/lang/String;
	// // 42: astore 4
	// // 44: getstatic 23 com/moon/moonlibrary/tools/Tools:TAG
	// Ljava/lang/String;
	// // 47: new 230 java/lang/StringBuilder
	// // 50: dup
	// // 51: ldc 232
	// // 53: invokespecial 233 java/lang/StringBuilder:<init>
	// (Ljava/lang/String;)V
	// // 56: aload 4
	// // 58: invokevirtual 237 java/lang/StringBuilder:append
	// (Ljava/lang/String;)Ljava/lang/StringBuilder;
	// // 61: invokevirtual 240 java/lang/StringBuilder:toString
	// ()Ljava/lang/String;
	// // 64: invokestatic 246 android/util/Log:d
	// (Ljava/lang/String;Ljava/lang/String;)I
	// // 67: pop
	// // 68: aload 4
	// // 70: iconst_0
	// // 71: iconst_1
	// // 72: invokevirtual 250 java/lang/String:substring
	// (II)Ljava/lang/String;
	// // 75: astore 6
	// // 77: getstatic 23 com/moon/moonlibrary/tools/Tools:TAG
	// Ljava/lang/String;
	// // 80: new 230 java/lang/StringBuilder
	// // 83: dup
	// // 84: ldc 252
	// // 86: invokespecial 233 java/lang/StringBuilder:<init>
	// (Ljava/lang/String;)V
	// // 89: aload 6
	// // 91: invokevirtual 237 java/lang/StringBuilder:append
	// (Ljava/lang/String;)Ljava/lang/StringBuilder;
	// // 94: invokevirtual 240 java/lang/StringBuilder:toString
	// ()Ljava/lang/String;
	// // 97: invokestatic 246 android/util/Log:d
	// (Ljava/lang/String;Ljava/lang/String;)I
	// // 100: pop
	// // 101: aload 6
	// // 103: invokestatic 258 java/lang/Integer:parseInt (Ljava/lang/String;)I
	// // 106: istore 8
	// // 108: getstatic 23 com/moon/moonlibrary/tools/Tools:TAG
	// Ljava/lang/String;
	// // 111: new 230 java/lang/StringBuilder
	// // 114: dup
	// // 115: ldc_w 260
	// // 118: invokespecial 233 java/lang/StringBuilder:<init>
	// (Ljava/lang/String;)V
	// // 121: iload 8
	// // 123: invokevirtual 263 java/lang/StringBuilder:append
	// (I)Ljava/lang/StringBuilder;
	// // 126: invokevirtual 240 java/lang/StringBuilder:toString
	// ()Ljava/lang/String;
	// // 129: invokestatic 246 android/util/Log:d
	// (Ljava/lang/String;Ljava/lang/String;)I
	// // 132: pop
	// // 133: aload_0
	// // 134: invokevirtual 59 java/io/BufferedReader:close ()V
	// // 137: iload 8
	// // 139: ireturn
	// // 140: astore_2
	// // 141: getstatic 23 com/moon/moonlibrary/tools/Tools:TAG
	// Ljava/lang/String;
	// // 144: ldc_w 265
	// // 147: invokestatic 268 android/util/Log:e
	// (Ljava/lang/String;Ljava/lang/String;)I
	// // 150: pop
	// // 151: iconst_0
	// // 152: ireturn
	// // 153: astore_1
	// // 154: aload_0
	// // 155: invokevirtual 59 java/io/BufferedReader:close ()V
	// // 158: aload_1
	// // 159: athrow
	// //
	// // Exception table:
	// // from to target type
	// // 18 38 140 java/io/IOException
	// // 133 137 140 java/io/IOException
	// // 154 160 140 java/io/IOException
	// // 38 133 153 finally
	// }

	public static int getVerCode(Context paramContext) {
		try {
			int i = paramContext.getPackageManager().getPackageInfo(
					Configs.PKG_NAME, 0).versionCode;
			return i;
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			localNameNotFoundException.printStackTrace();
		}
		return -1;
	}

	public static String getVerName(Context paramContext) {
		try {
			String str = paramContext.getPackageManager().getPackageInfo(
					Configs.PKG_NAME, 0).versionName;
			return str;
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			localNameNotFoundException.printStackTrace();
		}
		return "";
	}

	public static List getVoideList(String paramString) {
		File localFile = new File(paramString);
		ArrayList localArrayList = new ArrayList();
		Log.v("debug", "+++++++++" + localFile.listFiles().length);
		for (int j = 0; j < localFile.listFiles().length; j++) {
			localArrayList.add(localFile.listFiles()[j].getPath());
		}
		return localArrayList;
	}

	public static String getformatMac(String paramString) {
		return paramString.replaceAll("-", "").toLowerCase();
	}

	public static void listFile(File paramFile, boolean paramBoolean) {
		String str1;
		String str2;
		File[] arrayOfFile;
		if ((paramFile.isDirectory()) && (paramBoolean)) {
			arrayOfFile = paramFile.listFiles();
			if (arrayOfFile != null) {
				for (int j = 0; j < arrayOfFile.length; j++) {
					str1 = paramFile.getAbsolutePath();
					int i = str1.lastIndexOf(".");
					str2 = "";
					if (i != -1)
						str2 = str1.substring(i + 1, str1.length());
					if ((str2.equals("mp4")) || (str2.equals("ts"))
							|| (str2.equals("3gp")) || (str2.equals("wmv"))
							|| (str2.equals("flv")) || (str2.equals("rm"))
							|| (str2.equals("rmvb")) || (str2.equals("avi"))) {
						videoList.add(str1);
						continue;
					}
					if (str2.equals("mp3")) {
						musicList.add(str1);
						continue;
					}
					if (str2.equals("jpg") || str2.equals("bmp")) {
						photoList.add(str1);
						continue;
					}
					if (str2.equals("apk")) {
						apkList.add(str1);
						continue;
					}
				}
			}
		}

	}

	// public static void setLanguage(Context paramContext, Locale paramLocale)
	// {
	// Resources localResources = paramContext.getResources();
	// Configuration localConfiguration = localResources.getConfiguration();
	// DisplayMetrics localDisplayMetrics = localResources.getDisplayMetrics();
	// localConfiguration.locale = paramLocale;
	// localResources.updateConfiguration(localConfiguration,
	// localDisplayMetrics);
	// // new Thread(new Runnable(paramLocale)
	// // {
	// // public void run()
	// // {
	// // new Tools().setlanguage1(Tools.this);
	// // }
	// // }).start();
	// }

	public static int setScreenMode(String paramString) {
		if (!(new File(ScreenModeFile).exists()))
			return 0;
		try {
			BufferedWriter localBufferedWriter = new BufferedWriter(
					new FileWriter(ScreenModeFile), 32);
			try {
				localBufferedWriter.write(paramString);
				Log.d(TAG, "set Screen Mode to:" + paramString);
				return 1;
			} finally {
				localBufferedWriter.close();
			}
		} catch (IOException localIOException) {
			Log.e(TAG, "IOException when setScreenMode ");
		}
		return 0;
	}

	public static void setVodSize(int paramInt1, int paramInt2, int paramInt3,
			int paramInt4) {
		writeFile(paramInt1 + " " + paramInt2 + " " + " " + paramInt3 + " "
				+ paramInt4, "/sys/class/video/axis");
	}

	public static void setVodSize(String paramString) {
		writeFile(paramString, "/sys/class/video/axis");
	}

	public static void writeFile(String paramString1, String paramString2) {
	}

	public void getimg() {
	}

	// public void setlanguage1(Locale locale)
	// {
	// // IActivityManager localIActivityManager =
	// ActivityManagerNative.getDefault();
	// // try
	// // {
	// // Configuration localConfiguration =
	// localIActivityManager.getConfiguration();
	// // localConfiguration.locale = paramLocale;
	// // localIActivityManager.updateConfiguration(localConfiguration);
	// // BackupManager.dataChanged("com.android.providers.settings");
	// // return;
	// // }
	// // catch (RemoteException localRemoteException)
	// // {
	// // localRemoteException.printStackTrace();
	// // }
	// Log.d("yzy", locale.toString());
	// try {
	// Object objIActMag;
	// Class clzIActMag = Class.forName("android.app.IActivityManager");
	// Class clzActMagNative =
	// Class.forName("android.app.ActivityManagerNative");
	// Method mtdActMagNative$getDefault =
	// clzActMagNative.getDeclaredMethod("getDefault");
	// // IActivityManager iActMag = ActivityManagerNative.getDefault();
	// objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
	// // Configuration config = iActMag.getConfiguration();
	// Method mtdIActMag$getConfiguration =
	// clzIActMag.getDeclaredMethod("getConfiguration");
	// Configuration config = (Configuration)
	// mtdIActMag$getConfiguration.invoke(objIActMag);
	// config.locale = locale;
	// //config.userSetLocale = true;
	// // iActMag.updateConfiguration(config);
	// // 此处�?��声明权限:android.permission.CHANGE_CONFIGURATION
	// // 会重新调�?onCreate();
	// Class[] clzParams = { Configuration.class };
	// Method mtdIActMag$updateConfiguration = clzIActMag.getDeclaredMethod(
	// "updateConfiguration", clzParams);
	// mtdIActMag$updateConfiguration.invoke(objIActMag, config);
	//
	//
	// BackupManager.dataChanged("com.android.providers.settings");
	// Class clzBackupManager =
	// Class.forName("android.app.backup.BackupManager");
	// Class[] clzString= {String.class};
	// Method mtdDataChanged = clzBackupManager.getDeclaredMethod("dataChanged",
	// clzString);
	// mtdDataChanged.invoke(clzBackupManager,
	// "com.android.providers.settings");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	private static class muiscFilter implements FilenameFilter {
		public boolean accept(File paramFile, String paramString) {
			return paramString.endsWith(".mp3");
		}
	}

	private static class photoFilter implements FilenameFilter {
		public boolean accept(File paramFile, String paramString) {
			return paramString.endsWith(".jpg");
		}
	}

	private static class videoFilter implements FilenameFilter {
		public boolean accept(File paramFile, String paramString) {
			return ((paramString.endsWith(".mp4")) || (paramString
					.endsWith(".3gp")));
		}
	}
}

/*
 * Location: E:\ria\反编译\dex2jar-0.0.9.15\classes_dex2jar.jar Qualified Name:
 * com.moon.moonlibrary.tools.Tools JD-Core Version: 0.5.2
 */