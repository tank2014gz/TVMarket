package com.soniq.tvmarket.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.soniq.tvmarket.R;
import com.soniq.tvmarket.component.ImageDownloader;
import com.soniq.tvmarket.component.ImageDownloader.DownloadTask;
import com.soniq.tvmarket.component.ImageDownloader.OnDownloadTaskListener;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.AppInfo;
import com.soniq.tvmarket.data.WAPI;
import com.soniq.tvmarket.model.ClientUpgrade;
import com.soniq.tvmarket.service.DownloadService;
import com.soniq.tvmarket.service.DownloadTaskInfo;
import com.soniq.tvmarket.utils.ImageUtils;
import com.soniq.tvmarket.utils.MyUtils;
import com.soniq.tvmarket.utils.PackageUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AppDetailDialog extends Dialog implements OnDismissListener{
	
	private Context _context = null;
	private AppInfo _appInfo = null;
	
	private View _normalLayer = null;

	private View _loadingLayer = null;
	private ImageView _loadingImageView = null;
	private TextView _loadingTextView = null;
	private Animation _loadingAnimation = null;
	
	private LoadThread _loadingThread = null;
	
	
	private ImageView _imageViewIcon = null;
	private TextView _textViewTitle = null;
	private TextView _textViewInfo = null;
	private TextView _textViewDesc = null;
	private ProgressBar _progressbar = null;
	private Button _btnDownload = null;
	
	private boolean _bLoadFromServer = false;
	
	
	private ImageDownloader _imgDownloader = null;
	

	
	private static final int ACTION_DOWNLOAD = 0;
	private static final int ACTION_INSTALL = 1;
	private static final int ACTION_INSTALLING = 2;
	private static final int ACTION_UPGRADE = 3;
	private static final int ACTION_OPEN = 4;
	
	public AppDetailDialog(Context context, AppInfo appInfo) {
		super(context, R.style.dialog);
		// TODO Auto-generated constructor stub
		
		_context = context;
		_appInfo = appInfo;
		
		buildUI();
		
		updateUI();
	}
	
	public AppDetailDialog(Context context, int appId)
	{
		super(context, R.style.dialog);
		_context = context;
		_appInfo = new AppInfo();
		_appInfo.id = appId;
		
		// 从服务器加载详细数据
		_bLoadFromServer = true;

		buildUI();
		
		if( _bLoadFromServer )
			loadData();
		else
			updateUI();
	}
	
	private void buildUI()
	{
		_imgDownloader = new ImageDownloader(_context);
		
		int w = MyUtils.dip2px(_context, _context.getResources().getDimension(R.dimen.appdetail_dialog_width));
		int h = MyUtils.dip2px(_context, _context.getResources().getDimension(R.dimen.appdetail_dialog_height));
		
		Log.v(AppConfig.TAG, "width=" + w + " height=" + h);
		
		View v = LayoutInflater.from(_context).inflate(R.layout.dialog_app_detail, null);
			
		
		this.setContentView(v,
				new ViewGroup.LayoutParams(w, h));
		
		
		_normalLayer = this.findViewById(R.id.normal_layer);
		_loadingLayer = this.findViewById(R.id.loading_layer);
		
		if( _bLoadFromServer )
		{
			_loadingLayer.setVisibility(View.VISIBLE);
			_normalLayer.setVisibility(View.INVISIBLE);
		}
		else
		{
			_loadingLayer.setVisibility(View.INVISIBLE);
			_normalLayer.setVisibility(View.VISIBLE);
		}

		_loadingImageView = (ImageView)_loadingLayer.findViewById(R.id.imageViewLoading);
		_loadingTextView = (TextView)_loadingLayer.findViewById(R.id.textViewLoading);

		_imageViewIcon = (ImageView)_normalLayer.findViewById(R.id.imageViewIcon);
		_textViewTitle = (TextView)_normalLayer.findViewById(R.id.textViewTitle);
		_textViewInfo = (TextView)_normalLayer.findViewById(R.id.textViewInfo);
		_textViewDesc = (TextView)_normalLayer.findViewById(R.id.textViewDesc);
		_progressbar = (ProgressBar)_normalLayer.findViewById(R.id.progressbar);
		
		_progressbar.setMax(100);
		_progressbar.setProgress(0);
		_progressbar.setVisibility(View.GONE);
	}
	
	
	private void updateUI()
	{
		  
		_textViewTitle.setText(_appInfo.name);
		
		String size = MyUtils.GetSizeString(_appInfo.size);
		String info = String.format("版本：%s   容量：%s", _appInfo.version, size);
		_textViewInfo.setText(info);
		
		_textViewDesc.setText(_appInfo.desc);
		  

		// 图片
//		String filename = String.format("%s/%s%s", _context.getFilesDir(), 
//				AppConfig.IMAGE_CACHE_FILE_PREFIX,
//				_appInfo.getLocalIconFilename());
//		if( MyUtils.FileExist(filename) )
//		{
//			Bitmap bmp = ImageUtils.loadBitmapFromFile(filename);
//			if( bmp != null )
//			{
//				bmp = ImageUtils.getRoundedCornerBitmap(bmp, 15);
//				_imageViewIcon.setImageBitmap(bmp);
//			}
//		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appinfo", _appInfo);
		params.put("view", _imageViewIcon);
		params.put("roundcorner", 15);
		
		_imgDownloader.downloadImage(_context, _appInfo.icon, new OnDownloadTaskListener() {
			
			@Override
			public void onDownloadSuccessfully(DownloadTask downloadTask) {
				// TODO Auto-generated method stub
					_imageViewIcon.setImageBitmap(downloadTask.getBitmap());
			}

			@Override
			public void onDownloadFailed(int errCode,
					DownloadTask downloadTask) {
				// TODO Auto-generated method stub
				
			}
		}, params);
				
		
		
		Button btn = (Button)_normalLayer.findViewById(R.id.btn_appdlg_cancel);
		btn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
			
		});
		
		
		_btnDownload = (Button)_normalLayer.findViewById(R.id.btn_appdlg_install);
		_btnDownload.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// 下载
				doDownload(v);
			}
			
		});
		
		
		
		// 是否已经下载过
		boolean bInstalled = false;
		boolean bDownloaded = false;
		boolean bUpgrade = false;
        String apkfilename = MyUtils.get_filename_from_url(_appInfo.downurl, true);
        apkfilename = AppConfig.getApkDirName(_context) + "/" + apkfilename;
        File file = new File(apkfilename);
        if( file.exists() )
        	bDownloaded = true;
        
        try{
        	PackageInfo pkgInfo = _context.getPackageManager().getPackageInfo(_appInfo.pkgname, 0);
        	
        	// 已经安装过，再比较版本
        	bInstalled = true;
        	String version = String.format("%d", pkgInfo.versionCode);
        	
        	int n = MyUtils.compareVersionString(_appInfo.versionCode, version);
        	if( n > 0 )
        	{
        		// 可以更新
        		bUpgrade = true;
        		
        	}
        }
        catch(PackageManager.NameNotFoundException e)
        {
        	// 没安装
        }
        catch(Exception e)
        {
        	// 当做没安装
        }
        

        if( bUpgrade )
        {
        	if( bDownloaded )
        	{
        		// 安装更新
        	}
        	else
        	{
        		// 下载更新
        	}
        }
        else if( bInstalled )
        {
        	// 打开/卸载
        }
        else
        {
        	if( bDownloaded )
        	{
        		// 安装
        	}
        	else
        	{
        		// 下载安装
        	}
        }
        
		
		if( DownloadService.getBindService() !=  null )
		{
			if( DownloadService.getBindService().isDownloading(_appInfo.id) ) // 正在下载
			{
				_btnDownload.setVisibility(View.GONE);
				_progressbar.setVisibility(View.VISIBLE);
			}
			else
			{
				if( bUpgrade )
				{
					// 显示 更新 按钮
					_btnDownload.setText(_context.getResources().getString(R.string.title_btn_upgrade));
					_btnDownload.setTag(ACTION_UPGRADE);
				}
				else
				{
					if( bInstalled )
					{
						// 显示打开按钮
						_btnDownload.setText(_context.getResources().getString(R.string.title_btn_open));
						_btnDownload.setBackgroundResource(R.drawable.bg_btn_orange_selector);
						_btnDownload.setTag(ACTION_OPEN);
					}
					else
					{
						// 显示下载安装按钮
						_btnDownload.setText(_context.getResources().getString(R.string.title_btn_install));
						_btnDownload.setTag(ACTION_DOWNLOAD);
					}
				}
				
				_progressbar.setVisibility(View.GONE);
			}
		}
		
		
		this.setOnDismissListener(this);
		
		createReceiver();
	}
	
	
	
	public static AppDetailDialog showDetailDialog(Context context, AppInfo appInfo, OnAppDetailDismissListener listener)
	{
		AppDetailDialog dlg = new AppDetailDialog(context, appInfo);
		dlg.show();
		
		return dlg;
	}

	public static AppDetailDialog showDetailDialog(Context context, int appId, OnAppDetailDismissListener listener)
	{
		AppDetailDialog dlg = new AppDetailDialog(context, appId);
		dlg.show();
		
		return dlg;
	}

	
	private OnAppDetailDismissListener _onAppDetailDismissListener = null;
	
	public void setOnAppDetailDismissListener(OnAppDetailDismissListener listener)
	{
		_onAppDetailDismissListener = listener;
	}
	
	public interface OnAppDetailDismissListener
	{
		public void onAppDetailDismiss();
	};

	@Override
	public void onDismiss(DialogInterface dialog) {
		destroyReceiver();
		
		if( _onAppDetailDismissListener != null )
			_onAppDetailDismissListener.onAppDetailDismiss();
	}
	
	
	public void onResume()
	{
		// 检查一下安装结果
		int state = PackageUtils.isPackageExistedWithVersionCode(_appInfo.pkgname, _appInfo.versionCode, _context);
		
		AppConfig.showLog("state=" + state);
		
		if( state == 0 )
		{
			// 安装过了
			DownloadService.getBindService().sendInstallBroadcast(DownloadService.ACTION_INSTALL_SUCCESSED, _appInfo.pkgname);
		}
		else if( state == 1 )
		{
			// 更新
			_btnDownload.setText(_context.getResources().getString(R.string.title_btn_upgrade));
			_btnDownload.setTag(ACTION_UPGRADE);
		}
		else
		{
			// 安装
			DownloadService.getBindService().sendInstallBroadcast(DownloadService.ACTION_INSTALL_FAILED, _appInfo.pkgname);
		}
	}
	
	//================================================================
	// 下载相关


	private void doDownload(View v)
	{
		int action = ((Integer)v.getTag()).intValue();
		
		if( action == ACTION_INSTALLING )
			return;
		
		if( action == ACTION_OPEN )
		{
			try{
		    Intent LaunchIntent = _context.getPackageManager().getLaunchIntentForPackage(_appInfo.pkgname);  
		    _context.startActivity(LaunchIntent);
			}
			catch(Exception e){}

			return;
		}
		
		// 判断如果已经下载了，直接安装
		
        DownloadTaskInfo taskInfo = new DownloadTaskInfo();
        taskInfo.taskId = _appInfo.id;
        taskInfo.title = _appInfo.name;
        taskInfo.iconUrl = _appInfo.icon;
        taskInfo.packageName = _appInfo.pkgname;
        taskInfo.version = _appInfo.version;
        taskInfo.versionCode = _appInfo.versionCode;
        taskInfo.downloadUrl = _appInfo.downurl;
        String filename = MyUtils.get_filename_from_url(taskInfo.downloadUrl, true);
        taskInfo.localFile = AppConfig.getApkDirName(_context) + "/" + filename;
        
        
        if( action != ACTION_UPGRADE && taskInfo.getState() == DownloadTaskInfo.STATE_DOWNLOADED )
        {
        	// 下载完成
        	if( MyUtils.FileExist(taskInfo.localFile) )
        	{
//        		PackageUtils.installApk((Activity)_context, taskInfo.localFile, 0, null);
        		MainActivity.getInstance().installApk(taskInfo.localFile, taskInfo.packageName);
        		return;
        	}
        }
        
        DownloadService.startDownloadService(_context, taskInfo);
        
        _btnDownload.setVisibility(View.GONE);
        _progressbar.setVisibility(View.VISIBLE);
	}
	
//	
//    private DownloadService _downloadService = null;
//    
//    private void bindDownloadService()
//    {
//    	Intent intent = new Intent(_context, DownloadService.class);
//    	
//    	_context.bindService(intent, _download_service_connection, Context.BIND_AUTO_CREATE);
//    }
//    
//    private ServiceConnection _download_service_connection = new ServiceConnection()
//    {
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			// TODO Auto-generated method stub
//			Log.v(AppConfig.TAG, "onServiceConnected");
//			
//			_downloadService = ((DownloadService.ServiceBinder)service).getService();
//			
//			if( _downloadService != null)
//			{
//				Log.v(AppConfig.TAG, "task count:" + _downloadService.getTaskCount());
//				
//				if( _downloadService.isDownloading(_appInfo.id) )
//				{
//					// 正在下载中...
//					_btnDownload.setVisibility(View.GONE);
//					_progressbar.setVisibility(View.VISIBLE);
//				}
//				else
//				{
//					// 不在下载
//					_btnDownload.setVisibility(View.VISIBLE);
//					_progressbar.setVisibility(View.GONE);
//					
//				}
//			}
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			// TODO Auto-generated method stub
//			Log.v(AppConfig.TAG, "onServiceDisconnected");
//			
//		}
//    };
//    
//    
    
    private BroadcastReceiver _downloadReceiver = new BroadcastReceiver(){  
        @Override  
        public void onReceive(Context context, Intent intent) 
        {  
            if( intent.getAction().equals(DownloadService.ACTION_UPDATE) )
            {  
            	Log.v(AppConfig.TAG, "receive ACTION_UPDATE");
            	
            	Bundle b = intent.getExtras();
            	DownloadTaskInfo task = (DownloadTaskInfo)b.getParcelable("task");
            	if( task.taskId != _appInfo.id ) // 不是这个软件的下载信息
            		return;
            	
            	if( task.totalLength > 0 )
            	{
	            	int n = (int)(task.downloadLength * 100 / task.totalLength);
	            	
	            	_progressbar.setProgress(n);
            	}
            	
            	Log.d(AppConfig.TAG, "updateUI: " + task.title + " " + task.downloadLength);
            	
            }
            else if( intent.getAction().equals(DownloadService.ACTION_FINISHED) )
            {  
            	Log.v(AppConfig.TAG, "receive ACTION_FINISHED");
            	
 
            	Bundle b = intent.getExtras();
            	DownloadTaskInfo task = (DownloadTaskInfo)b.getParcelable("task");
            	
            	if( task.taskId != _appInfo.id ) // 不是这个软件的下载信息
            		return;
            	
            	Log.d(AppConfig.TAG, "download " + task.title + " finished");
            	

//            	AppDetailDialog.this.dismiss();
            } 
            else if( intent.getAction().equals(DownloadService.ACTION_INSTALLING ))
            {
            	Bundle b = intent.getExtras();
            	String pkgname = b.getString("packagename");
            	if( !_appInfo.pkgname.equals(pkgname) )
            		return;
            	
            	_progressbar.setVisibility(View.GONE);
            	_btnDownload.setText("正在安装...");
            	_btnDownload.setTag(ACTION_INSTALLING);
            	_btnDownload.setVisibility(View.VISIBLE);
            	_btnDownload.requestFocus();
            }
            else if( intent.getAction().equals(DownloadService.ACTION_INSTALL_SUCCESSED ))
            {
            	Bundle b = intent.getExtras();
            	String pkgname = b.getString("packagename");
            	if( !_appInfo.pkgname.equals(pkgname) )
            		return;
            	
            	_progressbar.setVisibility(View.GONE);
            	_btnDownload.setText(_context.getResources().getString(R.string.title_btn_open));
            	_btnDownload.setBackgroundResource(R.drawable.bg_btn_orange_selector);
            	_btnDownload.setTag(ACTION_OPEN);
            	_btnDownload.setVisibility(View.VISIBLE);
            	_btnDownload.requestFocus();
            }
            else if( intent.getAction().equals(DownloadService.ACTION_INSTALL_FAILED ))
            {
            	Bundle b = intent.getExtras();
            	String pkgname = b.getString("packagename");
            	if( !_appInfo.pkgname.equals(pkgname) )
            		return;
            	
            	_progressbar.setVisibility(View.GONE);
            	_btnDownload.setText(_context.getResources().getString(R.string.title_btn_install));
            	_btnDownload.setTag(ACTION_INSTALL);
            	_btnDownload.setVisibility(View.VISIBLE);
            	_btnDownload.requestFocus();
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
	    _context.registerReceiver(_downloadReceiver, filter);    
    }
    
    public void destroyReceiver()
    {
	    if( _downloadReceiver != null )
	    {
	    	_context.unregisterReceiver(_downloadReceiver);
	    	_downloadReceiver = null;
	    }
    }
    
    
    private void loadData()
	{
		_loadingAnimation = AnimationUtils.loadAnimation(
				_context, R.anim.loading_animation);
		// 使用ImageView显示动画
		_loadingImageView.startAnimation(_loadingAnimation);
		
		_loadingThread = new LoadThread();
		_loadingThread.start();
	}
	
	public int doLoadData()
	{
		String urlString = WAPI.getAppInfoURLString(_context, _appInfo.id);
		AppConfig.showLog(urlString);
		String content = WAPI.http_get_content(urlString);
		if( content == null || content.length() < 1)
			return 1;
		
		
		int iret = WAPI.parseAppInfoJSONResponse(_context, content, _appInfo);
		if( iret != 0 )
			return iret;
		

		// 成功了
		return 0;
	}
	
    
    private class LoadThread extends Thread{
    	public void run()
		{
			int iret = 1;
			try
			{
				iret = doLoadData();
			}
			catch(Exception e)
			{
				
			}
			
			Message msg = new Message();
			msg.what = iret;
			handler.sendMessage(msg);
		}    	
    }
    
    Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			if( msg.what == 0 )
			{
				// 加载成功
				_loadingAnimation.cancel();
				_loadingImageView.setAnimation(null);
				updateUI();
				_loadingLayer.setVisibility(View.INVISIBLE);
				_normalLayer.setVisibility(View.VISIBLE);
			}
			else
			{
				_loadingAnimation.cancel();
				_loadingImageView.setAnimation(null);
				_loadingImageView.setVisibility(View.GONE);
				_loadingTextView.setText("加载数据失败，请稍后再试一次！");
			}
		}
	};	
	    
}
