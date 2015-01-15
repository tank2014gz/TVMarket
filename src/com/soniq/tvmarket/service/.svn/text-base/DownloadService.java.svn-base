package com.soniq.tvmarket.service;

import java.io.File;

import com.soniq.tvmarket.model.HomeDataLoader;
import com.soniq.tvmarket.utils.MyUtils;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.AppInfo;
import com.soniq.tvmarket.data.MyDB;
import com.soniq.tvmarket.data.WAPI;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.view.View;


public class DownloadService extends Service {
	
	public static Intent _downloadServiceIntent = null;
	
	public final static String ACTION_UPDATE = DownloadService.class.getName() + ".action.updateui"; // "com.chris.download.service.UPDATE";  
	public final static String ACTION_FINISHED = DownloadService.class.getName() + ".action.finished";  	
	
	public final static String ACTION_INSTALLING = DownloadService.class.getName() + ".action.installing";  	
	public final static String ACTION_INSTALL_SUCCESSED = DownloadService.class.getName() + ".action.install.successed";  	
	public final static String ACTION_INSTALL_FAILED = DownloadService.class.getName() + ".action.install.failed";  	

	private List<DownloadTask> _taskList;
	private final ReentrantLock _taskLock = new ReentrantLock();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.v(AppConfig.TAG,"DownloadService::onBind" );
		
		
		return new ServiceBinder();
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
		Log.v(AppConfig.TAG, "DownloadService::onUnbind");
		return super.onUnbind(intent);
	}
	
	
	public class ServiceBinder extends Binder
	{  
        /** 
         * 获取当前Service的实例 
         * @return 
         */  
        public DownloadService getService(){  
        	Log.v(AppConfig.TAG, "Binder: getService");
            return DownloadService.this;  
        }  
    }  	
	
	
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.v("alex","DownloadService::onCreate" );
		
		
		_taskList = new ArrayList<DownloadTask>();
	}
	
	// Service是否已经运行
   public static boolean isServiceRunning(Context context) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
	    {
	        if (DownloadService.class.getName().equals(service.service.getClassName())) 
	        {
	            return true;
	        }
	    }
	    
	    return false;
	}    
	    
	
	// 开始一个下载任务
	public static Intent startDownloadService(Context context, DownloadTaskInfo taskInfo)
	{
		Intent intent = new Intent(context, DownloadService.class);
		Bundle bundle = new Bundle();
		bundle.putString("command", "download");
		
		bundle.putParcelable("taskinfo", taskInfo);
		
		intent.putExtras(bundle);
		
		context.startService(intent);
		
		return intent;
		
	}
	
	public static boolean startService(Context context)
	{
		
		Intent intent = new Intent(context, DownloadService.class);
		Bundle bundle = new Bundle();
		bundle.putString("command", "init");
		intent.putExtras(bundle);
		
		context.startService(intent);
		
		_downloadServiceIntent = intent;
		
		return true;
	}
	
	public static void stopService(Context context)
	{
		if( _downloadServiceIntent != null )
		{
			context.stopService(_downloadServiceIntent);
			_downloadServiceIntent = null;
		}
	}
	
	private void loadTaskFromDB()
	{
		String sql = "select * from downloadtask where state = 0 order by createtime desc";
		
		Cursor cursor = MyDB.getInstance().query(sql);
		if( cursor ==  null )
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
				taskInfo.iconUrl = cursor.getString(cursor.getColumnIndex("iconurl"));
				taskInfo.downloadUrl = cursor.getString(cursor.getColumnIndex("downurl"));
				taskInfo.downloadLength = cursor.getInt(cursor.getColumnIndex("totalsize"));
				taskInfo.localFile = cursor.getString(cursor.getColumnIndex("localfile"));

				Log.v(AppConfig.TAG, taskInfo.iconUrl);
				Log.v(AppConfig.TAG, taskInfo.downloadUrl);
				Log.v(AppConfig.TAG, taskInfo.localFile);
				
				String s= String.format("load task: id=%d, title=%s,length=%d", taskInfo.taskId, taskInfo.title, taskInfo.downloadLength);
				Log.v(AppConfig.TAG, s);
				
				DownloadTask dt = new DownloadTask(this);
				dt.taskInfo = taskInfo;

				_taskList.add(dt);
				
			}while( cursor.moveToNext() );
		}
		finally
		{
			cursor.close();
		}
		
		
	}
	
	public void removeDownloadTask(int taskId)
	{
		_taskLock.lock();
		
		DownloadTask task = null;
		
		//检查一下是否任务已经存在了
		for(int i = 0;i < _taskList.size(); i++ )
		{
			DownloadTask taskTmp = _taskList.get(i);
			if( taskTmp.taskInfo.taskId == taskId )
			{
				task = taskTmp;
				break;
			}
		}
		
		
		if( task == null )
		{
			_taskLock.unlock();
			return;
		}
		
		
		// 停止下载任务
		task.stop();
		
		// 从数据库中删除
		task.taskInfo.removeFromDB();
		
		// 删除下载文件 
		try{
			File file = new File(task.taskInfo.localFile);
			if( file.exists() )
			{
				file.delete();
			}
		}
		catch(Exception e)
		{
			
		}
		
		
		_taskList.remove(task);
		
		_taskLock.unlock();
		
		triggerTask();
		
		return;
		
	}
	
	public int addDownloadTask(DownloadTaskInfo taskInfo)
	{
		int iret = 1;
		
		DownloadTask dt = new DownloadTask(this);
		dt.taskInfo = taskInfo;
		
		_taskLock.lock();
		
		//检查一下是否任务已经存在了
		for(int i = 0;i < _taskList.size(); i++ )
		{
			DownloadTask task = _taskList.get(i);
			if( task.taskInfo.taskId == taskInfo.taskId )
			{
				return 10;
			}
		}
		
		
		// 保存到数据库中
		if( taskInfo.saveToDB() )
		{
			_taskList.add(dt);
			iret = 0;
		}
		
		_taskLock.unlock();
		
		if( iret == 0 )
			triggerTask();
		
		
		return iret;
	}
	
	
	
	public boolean isDownloading(int taskId)
	{
		boolean b = false;
		
		_taskLock.lock();
		
		//检查一下是否任务已经存在了
		for(int i = 0;i < _taskList.size(); i++ )
		{
			DownloadTask task = _taskList.get(i);
			if( task.taskInfo.taskId == taskId )
			{
				b = true;
				break;
			}
		}
		
		_taskLock.unlock();
		
		return b;
		
	}
	
	public DownloadTaskInfo getTaskInfo(int taskId)
	{
		DownloadTaskInfo dti = null;
		
		_taskLock.lock();
		
		//检查一下是否任务已经存在了
		for(int i = 0;i < _taskList.size(); i++ )
		{
			DownloadTask task = _taskList.get(i);
			if( task.taskInfo.taskId == taskId )
			{
				dti = task.taskInfo;
				break;
			}
		}
		
		_taskLock.unlock();
		
		return dti;
		
	}

	public int getTaskCount()
	{
		int cnt = 0;
		
		_taskLock.lock();
		
		cnt = _taskList.size();
		
		_taskLock.unlock();
		
		return cnt;
	}
	
	private void triggerTask()
	{
		_taskLock.lock();
		
		int idx = -1;
		int running_idx = -1;
		
		for(int i = 0; i < _taskList.size(); i++ )
		{
			DownloadTask dt = _taskList.get(i);
			if( ( dt.state == DownloadTask.STATE_NONE || dt.state == DownloadTask.STATE_DOWNLOAD_PAUSE ) && idx == -1 )
				idx = i;

			if( dt.state == DownloadTask.STATE_GETINFO 
					|| dt.state == DownloadTask.STATE_DOWNLOADING
					|| dt.state == DownloadTask.STATE_PREPARE_DOWNLOAD )
			{
				running_idx = i;
				break;
			}
		}
		
		if( running_idx == -1 && idx >= 0 )
		{
			DownloadTask dt = _taskList.get(idx);
			Log.v(AppConfig.TAG, "start download...");
			dt.start();
		}
		
		_taskLock.unlock();
	}
	
	private void onServiceInit()
	{
		// 从数据库中加载下载任务
		loadTaskFromDB();
		triggerTask();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flag, int startId)
	{
		if(intent == null )
			return super.onStartCommand(intent, flag, startId);
		Bundle b = intent.getExtras();
		if( b == null )
			return super.onStartCommand(intent, flag, startId);
		
		String command = b.getString("command");
		if( command.equals("download") )
		{
			DownloadTaskInfo taskInfo = b.getParcelable("taskinfo");
			
		
			Log.v(AppConfig.TAG, "onStartCommand: " + taskInfo.downloadUrl + " localFile:" + taskInfo.localFile);
			
			this.addDownloadTask(taskInfo);
		}
		else if( command.equals("init") )
		{
			onServiceInit();
		}
		
		return super.onStartCommand(intent, flag, startId);
	}

	@Override
	public void onDestroy()
	{
		Log.v(AppConfig.TAG, "DownloadService::onDestroy");
		
		_taskLock.lock();
		
		for(int i = 0; i < _taskList.size(); i++ )
		{
			DownloadTask task = _taskList.get(i);
			task.stop();
		}
		
		_taskLock.unlock();
		
		super.onDestroy();
	}
	
	private class DownloadTask implements Cloneable
	{
		public boolean bExitFlag = false;
		
		Context mContext;
		
		
		DownloadTaskInfo taskInfo;
		
		public int state;
		int db_state;

		//===============================
		DownloadThread _downloadThread = null;
		
		
		public static final int DOWNLOAD_MESSAGE_UPDATEUI = 1;
		public static final int DOWNLOAD_MESSAGE_FINISHED = 2;
		
		// 初始化默认状态
		public static final int STATE_NONE					= 0;
		
		// 以下3种是下载中状态
		// 准备开始下载
		public static final int STATE_PREPARE_DOWNLOAD		= 1;
		// 正在获取信息
		public static final int STATE_GETINFO				= 2;
		// 正在下载中
		public static final int STATE_DOWNLOADING			= 3;

		
		// 下载完成
		public static final int STATE_DOWNLOAD_FINISHED		= 4;
		// 任务被手工暂停（需要用户手工开始）
		public static final int STATE_DOWNLOAD_PAUSE_MANUAL	= 5;
		// 任务被自动暂停（可以自动开始）
		public static final int STATE_DOWNLOAD_PAUSE 		= 6;
		
		
		public static final int THREAD_TASK_FINISHED			= 0;
		public static final int THREAD_TASK_RETRY			= 1;
		public static final int THREAD_TASK_REDIRECT			= 10;
		public static final int THREAD_TASK_INTERRUPTED		= 100;
		
		public DownloadTask(Context context)
		{
			this.state = STATE_NONE;
			this.db_state = 0;
			
			this.mContext = context;
		}
		
		public DownloadTaskInfo getTaskInfo()
		{
			return taskInfo;
		}
		
		public boolean isRunning()
		{
			if( this.state == DownloadTask.STATE_PREPARE_DOWNLOAD ||
					this.state == DownloadTask.STATE_DOWNLOADING ||
					this.state == DownloadTask.STATE_GETINFO )
				return true;
			
			return false;
		}
		
		
		public int start()
		{
			this.state = STATE_PREPARE_DOWNLOAD;
			_downloadThread = new DownloadThread();
			_downloadThread.start();
			
			return 0;
		}
		
		public void stop()
		{
			if( _downloadThread != null )
			{
				Log.v(AppConfig.TAG, "DownloadTask stop()");
//				_downloadThread.interrupt();//
				_downloadThread.bExitFlag = true; // 让线程自己结束
			//	_downloadThread.stop(); stop方法无效
				_downloadThread = null;
			}
		}
		
		 public Object clone()
		 {
			 try {
				 return super.clone();
			 }
			 catch(Exception e)
			 {
			 	return null;
			 }
		 }
	

	
		
		private int do_download_file(DownloadThread thread)
		{
			InputStream input = null;
			
			FileOutputStream fos = null;
			
			
			if( taskInfo.totalLength > 0 )
			{
				// 先看看已经下载了多少，是否需要断点续传
				File file = new File(taskInfo.localFile);
				if( file.exists() )
				{
					long len = file.length();
					
					if( len > taskInfo.totalLength )
					{
						// 下载的文件大小比total_length还大？数据看来有问题，重新下载
						taskInfo.totalLength = 0;
						taskInfo.downloadLength = 0;
					}
					else// 正常，可以继续下载
					{
						taskInfo.downloadLength = len;
					}
				}
				else
				{// 以前下载过，但是文件不存在了，重新下载
					taskInfo.downloadLength = 0;
					taskInfo.totalLength = 0;
				}
			}
			
			if( taskInfo.totalLength == 0 )
			{
				// 第一次下载，获取文件信息（文件大小）
				taskInfo.totalLength = 0;
				taskInfo.downloadLength = 0;
				
				File file = new File(taskInfo.localFile);
				if( file.exists() )
					file.delete();
				
				
				this.state = STATE_GETINFO;
				notifyMessage(DOWNLOAD_MESSAGE_UPDATEUI, STATE_GETINFO, 0);
			}
			
			try
			{
				if( taskInfo.totalLength == 0 || this.taskInfo.downloadLength < taskInfo.totalLength )
				{
					URL url = new URL(this.taskInfo.downloadUrl);
					HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
					boolean bRange = false;
					if( taskInfo.downloadLength > 0 )
					{
						bRange = true;
						String sRange = String.format("bytes=%d-", this.taskInfo.downloadLength);
						httpConnection.setRequestProperty("RANGE", sRange);
					}
					
//					httpConnection.setRequestProperty("User-Agent", MyProfile.HTTP_USERAGENT);
					
				//	httpConnection.setDoInput(true);
					httpConnection.setConnectTimeout(1000);
					httpConnection.setRequestMethod("GET");
					httpConnection.connect();
					int respCode = httpConnection.getResponseCode();
					if(  respCode == HttpURLConnection.HTTP_MOVED_TEMP )
					{
						// 重定向了	
						String newUrl = httpConnection.getHeaderField("Location");
						taskInfo.downloadUrl = newUrl;
	
						return THREAD_TASK_REDIRECT;
					}
					
					/*
					 * 
	HTTP/1.1 206 Partial Content
	Date: Sun, 24 Apr 2011 13:16:17 GMT
	Server: Apache/2.2.17 (Unix) PHP/5.3.5
	Last-Modified: Mon, 09 Aug 2010 15:56:00 GMT
	ETag: "72c475-709939-48d660d876400"
	Accept-Ranges: bytes
	Content-Length: 7379134
	Content-Range: bytes 123-7379256/7379257
	Connection: close
	Content-Type: video/mp4
					 * 
					 */
					
					if( thread.bExitFlag )
						return THREAD_TASK_INTERRUPTED;
					
					long down_total_length = 0; // 本次需要下载的全部数据大小
					
					
					if( bRange )
					{
						if( respCode == HttpURLConnection.HTTP_PARTIAL ) // 302
						{
							// OK
							String s_content_length = httpConnection.getHeaderField("Content-Length");
							String s_content_range = httpConnection.getHeaderField("Content-Range");
							if( !s_content_range.startsWith("bytes ") )
								return THREAD_TASK_RETRY;
							
							String s = s_content_range.substring(6);
							String[] ss = s.split("\\/");
							if( ss.length != 2 )
								return THREAD_TASK_RETRY;
							
							String s_total_length = ss[1];
							taskInfo.totalLength = Long.parseLong(s_total_length);
							down_total_length = Long.parseLong(s_content_length);
						}
						else if( respCode == HttpURLConnection.HTTP_OK || respCode == 416 ) // 206
						{
							// 不支持断点续传，重新下载
							taskInfo.downloadLength = 0;
							//this.total_length = 0;
							deleteFile(taskInfo.localFile);
						}
						else
						{
							// 错误;
							return THREAD_TASK_RETRY;
						}
						
					}
					else
					{
						if( respCode != HttpURLConnection.HTTP_OK )
						{
							// 错误
							return THREAD_TASK_RETRY;
						}
						
						//  获取总长度
						String s_content_length = httpConnection.getHeaderField("Content-Length");
						taskInfo.totalLength = Long.parseLong(s_content_length);
						down_total_length = taskInfo.totalLength;
					}
	
					// 更新一下下载文件的total_length;
					taskInfo.updateTotalLength(taskInfo.totalLength);
					
					notifyMessage(DOWNLOAD_MESSAGE_UPDATEUI, 0, 0);
					// OK, 开始下载了
					
					input = httpConnection.getInputStream();
					
					File file = new File(taskInfo.localFile);
					fos = new FileOutputStream(file, true);
					
					byte[] buf = new byte[1024];
					
					long nTotal = down_total_length;
					long t1 = MyUtils.getTickCount();
					
					this.state = STATE_DOWNLOADING;
					while( nTotal > 0 )
					{
						if( thread.bExitFlag )
						{
							input.close();
							return THREAD_TASK_INTERRUPTED;
						}
						int size = buf.length;
						if( size > nTotal )
							size = (int)nTotal;
						
						int n = input.read(buf, 0, size);
						if( n <= 0 ) //!= size )
						{
							input.close();
							return THREAD_TASK_RETRY;
						}
						// 写文件
						fos.write(buf, 0, n);
						fos.flush();
						
	
						nTotal -= n;
						taskInfo.downloadLength += n;
						
//						Log.v(AppConfig.TAG, "total=" + taskInfo.totalLength + " down=" + taskInfo.downloadLength + " n=" + n);
						
						long t2 = MyUtils.getTickCount();
						if( t2 - t1 >= 500 ) // 不实时刷新，500毫秒刷新一次
						{
							notifyMessage(DOWNLOAD_MESSAGE_UPDATEUI, 0, 0);
							t1 = MyUtils.getTickCount();
						}
					}
					
					notifyMessage(DOWNLOAD_MESSAGE_UPDATEUI, 0, 0);
					
					Thread.sleep(200);
					input.close();
					input = null;
					fos.close();
					fos = null;
				}
				

				// 下载完毕
				this.state = STATE_DOWNLOAD_FINISHED;
				
				this.db_state = 1;
				taskInfo.updateState(1);
				
				notifyMessage(DOWNLOAD_MESSAGE_UPDATEUI, 0, 0);
				Thread.sleep(100); // 显示"下载完成"停留一下，然后再删除
				
				//
				notifyMessage(DOWNLOAD_MESSAGE_FINISHED, 0, 0);
				
				return THREAD_TASK_FINISHED;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try{
				if( input != null )
					input.close();
				
				if( fos != null )
					fos.close();
				}
				catch(Exception e)
				{
					
				}
			}
			return 1;
		}
		
		private void sleep(int msec)
		{
			try{
				Thread.sleep(msec);
			}
			catch(Exception e)
			{
				
			}
		}
		
		private int do_download_work(DownloadThread thread)
		{
			
			while( !this.bExitFlag )
			{
//				notifyMessage(DOWNLOAD_MESSAGE_UPDATEUI, 0, 0);
//				sleep(3000);
//				notifyMessage(DOWNLOAD_MESSAGE_REMOVE, 0, 0);
//				break;
				
				int iret = do_download_file(thread);
				if( iret == THREAD_TASK_FINISHED || iret == THREAD_TASK_INTERRUPTED )
					break;
				
				if( thread.bExitFlag )
				{
					break;
				}
				
				
				
				try{
					Thread.sleep(200);
				}
				catch(Exception e){}
			}
			
			return 0;
		}
		
		private class DownloadThread extends Thread
		{
			public boolean bExitFlag = false;
			
			public void run()
			{
				try{
				int ret = do_download_work(this);//, Thread.currentThread());
				}
				catch(Exception e)
				{
					MyUtils.showLog("thread exception");
				}
				
				
				Log.v(AppConfig.TAG, "download thread exit");
				// 给主线程发消息
			//	notifyMessage(0, 0, 0);
			}		
		}
		
		private void notifyMessage(int msgType, int arg1, int arg2)
		{
			Message msg = new Message();
			msg.what = msgType;
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			msg.obj = this;
			_handler.sendMessage(msg);
		}
	}
	
	
	Handler _handler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case DownloadTask.DOWNLOAD_MESSAGE_UPDATEUI:
				{
					DownloadTask task = (DownloadTask)msg.obj;
					
					Intent it = new Intent(ACTION_UPDATE);  
					
			        Bundle b = new Bundle();
			        b.putParcelable("task", task.getTaskInfo());
			        it.putExtras(b);
			        
			        DownloadService.this.sendBroadcast(it);  					
					
				}
				break;
			case DownloadTask.DOWNLOAD_MESSAGE_FINISHED:
				{
					// 从队列中删除
					DownloadTask task = (DownloadTask)msg.obj;
					_taskLock.lock();
					_taskList.remove(task);
					_taskLock.unlock();

					Intent it = new Intent(ACTION_FINISHED);  
			        it.putExtra("result", msg.arg1); 
			        
			        Bundle b = new Bundle();
			        b.putParcelable("task", task.getTaskInfo());
			        it.putExtras(b);
			        
			        DownloadService.this.sendBroadcast(it);  					

			        // 下一个下载
					triggerTask();
					
				}
				break;
			default:
				break;
			}
		}
	};
	
	
	public void sendInstallBroadcast(String action, String packageName)
	{
		Intent it = new Intent(action);  
		Bundle b = new Bundle();
		b.putString("packagename", packageName);
		it.putExtras(b);
        this.sendBroadcast(it);
	}
	
	
    private static DownloadService _downloadService = null;
    
    
    public static DownloadService getBindService()
    {
    	return _downloadService;
    }
    
    public static void bindService(Context context)
    {
    	Intent intent = new Intent(context, DownloadService.class);
    	
    	context.bindService(intent, _download_service_connection, Context.BIND_AUTO_CREATE);
    }
    
    public static void unbindService(Context context)
    {
    	context.unbindService(_download_service_connection);
    }
    
    private static ServiceConnection _download_service_connection = new ServiceConnection()
    {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.v(AppConfig.TAG, "onServiceConnected");
			
			_downloadService = ((DownloadService.ServiceBinder)service).getService();
			
//			if( _downloadService != null)
//			{
//				Log.v(AppConfig.TAG, "task count:" + _downloadService.getTaskCount());
//			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.v(AppConfig.TAG, "onServiceDisconnected");
			
		}
    };

    
    public void notifyDownload(Context context, int appId)
    {
    	NotifyThread nt = new NotifyThread(context, appId);
    	nt.start();
    }
    
	public class NotifyThread extends Thread
	{
		private Context _context;
		private int _appId;
		
		public NotifyThread(Context context, int appId)
		{
			_context = context;
			_appId = appId;
		}
		
		public void run()
		{
			try{
				String urlString = WAPI.getDownloadNotifyURLString(_context, _appId);
				AppConfig.showLog(urlString);
				WAPI.http_get_content(urlString);
			}
			catch(Exception e)
			{
				
			}
		}
	}	
    
	
}
