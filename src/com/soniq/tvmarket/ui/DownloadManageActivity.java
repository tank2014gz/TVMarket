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
import com.soniq.tvmarket.data.AppInfo;
import com.soniq.tvmarket.data.MyDB;
import com.soniq.tvmarket.data.MyProfile;
import com.soniq.tvmarket.data.WAPI;
import com.soniq.tvmarket.service.DownloadService;
import com.soniq.tvmarket.service.DownloadTaskInfo;
import com.soniq.tvmarket.utils.ImageUtils;
import com.soniq.tvmarket.utils.MyUtils;
import com.soniq.tvmarket.utils.PackageUtils;

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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class DownloadManageActivity extends MyBaseActivity implements OnItemClickListener{
	
	private ListView _listView = null;
	
	private List<DownloadTaskInfo> _taskList = new ArrayList<DownloadTaskInfo>();
	private MyAdapter _adapter = null;
	
	private View _lastSelectedView = null;
	
	private ImageDownloader _imgDownloader = null;
	
	private final ReentrantLock _taskLock = new ReentrantLock();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_download_manage);
		
		
		
		
		_imgDownloader = new ImageDownloader(this);
		_imgDownloader.setImageFilePrefix(AppConfig.IMAGE_CACHE_FILE_PREFIX);
		
		// 标题
		TextView titleTextView = (TextView)this.findViewById(R.id.textViewTitle);
		titleTextView.setText("下载管理");
		
		
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
		
		
		_listView.setOnItemClickListener(this);
		
		_adapter = new MyAdapter(this);
		
		loadData();
		
		_listView.setAdapter(_adapter);
		
		createReceiver();
	}
	
	
	private void showOptionDialog(int position)
	{
		// 正在下载中，显示“删除"
		// 下载完成，已经安装的显示“删除， 打开“， 未安装的显示"安装或更新"
		final Dialog dlg = new Dialog(this, R.style.dialog);
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_download_option, null);
		
		final DownloadTaskInfo taskInfo = _taskList.get(position);
		
		final int pos = position;
		
		
		
		Button btn;
		
		// 如果已经安装了，显示这个按钮
		
		if( taskInfo.state == 1 ) // 已经下载完成
		{
			int state = PackageUtils.isPackageExistedWithVersionCode(taskInfo.packageName, taskInfo.versionCode,this);
			if( state == 0 )
			{// 
				btn = (Button)v.findViewById(R.id.btn_option_open);
				btn.setVisibility(View.VISIBLE);
				btn.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) 
					{
					    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(taskInfo.packageName);  
					    startActivity(LaunchIntent);
					}
					
				});
			}
			else
			{
		        String apkfilename = MyUtils.get_filename_from_url(taskInfo.downloadUrl, true);
		        apkfilename = AppConfig.getApkDirName(this) + "/" + apkfilename;
		        File file = new File(apkfilename);
		        if( file.exists() )
		        {
		        	// 显示安装按钮
					btn = (Button)v.findViewById(R.id.btn_option_install);
					btn.setVisibility(View.VISIBLE);
					btn.setOnClickListener(new OnClickListener(){
					
						@Override
						public void onClick(View v) {
			                String filename = MyUtils.get_filename_from_url(taskInfo.downloadUrl, true);
			                filename = AppConfig.getApkDirName(DownloadManageActivity.this) + "/" + filename;
			            	
			            	
//			            	File file = new File(filename);
//			            	MyUtils.execCmd("chmod 777 " + file.toString());
//			            	
//			            	PackageUtils.installApk(DownloadManageActivity.this, filename, 0, null);
			        		MainActivity.getInstance().installApk(taskInfo.localFile, taskInfo.packageName);

			            	
			            	dlg.dismiss();
						}
						
					});
		        }
				
			}
		}
		

		// 删除显示
		btn = (Button)v.findViewById(R.id.btn_option_delete);
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				try{
					
					if( taskInfo.state == 0 )
					{
						DownloadService.getBindService().removeDownloadTask(taskInfo.taskId);
					}
	                String filename = MyUtils.get_filename_from_url(taskInfo.downloadUrl, true);
	                filename = AppConfig.getApkDirName(DownloadManageActivity.this) + "/" + filename;
	            	
	            	File file = new File(filename);
	            	if( file.exists() )
	            		file.delete();
	            	
	            	taskInfo.removeFromDB();
	            	_taskList.remove(pos);
	            	_adapter.notifyDataSetChanged();
				}
				catch(Exception e)
				{
					
				}
				
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
		int h = MyUtils.dip2px(this, 424);
		
		dlg.setContentView(v,
				new ViewGroup.LayoutParams(w, h));
		dlg.show();
	}
    	
	
	@Override
	public void onDestroy()
	{
		destroyReceiver();
		
		super.onDestroy();
	}
	
	private void loadData()
	{
		// 从数据库中加载数据
		String sql = "select * from downloadtask order by state asc, createtime desc";
		
		Cursor cursor = MyDB.getInstance().query(sql);
		if( cursor == null )
			return;
		
		if( !cursor.moveToFirst() )
		{
			cursor.close();
			return;
		}
		
		try{
		
		do
		{
			DownloadTaskInfo taskInfo = new DownloadTaskInfo();
			taskInfo.taskId = cursor.getInt(cursor.getColumnIndex("taskid"));
			taskInfo.title = cursor.getString(cursor.getColumnIndex("title"));
			taskInfo.packageName = cursor.getString(cursor.getColumnIndex("packagename"));
			taskInfo.version = cursor.getString(cursor.getColumnIndex("version"));
			taskInfo.versionCode = cursor.getString(cursor.getColumnIndex("versioncode"));
			taskInfo.iconUrl = cursor.getString(cursor.getColumnIndex("iconurl"));
			taskInfo.downloadUrl = cursor.getString(cursor.getColumnIndex("downurl"));
			taskInfo.downloadLength = cursor.getInt(cursor.getColumnIndex("totalsize"));
			taskInfo.localFile = cursor.getString(cursor.getColumnIndex("localfile"));
			taskInfo.state = cursor.getInt(cursor.getColumnIndex("state"));

			Log.v(AppConfig.TAG, taskInfo.iconUrl);
			Log.v(AppConfig.TAG, taskInfo.downloadUrl);
			Log.v(AppConfig.TAG, taskInfo.localFile);
			
			String s= String.format("load task: id=%d, title=%s,length=%d", taskInfo.taskId, taskInfo.title, taskInfo.downloadLength);
			Log.v(AppConfig.TAG, s);
			
			_taskList.add(taskInfo);
			
		}while(cursor.moveToNext());
			
		}
		catch(Exception e)
		{
		}
		finally
		{
			cursor.close();
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
			return _taskList.size();
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
			MyProgressBar progressBar;
			
			int keyId;
			String pkgname;
		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			_taskLock.lock();
			
			ViewHolder holder = null;
			if( convertView == null )
			{
				convertView = _inflater.inflate(R.layout.layout_download_item, null);
				
				holder = new ViewHolder();
				holder.imageView = (ImageView)convertView.findViewById(R.id.imageViewIcon);
				holder.textViewTitle = (TextView)convertView.findViewById(R.id.textViewTitle);
				holder.textViewInfo = (TextView)convertView.findViewById(R.id.textViewInfo);
				holder.progressBar = (MyProgressBar)convertView.findViewById(R.id.progressbar);
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
			
			holder.progressBar.setVisibility(View.INVISIBLE);
			holder.textViewInfo.setVisibility(View.INVISIBLE);
				
			DownloadTaskInfo taskInfo = _taskList.get(position);
			
			holder.keyId = taskInfo.taskId;
			holder.pkgname = taskInfo.packageName;
			
			holder.textViewTitle.setText(taskInfo.title);
			
			holder.imageView.setImageResource(R.drawable.app_loading);
			
			// 图片
			String filename = String.format("%s/%s%s", _context.getFilesDir(), 
					AppConfig.IMAGE_CACHE_FILE_PREFIX,
					AppInfo.getLocalIconFilename(taskInfo.iconUrl));
			if( MyUtils.FileExist(filename) )
			{
				Bitmap bmp = ImageUtils.loadBitmapFromFile(filename);
				if( bmp != null )
				{
					bmp = ImageUtils.getRoundedCornerBitmap(bmp, 15);
					holder.imageView.setImageBitmap(bmp);
				}
			}
			
			AppConfig.showLog(taskInfo.title);
			if( taskInfo.state == 1 ) // 已经下载了
			{
				AppConfig.showLog("code=" + taskInfo.versionCode);
				int state = PackageUtils.isPackageExistedWithVersionCode(taskInfo.packageName, taskInfo.versionCode, _context);
				if( state == 0 )
				{
					holder.textViewInfo.setText("已安装!");
				}
				else if( state == 1 )
				{
					holder.textViewInfo.setText("已下载，点击更新!");
				}
				else
					holder.textViewInfo.setText("已下载，点击安装！");
				
				holder.textViewInfo.setVisibility(View.VISIBLE);
			}
			else if( taskInfo.state == 0 ) // 等待下载
			{
				// 要刷新进度条
            	if( taskInfo.totalLength > 0 )
            	{
	            	int n = (int)(taskInfo.downloadLength * 100 / taskInfo.totalLength);
	            	
	            	holder.progressBar.setProgress(n);
            	}
            	else
	            	holder.progressBar.setProgress(0);
            		
				
				holder.textViewInfo.setText("");
				holder.progressBar.setVisibility(View.VISIBLE);
			}
			
			_taskLock.unlock();
			
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

	
    
    private BroadcastReceiver _downloadReceiver = new BroadcastReceiver(){  
        @Override  
        public void onReceive(Context context, Intent intent) 
        {  
            if( intent.getAction().equals(DownloadService.ACTION_UPDATE) )
            {
            	Log.v(AppConfig.TAG, "receive ACTION_UPDATE");
            	
            	Bundle b = intent.getExtras();
            	DownloadTaskInfo task = (DownloadTaskInfo)b.getParcelable("task");
            	
            	// 刷新列表
            	_taskLock.lock(); // 这里需要加锁，更新的时候，可能会调用getView来刷新UI

            	for(int i = 0; i < _taskList.size(); i++ )
            	{
            		DownloadTaskInfo taskInfo = _taskList.get(i);
            		if( taskInfo.taskId == task.taskId )
            		{
            			taskInfo.downloadLength = task.downloadLength;
            			taskInfo.totalLength = task.totalLength;
            			break;
            		}
            	}
            	
            	for(int i = 0; i < _listView.getChildCount(); i++ )
            	{
            		View view = _listView.getChildAt(i);
            		
    				MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder)view.getTag();
    				if( holder.keyId == task.taskId )
    				{
    					
    	            	if( task.totalLength > 0 )
    	            	{
    		            	int n = (int)(task.downloadLength * 100 / task.totalLength);
    		            	
    		            	holder.progressBar.setProgress(n);
    	            	}
    					
    					break;
    				}
            	}
            	_taskLock.unlock();
            }
            else if( intent.getAction().equals(DownloadService.ACTION_FINISHED) )
            {  
            	Log.v(AppConfig.TAG, "receive ACTION_FINISHED");

            	Bundle b = intent.getExtras();
            	DownloadTaskInfo task = (DownloadTaskInfo)b.getParcelable("task");
 
            	// 下载完成 刷新UI
            	_taskLock.lock();
            	
            	for(int i = 0; i < _taskList.size(); i++ )
            	{
            		DownloadTaskInfo taskInfo = _taskList.get(i);
            		if( taskInfo.taskId == task.taskId )
            		{
            			taskInfo.state = 1; // 下载完成
            			break;
            		}
            	}
            	
            	for(int i = 0; i < _listView.getChildCount(); i++ )
            	{
            		View view = _listView.getChildAt(i);
            		
    				MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder)view.getTag();
    				if( holder.keyId == task.taskId )
    				{
    					holder.progressBar.setVisibility(View.INVISIBLE);
    					holder.progressBar.setProgress(0);
    					
    					holder.textViewInfo.setText("下载完成！");
    					holder.textViewInfo.setVisibility(View.VISIBLE);
    					
    					break;
    				}
            	}
            	_taskLock.unlock();
            }
            else if( intent.getAction().equals(DownloadService.ACTION_INSTALLING ))
            {
            	Bundle b = intent.getExtras();
            	String pkgname = b.getString("packagename");
            	Log.v(AppConfig.TAG, "receive ACTION_INSTALLING " + pkgname);
            	
               	// 下载完成 刷新UI
            	_taskLock.lock();
            	
            	for(int i = 0; i < _listView.getChildCount(); i++ )
            	{
            		View view = _listView.getChildAt(i);
            		
    				MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder)view.getTag();
    				if( holder.pkgname.equals(pkgname) )
    				{
    					holder.progressBar.setVisibility(View.INVISIBLE);
    					holder.progressBar.setProgress(0);
    					
    					holder.textViewInfo.setText("正在安装...");
    					holder.textViewInfo.setVisibility(View.VISIBLE);
    					
    					break;
    				}
            	}
            	_taskLock.unlock();            	
            	
            }
            else if( intent.getAction().equals(DownloadService.ACTION_INSTALL_SUCCESSED ))
            {
            	Bundle b = intent.getExtras();
            	String pkgname = b.getString("packagename");
            	Log.v(AppConfig.TAG, "receive ACTION_INSTALL_SUCCESSED " + pkgname);
            	
               	// 下载完成 刷新UI
            	_taskLock.lock();
            	
            	for(int i = 0; i < _listView.getChildCount(); i++ )
            	{
            		View view = _listView.getChildAt(i);
            		
    				MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder)view.getTag();
    				if( holder.pkgname.equals(pkgname) )
    				{
    					holder.progressBar.setVisibility(View.INVISIBLE);
    					holder.progressBar.setProgress(0);
    					
    					holder.textViewInfo.setText("已安装!");
    					holder.textViewInfo.setVisibility(View.VISIBLE);
    					
    					break;
    				}
            	}
            	_taskLock.unlock();            	
            	
            	
            }
            else if( intent.getAction().equals(DownloadService.ACTION_INSTALL_FAILED ))
            {
            	Bundle b = intent.getExtras();
            	String pkgname = b.getString("packagename");
            	Log.v(AppConfig.TAG, "receive ACTION_INSTALL_FAILED " + pkgname);
            	
               	// 下载完成 刷新UI
            	_taskLock.lock();
            	
            	for(int i = 0; i < _listView.getChildCount(); i++ )
            	{
            		View view = _listView.getChildAt(i);
            		
    				MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder)view.getTag();
    				if( holder.pkgname.equals(pkgname) )
    				{
    					holder.progressBar.setVisibility(View.INVISIBLE);
    					holder.progressBar.setProgress(0);
    					
    					holder.textViewInfo.setText("安装失败!");
    					holder.textViewInfo.setVisibility(View.VISIBLE);
    					
    					break;
    				}
            	}
            	_taskLock.unlock();            	
            	
            }
            
            
        }  
    };      
    
    
    public void createReceiver()
    {
    	IntentFilter filter = new IntentFilter();  
	    filter.addAction(DownloadService.ACTION_UPDATE);  
	    filter.addAction(DownloadService.ACTION_FINISHED);  
	    filter.addAction(DownloadService.ACTION_INSTALL_SUCCESSED);
	    filter.addAction(DownloadService.ACTION_INSTALL_FAILED);
	    filter.addAction(DownloadService.ACTION_INSTALLING);
	    registerReceiver(_downloadReceiver, filter);    
    }
    
    public void destroyReceiver()
    {
	    if( _downloadReceiver != null )
	    {
	    	unregisterReceiver(_downloadReceiver);
	    	_downloadReceiver = null;
	    }
    }
    	
    
    
    @Override
    public void onResume()
    {
    	_adapter.notifyDataSetChanged();
    	super.onResume();
    }
}
