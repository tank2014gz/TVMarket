package com.soniq.tvmarket.component;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.soniq.tvmarket.utils.ImageUtils;
import com.soniq.tvmarket.utils.MD5Util;
import com.soniq.tvmarket.utils.MyUtils;

public class ImageDownloader {
	
	public final static String defaultFilePrefix = "imgcache-";
	private String _prefix = defaultFilePrefix;
	
	private Context _context = null;
	private String _savePath = "";
	private int mMaxConcurrentTaskCount = 1;
	private final ArrayList<DownloadTask> mTaskList = new ArrayList<DownloadTask>();
	private final ReentrantLock mLock = new ReentrantLock();
	
	private Map<String, SoftReference<Bitmap>> _imageCaches = new HashMap<String, SoftReference<Bitmap>>();
	
	public ImageDownloader(Context context)
	{
		_context = context;
		
		// 默认就保存在私有目录下
		_savePath = String.format("%s", context.getFilesDir());
	}
	
	public void setImageFilePrefix(String prefix)
	{
		_prefix = prefix;
	}
	
	public boolean setImageSavePath(Context ctx, String path)
	{
		File file = new File(path);
		if( file.exists() )
			return true;
		
		return false;
	}
	
	public static Bitmap loadBitmapFromFile(Context context, String filename)
	{
		try
		{
			FileInputStream fis = new FileInputStream(filename);
			
			ByteArrayOutputStream outStream=new ByteArrayOutputStream();
	        byte[] buffer=new byte[1024];
	        int len = 0;
	        while ((len=fis.read(buffer))!=-1){
	            outStream.write(buffer, 0, len);
	        }
	        outStream.close();
	        
	        byte[] data=outStream.toByteArray();
	        
	        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//	        Drawable frame = new BitmapDrawable(context.getResources(), bmp);	        
//	        
////	        Bitmap bmp = BitmapFactory.decodeFile("filename”) ;
////	                Drawable frame = new BitmapDrawable(bmp);	        
	        
	        return bmp;			
			
		}
		catch(Exception e)
		{
			
		}
		
		return null;
	}
	
	public static int downloadFile(Context context, String remote_file, String local_file)
	{
		
		try
		{
			URL url = new URL(remote_file);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(1000);
			conn.setRequestMethod("GET");
			conn.connect();
			if( conn.getResponseCode() == HttpURLConnection.HTTP_OK )
			{
				InputStream is = conn.getInputStream();
				
	    		FileOutputStream fos = new FileOutputStream(local_file);
	    		
	    		byte[] buffer = new byte[1024];
	    		int len = 0;
	    		
	    		while( (len = is.read(buffer)) != -1 )
	    		{
	    			fos.write(buffer, 0, len);
	    		}
	    		
	    		fos.close();
	    		
	    		return 0;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return 1;
	}

	// 本类中不用这个函数
	public static Bitmap getBitmap(String image_url)
	{
		try
		{
			URL url = new URL(image_url);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(1000);
			conn.setRequestMethod("GET");
			conn.connect();
			if( conn.getResponseCode() == HttpURLConnection.HTTP_OK )
			{
				InputStream is = conn.getInputStream();
				
				Bitmap bmp = BitmapFactory.decodeStream(is, null, null);
				if( is != null )
				{
					is.close();
					is = null;
				}
				
				if( conn != null )
				{
					conn.disconnect();
					conn = null;
				}
				
				return bmp;
			}
		}
		catch(Exception e)
		{
			
		}
		
		return null;
	}
	
	private String getLocalImageFileNameByUrl(String url, String prefix)
	{
		if( url == null )
			return null;
		
		
		String ext = MyUtils.get_filename_ext_from_url(url);

		String s;
		if( ext != null && ext.length() > 0 )
			s = String.format("%s%s.%s", prefix, MD5Util.getMD5Encoding(url), ext);
		else
			s = String.format("%s%s", prefix, MD5Util.getMD5Encoding(url));
			
		
		s = String.format("%s/%s", _savePath, s);
		
		return s;
	}
	
//	public Bitmap getRoundCornerDownloadedImage(String url, int roundcorner)
//	{
//		String filename = getLocalImageFileNameByUrl(url, _prefix);
//		String key = MD5Util.getMD5Encoding(url);
//		
//		Bitmap bmp = null;
//		SoftReference<Bitmap> softBitmap = _imageCaches.get(key);
//		if( softBitmap != null )
//		{
//			bmp = softBitmap.get();
//			if( bmp == null )
//				_imageCaches.remove(key);
//		}
//		
//		if( bmp == null )
//		{
//			// cache里不存在, 看看文件是否存在
//			if( MyUtils.FileExist(filename) )
//			{
//				// 图片文件存在
//				bmp = loadBitmapFromFile(_context, filename);
//				if( bmp != null )
//				{
//					// 加入到cache里
//					if( roundcorner > 0 )
//						{
//							bmp = ImageUtils.getRoundedCornerBitmap(bmp, roundcorner);
//						}
//						_imageCaches.put(key,  new SoftReference<Bitmap>(bmp));  				
//				}
//				else
//				{
//					MyUtils.deleteFile(filename);
//				}
//			}
//		}
//		
//		return bmp;
//	}
	
	
	public int downloadImage(Context context, String remote_url, 
			OnDownloadTaskListener listener,
			Map<String,Object> params)
	{
		
		
		Bitmap bmp = null;
		
		String filename = getLocalImageFileNameByUrl(remote_url, _prefix);
		
		String key = MD5Util.getMD5Encoding(remote_url);
		
		
		DownloadTask task = new DownloadTask(context, remote_url, filename, listener, params);
		
		
		SoftReference<Bitmap> softBitmap = _imageCaches.get(key);
		if( softBitmap != null )
		{
			bmp = softBitmap.get();
			if( bmp == null )
			{
				_imageCaches.remove(key);
			}
		}
		else
		{
			// cache里不存在, 看看文件是否存在
			if( MyUtils.FileExist(filename) )
			{
				// 图片文件存在
				bmp = loadBitmapFromFile(_context, filename);
				if( bmp != null )
				{
					// 加入到cache里
					int roundcorner = task.getRoundCornerParam();
					if( roundcorner > 0 )
						{
							bmp = ImageUtils.getRoundedCornerBitmap(bmp, roundcorner);
						}
					_imageCaches.put(key,  new SoftReference<Bitmap>(bmp));  				
				}
				else
				{
					MyUtils.deleteFile(filename);
				}
			}
		}
		

		if( bmp != null )
		{
			// 不需要下载
			if( listener != null )
			{
				task.setBitmap(bmp);
				
				listener.onDownloadSuccessfully(task);
			}
			
			return 0;
		}
		
		
		// 需要下载
		return addTask(task);
		
	}	
	private void doDownloadThreadFunc(Thread thread, DownloadTask task)
	{
		int ret = 1;
		// 需要下载的URL
		String imgUrl = task.getImageUrl();
		System.out.println("downloading " + imgUrl);
		
		String key = MD5Util.getMD5Encoding(imgUrl);
		
		
		// 2次尝试
		for(int i = 0; i < 2; i ++ )
		{
			if( task.mSaveFilename == null || task.mContext == null )
				break;
			
			int iret = downloadFile(task.mContext, imgUrl, task.mSaveFilename);
			if( iret == 0 )
			{// 下载成功
				
				Bitmap bmp = loadBitmapFromFile( task.mContext, task.mSaveFilename );
				if( bmp != null )
				{
					// 加入到cache里
					int roundcorner = task.getRoundCornerParam();
					if( roundcorner > 0 )
					{
						bmp = ImageUtils.getRoundedCornerBitmap(bmp, roundcorner);
					}
					
					_imageCaches.put(key,  new SoftReference<Bitmap>(bmp));
					
					task.setBitmap(bmp);
					ret = 0;
					break;
				}
			}
		}
		
		// 执行完毕
		Message msg = new Message();
		msg.what = ret;
		msg.obj = task;
		handler.sendMessage(msg);			
	}
	
	
	// 下载线程
	private class DownloadThread extends Thread
	{
		public DownloadTask _taskObject = null;
		
		public DownloadThread(DownloadTask task)
		{
			_taskObject = task;
		}
		
		public void run(){
			
			
			doDownloadThreadFunc(this, _taskObject);
		}	
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			if( msg.obj != null )
			{
				DownloadTask taskObject = (DownloadTask)msg.obj;

				// 从队列中删除
				mLock.lock();
				
				mTaskList.remove(taskObject);
				
				// 如果队列中又排队等待下载的任务，开始下载
				for(int i = 0; i < mTaskList.size();i++)
				{
					DownloadTask t = mTaskList.get(i);
					if( t != null && !t.isRunning() && !t.isCanceled() )
					{
						t.start();
						break;
					}
				}
				
				mLock.unlock();
				
				
				if( !taskObject.mbCanceled )
				{				
					OnDownloadTaskListener listener = taskObject.getOnDownloadTaskListener();
					if( listener != null )
					{
						if( msg.what == 0 )
							listener.onDownloadSuccessfully(taskObject);
						else
							listener.onDownloadFailed(msg.what, taskObject);
					}
				}
			}
			
		}
	};		
	
	public void setMaxConcurrentTaskCount(int nMax)
	{
		mMaxConcurrentTaskCount = nMax;
	}
	
	public void cancelAll()
	{
		mLock.lock();

		for(int i = 0; i < mTaskList.size(); i++ )
		{
			DownloadTask t = mTaskList.get(i);
			if( t != null )
			{
				t.cancel();
			}
		}
		
		mLock.unlock();
	}
	
	public int addTask(DownloadTask task)
	{
		mLock.lock();
		int nCnt = 0;
		for(int i = 0; i < mTaskList.size(); i++ )
		{
			DownloadTask t = mTaskList.get(i);
			if( t != null && t.isRunning() )
				nCnt++;
		}
		
		mTaskList.add(task);
		
		
		if( nCnt < mMaxConcurrentTaskCount )
		{
			task.start();
		}
		
		mLock.unlock();
		
		return 0;
	}
	
	public interface OnDownloadTaskListener {
	        void onDownloadSuccessfully(DownloadTask downloadTask);
	        void onDownloadFailed(int errCode, DownloadTask downloadTask);
	}
	
	
	
	   
	public class DownloadTask{
		private boolean mbCanceled = false;
		private boolean mIsRunning = false;
		private DownloadThread mThread = null;
		private OnDownloadTaskListener onDownloadTaskListener = null;
		private String mImageUrl = null;
		private String mSaveFilename = null;
		private Bitmap mBitmap = null;
		private Context mContext = null;
				
		public Map<String,Object> params = null;

		public void setBitmap(Bitmap d)
		{
			mBitmap = d;
		}
		
		public Bitmap getBitmap()
		{
			return mBitmap;
		}
		
		public int getRoundCornerParam()
		{
				Integer n = (Integer)params.get("roundcorner");
				if( n == null )
					return 0;
				return n.intValue();
		}
		
		public DownloadTask(Context context, String url,  String save_filename, 
				OnDownloadTaskListener l, Map<String,Object> map)
		{
			mContext = context;
			mImageUrl = url;
			mSaveFilename = save_filename;
			onDownloadTaskListener = l;

			params = map;
		}
		
		
		public String getImageUrl()
		{
			return mImageUrl;
		}
		
		public boolean isRunning()
		{
			return mIsRunning;
		}
		
		public void start()
		{
			mbCanceled = false;
			mIsRunning = true;
			mThread = new DownloadThread(this);
			mThread.start();
		}
		
		public void cancel()
		{
			mbCanceled = true;
			if( mThread != null )
				mThread.interrupt();
		}
		
		public boolean isCanceled()
		{
			return mbCanceled;
		}
		
	
		
		public OnDownloadTaskListener getOnDownloadTaskListener()
		{
			return onDownloadTaskListener;
		}
		
	}
	

}
