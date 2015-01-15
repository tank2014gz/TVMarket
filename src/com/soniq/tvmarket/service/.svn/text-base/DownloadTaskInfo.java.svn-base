package com.soniq.tvmarket.service;

import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.MyDB;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


public class DownloadTaskInfo implements Parcelable
{
	public final static int STATE_DOWNLOADING = 0;
	public final static int STATE_DOWNLOADED = 1;
	
	public int taskId;
	public String title;
	public String packageName;
	public String version;
	public String versionCode;
	public String iconUrl;
	public String downloadUrl;
	public String localFile;
	public long totalLength;
	public long downloadLength;
	
	public int state;
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(taskId);
		dest.writeString(title);
		dest.writeString(packageName);
		dest.writeString(version);
		dest.writeString(versionCode);
		dest.writeString(iconUrl);
		dest.writeString(downloadUrl);
		dest.writeString(localFile);
		dest.writeLong(totalLength);
		dest.writeLong(downloadLength);
		
		dest.writeInt(state);
	}
	
	public static final Parcelable.Creator<DownloadTaskInfo> CREATOR = new Creator<DownloadTaskInfo>() {  
        public DownloadTaskInfo createFromParcel(Parcel source) {  
        	DownloadTaskInfo dt = new DownloadTaskInfo();  
        	dt.taskId = source.readInt();
        	dt.title = source.readString();
        	dt.packageName = source.readString();
        	dt.version = source.readString();
        	dt.versionCode = source.readString();
        	dt.iconUrl = source.readString();
        	dt.downloadUrl = source.readString();
        	dt.localFile = source.readString();
        	dt.totalLength = source.readLong();
        	dt.downloadLength = source.readLong();
        	
        	dt.state = source.readInt();
        	
            return dt;  
        }  
        public DownloadTaskInfo[] newArray(int size) {  
            return new DownloadTaskInfo[size];  
        }  
    }; 
    
    public boolean updateState(int state)
    {
    	String sql = String.format("update downloadtask set state = %d where taskid = %d", state, taskId);
    	return MyDB.getInstance().execSQL(sql);
    }
    public boolean updateTotalLength(long length)
    {
    	String sql = String.format("update downloadtask set totalsize = %d where taskid = %d", length, taskId);
    	return MyDB.getInstance().execSQL(sql);
    }
    
    public int getState()
    {
    	String sql = String.format("select * from downloadtask where taskid = %d",  taskId);
		Cursor cursor = MyDB.getInstance().query(sql);
		if( cursor == null )
			return -1;
		
		if( !cursor.moveToFirst() )
		{
			cursor.close();
			return -1;
		}
		
		try{
		
			int state = cursor.getInt(cursor.getColumnIndex("state"));
			
			return state;
		}
		catch(Exception e)
		{
		}
		finally
		{
			cursor.close();
		}
		
		return -1;
		
    }
    
    
    public boolean removeFromDB()
    {
    	try{
    		String sql = String.format("delete from downloadtask where taskid = %d", this.taskId);
    		return MyDB.getInstance().execSQL(sql);
    	}
    	catch(Exception e)
    	{
    		
    	}
    	
    	return true;
    }
    
    public boolean saveToDB()
    {
    	try
    	{
    		String sql = String.format("delete from downloadtask where taskid = %d", this.taskId);
    		MyDB.getInstance().execSQL(sql);
    		
    		sql = String.format("insert into downloadtask(taskid,title,packagename,version,versioncode,iconurl,downurl,localfile,totalsize,state) values("
    				+ "%d, '%s', '%s','%s','%s','%s', '%s', '%s',0, 0)",
    				this.taskId,
    				this.title,
    				this.packageName,
    				this.version,
    				this.versionCode,
    				this.iconUrl,
    				this.downloadUrl,
    				this.localFile
    				);
    		
    		return MyDB.getInstance().execSQL(sql);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return false;
    }
			
}
