package com.soniq.tvmarket.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.androidquery.callback.ImageOptions;
import com.soniq.tvmarket.R;
import com.soniq.tvmarket.component.ImageDownloader;
import com.soniq.tvmarket.component.MyProgressBar;
import com.soniq.tvmarket.component.ImageDownloader.DownloadTask;
import com.soniq.tvmarket.component.ImageDownloader.OnDownloadTaskListener;
import com.soniq.tvmarket.data.AppClassInfo;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.MyDB;
import com.soniq.tvmarket.data.MyProfile;
import com.soniq.tvmarket.data.WAPI;
import com.soniq.tvmarket.service.DownloadService;
import com.soniq.tvmarket.service.DownloadTaskInfo;
import com.soniq.tvmarket.utils.ImageUtils;
import com.soniq.tvmarket.utils.MyUtils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class AppManageActivity extends MyBaseActivity implements OnItemClickListener{
	
	private ListView _listView = null;
	
	private List<LocalAppInfo> _appList = new ArrayList<LocalAppInfo>();
	private MyAdapter _adapter = null;
	
	private View _lastSelectedView = null;
	
	private AppReceiver _appReceiver = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_download_manage);
		
		
    
		// 标题
		TextView titleTextView = (TextView)this.findViewById(R.id.textViewTitle);
		titleTextView.setText("应用管理");
		
		
		_listView = (ListView)findViewById(R.id.listView);
		

		_listView.setDividerHeight(0);
		_listView.setSelector(R.drawable.bg_download_list_selected);
//		_listView.setSelector(new ColorDrawable(Color.argb(50, 255,255,255)));
//		_listView.setSelector(R.drawable.bg_app_focused);
		
		_listView.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
				String s = String.format("onItemSelected: %d, %d", arg2, arg3);
				Log.v(AppConfig.TAG, s);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				Log.v(AppConfig.TAG, "onNothingSeleced");
			}
			
		});
		
		_listView.setVerticalScrollBarEnabled(true);
		
		_listView.setOnItemClickListener(this);
		
		_adapter = new MyAdapter(this);
		
		loadData();
		
		_listView.setAdapter(_adapter);
		
		
    	IntentFilter filter = new IntentFilter();  
    	filter.addDataScheme("package");
    	filter.addAction(Intent.ACTION_PACKAGE_REMOVED); 
    	_appReceiver = new AppReceiver();
	    this.registerReceiver(_appReceiver, filter);    
    	
		
	}
	
	
	private void showOptionDialog(int position)
	{
		// 正在下载中，显示“删除"
		// 下载完成，已经安装的显示“删除， 打开“， 未安装的显示"安装或更新"
		final Dialog dlg = new Dialog(this, R.style.dialog);
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_app_option, null);
		
		final LocalAppInfo appInfo = _appList.get(position);
		
		Button btn = (Button)v.findViewById(R.id.btn_option_uninstall);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) 
			{
				Log.v(AppConfig.TAG, appInfo.packageName);
				
				// 常规
				Uri packageURI = Uri.parse("package:" + appInfo.packageName);

				Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);

				startActivity(intent);	
				
				// 系统权限的话：
				
//				PackageInstallObserver observer = new PackageInstallObserver();
//
//				pm.installPackage(mPackageURI, observer, installFlags);
//
				 

//				卸载应用权限：android.permission.DELETE_PACKAGES				
				
				dlg.dismiss();
			}
		});
		
		btn = (Button)v.findViewById(R.id.btn_option_open);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) 
			{
				Log.v(AppConfig.TAG, appInfo.packageName);
				
				// 常规
			    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(appInfo.packageName);  
			    startActivity(LaunchIntent);
			    
//				Log.v(AppConfig.TAG, appInfo.packageName);
//				Uri packageURI = Uri.parse("package:" + appInfo.packageName);
//				Intent intent = new Intent(Intent.ACTION_VIEW, packageURI);
//				startActivity(intent);	
				dlg.dismiss();
			}
		});
		
		
		btn = (Button)v.findViewById(R.id.btn_option_cancel);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		
		int w = MyUtils.dip2px(this,350);
		int h = MyUtils.dip2px(this, 360);
		
		dlg.setContentView(v,
				new ViewGroup.LayoutParams(w, h));
		dlg.show();
	}
    	
	
	@Override
	public void onDestroy()
	{
		
	    if( _appReceiver != null )
	    {
	    	this.unregisterReceiver(_appReceiver);
	    	_appReceiver = null;
	    }
		
		super.onDestroy();
	}
	
	
	private void reloadList()
	{
		_appList.clear();
		loadData();
		_adapter.notifyDataSetChanged();
	}
	
	private void loadData()
	{
		List<PackageInfo> packages = this.getPackageManager().getInstalledPackages(0);
		for(int i = 0; i < packages.size(); i++ )
		{
			PackageInfo pi = packages.get(i);
			
			if( ( pi.applicationInfo.flags  & ApplicationInfo.FLAG_SYSTEM) == 0 )
			{
				LocalAppInfo appInfo = new LocalAppInfo();
				appInfo.appName = pi.applicationInfo.loadLabel(
						getPackageManager()).toString();
				
				appInfo.packageName = pi.packageName;
				
				if( appInfo.packageName.equalsIgnoreCase(this.getPackageName()))
					continue;
				
				appInfo.versionName  = pi.versionName;
				if( appInfo.versionName == null ) appInfo.versionName = "--";
				appInfo.versionCode = pi.versionCode;
				appInfo.appIcon = pi.applicationInfo.loadIcon(getPackageManager());
				
				String dir = pi.applicationInfo.publicSourceDir;
				try{
					File file = new File(dir);
					appInfo.appSize = file.length();
				}
				catch(Exception e)
				{
					appInfo.appSize = 0;
				}
				
				
				_appList.add(appInfo);
			}
		}
	}
	
	private class MyAdapter extends BaseAdapter
	{
		private Context _context;
		private LayoutInflater _inflater;
		
		public MyAdapter(Context context)
		{
			_context = context;
			_inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return _appList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		public class ViewHolder
		{
			ImageView imageView;
			TextView textViewTitle;
			TextView textViewInfo;
			
			int keyId;
		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if( convertView == null )
			{
				convertView = _inflater.inflate(R.layout.layout_localapp_item, null);
				
				holder = new ViewHolder();
				holder.imageView = (ImageView)convertView.findViewById(R.id.imageViewIcon);
				holder.textViewTitle = (TextView)convertView.findViewById(R.id.textViewTitle);
				holder.textViewInfo = (TextView)convertView.findViewById(R.id.textViewInfo);
				convertView.setTag(holder);
						
			}
			else
				holder = (ViewHolder)convertView.getTag();
			
//			if( (position % 2) == 0 )
//			{
//				convertView.setBackgroundColor(Color.TRANSPARENT);
//			}
//			else
//			{
//				convertView.setBackgroundColor(Color.argb(50, 255,255,255));
//			}
			
				
			LocalAppInfo appInfo = _appList.get(position);
			
			holder.textViewTitle.setText(appInfo.appName);
			String s = String.format("版本：%s  大小：%s", appInfo.versionName, MyUtils.GetSizeString(appInfo.appSize));
			holder.textViewInfo.setText(s);
			
			
			holder.imageView.setImageDrawable(appInfo.appIcon);
			
			
			return convertView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.app_list, menu);
		return true;
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		showOptionDialog(arg2);				
		
	}	

	private class LocalAppInfo
	{
		public String appName;
		public String packageName;
		public String versionName;
		public int versionCode;
		public Drawable appIcon;
		public long appSize;
	}
	
	
	public class AppReceiver extends BroadcastReceiver {

	    @Override
	    public void onReceive(Context context, Intent intent) 
	    {
	    	
	        //接收广播：系统启动完成后运行程序
	        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) 
	        {
	        }
	        //接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
	        else if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
//	            String packageName = intent.getDataString().substring(8);
//	            System.out.println("---------------" + packageName);
//	            Intent newIntent = new Intent();
//	           newIntent.setClassName(packageName,packageName+ .MainActivity");
//	newIntent.setAction("android.intent.action.MAIN");             newIntent.addCategory("android.intent.category.LAUNCHER");             newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	            context.startActivity(newIntent);
	        }
	        //接收广播：设备上删除了一个应用程序包。
	        else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) 
	        {
	        	reloadList();
	        }
	    }
	}
}
