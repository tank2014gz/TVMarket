package com.soniq.tvmarket.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.ui.MainActivity;

public class PackageUtils {
	private static int INSTALL_REPLACE_EXISTING = 2;

	
	public static  boolean isPackageExisted(String targetPackage, Context context){
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			return false;
		}
		return true;
	}
	
	
	public static int isPackageExistedWithVersionCode(String packageName, String versionCode, Context context)
	{
		PackageManager pm = context.getPackageManager();
		try{
	        	PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(packageName, 0);
	        	
	        	// 已经安装过，再比较版本
	        	String versionCode1 = String.format("%d", pkgInfo.versionCode);
	        	
	        	int n = MyUtils.compareVersionString(versionCode, versionCode1);
	        	if( n == 0 )
	        		return 0;
	        	else if( n > 0 )
	        		return 1;
	        	else
	        		return 2;
		}
		catch(Exception e)
		{
			
		}
		return -1;
	}

    public static int chmod(File path, int mode) throws Exception {
		Class<?> fileUtils = Class.forName("android.os.FileUtils");
		Method setPermissions = fileUtils.getMethod("setPermissions", String.class, int.class, int.class, int.class);
		return (Integer) setPermissions.invoke(null, path.getAbsolutePath(), mode, -1, -1);
   	}
    
    public static int silentInstall(Context context, File downloaded,InstallApkCallback callback){
    	final InstallApkCallback _callback = callback;
		return silentInstall(context, downloaded, new IPackageInstallObserver.Stub() {
			
			@Override
			public void packageInstalled(String packageName, int returnCode)
					throws RemoteException {
				
				if( _callback != null )
				{
					_callback.onInstallFinished(packageName, returnCode);
				}
				
				Log.v(AppConfig.TAG, "packageInstalled: " + packageName + " returnCode:" + returnCode);
			}
		});
    }
    
	private static int silentInstall(Context context, File downloaded, IPackageInstallObserver.Stub observer){

    	PackageManager pm = context.getPackageManager();
    	
    	Class<?>[] types = new Class[] {Uri.class, IPackageInstallObserver.class, int.class, String.class};
    	Method method = null;
    	try {
    		Log.v(AppConfig.TAG, "111");
    		chmod(downloaded, 0755);
    		Log.v(AppConfig.TAG, "222");
    		
			method = pm.getClass().getMethod("installPackage", types);		
    		Log.v(AppConfig.TAG, "333");
			method.invoke(pm, new Object[] {Uri.fromFile(downloaded), observer, INSTALL_REPLACE_EXISTING, "com.soniq.tvmarket"});
    		Log.v(AppConfig.TAG, "444");
		} 
    	catch (InvocationTargetException invocateErr) 
    	{
//    		Intent intent = new Intent(Intent.ACTION_VIEW); 
//	   		intent.setDataAndType(Uri.fromFile(downloaded), "application/vnd.android.package-archive"); 
//	   		//mContext.startActivity(intent);
//	   		context.startActivityForResult(intent, 1);
    		Log.v(AppConfig.TAG, "InvocationTargetException");
    		invocateErr.printStackTrace();
    		return 2;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return 1;
		}
    	
    	return 0;
	}

	
	
	public interface InstallApkCallback{
		public void onInstallFinished(String packageName, int resultCode);
	}
	
	
	public static int installApk(Activity activity, String filename, int requestCode, InstallApkCallback callback)
	{
		int iret = 1;
		try
		{
			File file = new File(filename);
			if( !file.exists() )
				return 1;
	
			iret = 2;
	    	if( silentInstall(activity, file, callback) != 0 )
	    	{
	        	Log.v(AppConfig.TAG, "silent install failed, retry to install use intent");
	        	Log.v(AppConfig.TAG, file.toString());
	        	
	    		Intent nn = new Intent(Intent.ACTION_VIEW);
	    		nn.setDataAndType(Uri.parse("file://"+file.toString()), "application/vnd.android.package-archive");
	    		nn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	    		activity.startActivity(nn);//ForResult(nn, requestCode);
	        	iret = 0;
	    	}
		}
		catch(Exception e)
		{
			iret = 3;
		}
    	
    	return iret;
		
	}

}
