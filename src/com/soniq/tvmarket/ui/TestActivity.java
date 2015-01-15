package com.soniq.tvmarket.ui;

import java.io.File;

import com.soniq.tvmarket.R;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.service.DownloadService;
import com.soniq.tvmarket.service.DownloadTaskInfo;
import com.soniq.tvmarket.utils.MyUtils;

import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class TestActivity extends Activity {
	
	private ImageView _imgView;
	 private Intent _downloadIntent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		
		 _imgView = (ImageView)this.findViewById(R.id.imageView1);
		 
		Button btn = (Button)this.findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				_imgView.setScaleType(ScaleType.MATRIX);
				Matrix mx = new Matrix();
				
				mx.setScale(1.5f, 1.5f);
				
				_imgView.setImageMatrix(mx);
				
				
				
//				Rect rc = new Rect();
//				rc.left = _imgView.getLeft();
//				rc.top = _imgView.getTop();
//				rc.right = _imgView.getRight();
//				rc.bottom = _imgView.getBottom();
//				
//				_imgView.layout(rc.left - 50, rc.top - 50, rc.right + 50, rc.bottom + 50);
//				
//				_imgView.invalidate();
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}
	
	
	
	@Override
	public void onDestroy()
	{
    	// 可以不调用，自己会unbind的
//    	unbindService(conn);
    	
    	
    	if( _downloadIntent != null )
    	{
    		this.stopService(_downloadIntent);
    		_downloadIntent = null;
    	}
    	
    	
//    	destroyReceiver();
    	
		
		super.onDestroy();
	}
	
	 
    void test()
    {
    	
        // 
        bindDownloadService();
    	
        createReceiver();
        
        Intent  intent;
        
        DownloadTaskInfo taskInfo = new DownloadTaskInfo();
        taskInfo.taskId = 1;
        taskInfo.title = "iDisplay";
        taskInfo.downloadUrl = "http://192.168.1.100/tt.apk";
        String filename = MyUtils.get_filename_from_url(taskInfo.downloadUrl, true);
        taskInfo.localFile = AppConfig.getApkDirName(this) + "/" + filename;
        
        _downloadIntent = DownloadService.startDownloadService(this, taskInfo);
    }
    
    
    private DownloadService _downloadService = null;
    
    private void bindDownloadService()
    {
    	Intent intent = new Intent(this, DownloadService.class);
    	
    	this.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }
    
    
    private ServiceConnection conn = new ServiceConnection()
    {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.v(AppConfig.TAG, "onServiceConnected");
			
			_downloadService = ((DownloadService.ServiceBinder)service).getService();
			
			if( _downloadService != null)
			{
				Log.v(AppConfig.TAG, "task count:" + _downloadService.getTaskCount());
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.v(AppConfig.TAG, "onServiceDisconnected");
			
		}
    };
    
     
    // for test
    private BroadcastReceiver _downloadReceiver = new BroadcastReceiver(){  
        @Override  
        public void onReceive(Context context, Intent intent) 
        {  
            if( intent.getAction().equals(DownloadService.ACTION_UPDATE) )
            {  
            	Log.v(AppConfig.TAG, "receive ACTION_UPDATE");
            	
            	Bundle b = intent.getExtras();
            	DownloadTaskInfo task = (DownloadTaskInfo)b.getParcelable("task");
            	
            	Log.d(AppConfig.TAG, "updateUI: " + task.title + " " + task.downloadLength);
            	
//                int progress = intent.getIntExtra("progress", 0);  
//                Log.d(TAG, "myReceiver - progress = " + progress);  
            }
            else if( intent.getAction().equals(DownloadService.ACTION_FINISHED) )
            {  
            	Log.v(AppConfig.TAG, "receive ACTION_FINISHED");
//                boolean isSuccess = intent.getBooleanExtra("success", false);  
//                Log.d(TAG, "myReceiver - success = " + isSuccess);  
            	
 
            	Bundle b = intent.getExtras();
            	DownloadTaskInfo task = (DownloadTaskInfo)b.getParcelable("task");
            	
            	
            	Log.d(AppConfig.TAG, "download " + task.title + " finished");
            	
            	
            	
            	File file = new File(AppConfig.getApkDirName(TestActivity.this) + "/tt.apk");
            	MyUtils.execCmd("chmod 777 " + file.toString());
            	
            	
            	Log.v(AppConfig.TAG, file.toString());
            	
        		Intent nn = new Intent(Intent.ACTION_VIEW);
        		nn.setDataAndType(Uri.parse("file://"+file.toString()), "application/vnd.android.package-archive");
        		nn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 

        		
        		
        		
//            	Intent nn = new Intent(Intent.ACTION_VIEW);
//            	String path = "file://" + getFilesDir() + "/tt.apk";
//            	Log.v(AppConfig.TAG, path);
//            	nn.setDataAndType(Uri.parse(path), "application/vnd.android.package-archive");
            	TestActivity.this.startActivity(nn);
            }  
        }  
    };      
    
    
    public void createReceiver()
    {
    	
    	IntentFilter filter = new IntentFilter();  
	    filter.addAction(DownloadService.ACTION_UPDATE);  
	    filter.addAction(DownloadService.ACTION_FINISHED);  
	    registerReceiver(_downloadReceiver, filter);    
	    
    }
    
    public void destroyReceiver()
    {
	    if( _downloadReceiver != null )
	    {
	    	this.unregisterReceiver(_downloadReceiver);
	    	_downloadReceiver = null;
	    }
    }
    	

}
