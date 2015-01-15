package com.soniq.tvmarket.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyDB {
	private static MyDB myDBInstance = null;
	
	private SQLiteDatabase mDatabase = null;
	private String mDBFilename = "my.db";
	
	public MyDB()
	{
	}
	
	public static MyDB getInstance()
	{
		if( myDBInstance == null )
		{
			myDBInstance = new MyDB();
		}
		
		return myDBInstance;
	}
	
	public int db_open(Context context)
	{
		mDatabase = context.openOrCreateDatabase(mDBFilename, Context.MODE_PRIVATE, null);
		if( mDatabase == null )
			return 1;

		create();
		
		return 0;
	}
	
	public void db_close()
	{
		mDatabase.close();
	}
	
	public void create()
	{
//		execSQL("drop table downloadtask");
		String sql = "create table if not exists downloadtask(" +
					"taskid integer primary key," +
				   "title varchar(256) not null,"+
					"packagename varchar(256) not null,"+
					"version varchar(100) not null,"+
					"versioncode varchar(20) not null default '0',"+
				   "iconurl varchar(256),"+
				   "downurl varchar(256),"+
				   "localfile varchar(256),"+
				   "totalsize integer,"+
				   "state integer,"+ // 0_download 1_download ok
				   "createtime TimeStamp NOT NULL DEFAULT (datetime('now','localtime'))"+
				   ");";

				   mDatabase.execSQL(sql);
				   
				   
				   try{
				   sql = "alter table downloadtask add column versioncode varchar(20) not null default '0'";
				   mDatabase.execSQL(sql);
				   }
				   catch(Exception e)
				   {
					   
				   }
	}
	
	public boolean execSQL(String sql)
	{
		try
		{
			mDatabase.execSQL(sql);
			Log.v(AppConfig.TAG, sql);
			return true;
		}
		catch(Exception e)
		{
			Log.v(AppConfig.TAG, "err:" + sql);
			return false;
		}
	}
	
	
	public Cursor query(String sql)
	{
		try
		{
			return mDatabase.rawQuery(sql, null);
		}
		catch(Exception e)
		{
			
		}
		
		return null;
	}
	
	public void test()
	{
		/*
		String sql = "insert into watchhistory(videoid, videotype, title, watchtime, videostore, duration, video_file)" + 
		" values(1, 1, \"abc\", \"2010-11-11\", 1, 100, \"/hyrbjc/abc.mp4\")";
		execSQL(sql);
		
		Cursor cursor = this.query("select videoid,title,watchtime from watchhistory");
		int n = cursor.getCount();
		cursor.moveToPosition(0);
		int id = cursor.getInt(0);
		String title = cursor.getString(1);
		
		Log.d("my", title);
		*/
	}
}
