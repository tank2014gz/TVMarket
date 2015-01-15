package com.soniq.tvmarket.data;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;

import com.soniq.tvmarket.utils.MyUtils;

public class AppConfig {
	
	public final static String TAG = "alex";
	
	public final static String IMAGE_CACHE_FILE_PREFIX = "imgcache-";
	
 	public final static String clientPlatform = "tvandroid42"; // cvte版本电视用这个, 签名不一样
//	public final static String clientPlatform = "tvandroid";	// soniq电视用这个

	public final static String http_user_agent = "Mozilla/5.0 (Android; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/1.0 Mobile/EC99E CUTV/A312 Safari/525.13";
	
	
	public static String sHomeDataXMLFilename = "home.xml";
	public static String sTempHomeDataXMLFilename = "home.xml.tmp";
	
	public static String sAllAppListDataFilename = "allapp.dat";
	
	public static boolean useSDCard = false;
	
	public final static String sdRootDir = "soniq/tvmarket";
	public final static String APK_DIRNAME	= "apks";
	

	public final static String getDeviceId(Context context)
	{
		String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);	
		Log.v(AppConfig.TAG, "devid" + android_id);
		
		return android_id;
	}
	
	public final static void showLog(String log)
	{
		Log.v(AppConfig.TAG, log);
	}
	
	
	// 所有应用列表的数据文件
	public final static String getAllAppListDataFileName(Context context)
	{
		String filename = String.format("%s/%s", context.getFilesDir(), sAllAppListDataFilename);
		
		return filename;
	}
	
	public final static String getTempFileName(String filename)
	{
		String s = String.format("%s.tmp", filename);
		
		return s;
	}
	
	public final static String getHomeDataXMLFileName(Context context)
	{
		String filename = String.format("%s/%s", context.getFilesDir(), sHomeDataXMLFilename);
		
		return filename;
	}
	
	public final static String getTempHomeDataXMLFileName(Context context)
	{
		String filename = String.format("%s/%s", context.getFilesDir(), sTempHomeDataXMLFilename);
		
		return filename;
	}
	
	
	public final static String getApkDirName(Context context)
	{
		
		if( useSDCard )
		{
			// sdcard 存储
			if( !MyUtils.checkSDCardExists() )
				return null;
			
			String sdpath = MyUtils.getSDCardPath();
			String path = String.format("%s/%s/%s", sdpath, sdRootDir, APK_DIRNAME);
			
			MyUtils.makeSureDirExists(path);
			
			return path;
		}
		else
		{
			// 内部存储
			
			String path = String.format("%s/%s", context.getFilesDir(), APK_DIRNAME);
			MyUtils.makeSureDirExists(path);
			

			// 设置目录权限，否则安装apk时会提示解析错误（没有权限访问这个目录)
			// 目录要设置权限，下面的文件也一样
        	MyUtils.execCmd("chmod 777 " + path);
			
			return path;
		}
	}
}
