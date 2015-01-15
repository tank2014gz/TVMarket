package com.soniq.tvmarket.data;

import android.content.Context;
import android.content.SharedPreferences;


public class MyProfile {
	
	// 广告位名称
	public final static String DATA_LOADING_KEY = "data_loading_key";
	
	private static MyProfile myProfileInstance = null;
	private static final String PROFILE_NAME = "my.config";
	
	
	public static MyProfile getInstance()
	{
		if( myProfileInstance == null )
			myProfileInstance = new MyProfile();
		
		return myProfileInstance;
	}
	
	// 首次的数据是否已经加载
	public static boolean isDataLoaded(Context context)
	{
		String s = get_profile_string_value(context, DATA_LOADING_KEY, "");
		if( s != null && s.equalsIgnoreCase("yes") )
			return true;
		
		return false;
	}
	
	
	public static int get_profile_int_value(Context context, String key, int defaultValue)
	{
		try{
			SharedPreferences pre = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE);
			
			return pre.getInt(key, defaultValue);
		}
		catch(Exception e)
		{
			
		}
		
		return defaultValue;
	}
	
	public static int save_profile_int_value(Context context, String key, int value)
	{
		try{
			SharedPreferences.Editor editor = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE).edit();
			
			editor.putInt(key, value);
			
			editor.commit();
			
			return 0;
		}
		catch(Exception e)
		{
			
		}
		
		return 1;
	}
	
	
	public static String get_profile_string_value(Context context, String key, String defaultValue)
	{
		try{
			SharedPreferences pre = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE);
			
			return pre.getString(key, defaultValue);
		}
		catch(Exception e)
		{
			
		}
		
		return defaultValue;
	}
	
	public static int save_profile_string_value(Context context, String key, String value)
	{
		try{
			SharedPreferences.Editor editor = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE).edit();
			
			editor.putString(key, value);
			
			editor.commit();
			
			return 0;
		}
		catch(Exception e)
		{
			
		}
		
		return 1;
	}	
}
