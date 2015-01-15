package com.soniq.tvmarket.data;

import java.io.Serializable;

import com.soniq.tvmarket.utils.MD5Util;
import com.soniq.tvmarket.utils.MyUtils;

import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo implements Parcelable{
	public int id;
	public String name;
	public String py;
	public int classid;
	public String classname;
	public String version;
	public String versionCode;
	public String pkgname;
	public int size;
	public String icon;
	public String downurl;
	public String desc;

	
	public String localFile;


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(py);
		dest.writeInt(classid);
		dest.writeString(classname);
		dest.writeString(version);
		dest.writeString(versionCode);
		dest.writeString(pkgname);
		dest.writeInt(size);
		dest.writeString(icon);
		dest.writeString(downurl);
		dest.writeString(desc);
		
		dest.writeString(localFile);
		
	}
	
	public static final Parcelable.Creator<AppInfo> CREATOR = new Creator<AppInfo>() {  
        public AppInfo createFromParcel(Parcel source) {  
        	AppInfo appInfo = new AppInfo();  
        	appInfo.id = source.readInt();
        	appInfo.name = source.readString();
        	appInfo.py = source.readString();
        	appInfo.classid = source.readInt();
        	appInfo.classname = source.readString();
        	appInfo.version = source.readString();
        	appInfo.versionCode = source.readString();
        	appInfo.pkgname = source.readString();
        	appInfo.size = source.readInt();
        	appInfo.icon = source.readString();
        	appInfo.downurl = source.readString();
        	appInfo.desc = source.readString();
        	appInfo.localFile = source.readString();
        	
            return appInfo;  
        }  
        public AppInfo[] newArray(int size) {  
            return new AppInfo[size];  
        }  
    };  	
	
	
    
    public String getLocalIconFilename()
    {
    	String ext = MyUtils.get_filename_ext_from_url(this.icon);
    	String fname = MD5Util.getMD5Encoding(this.icon);
    	
    	
    	if( ext.length() > 0 )
    		return String.format("%s.%s", fname, ext);
    	else
    		return String.format("%s", fname);
    }

    
    public static String getLocalIconFilename(String iconUrl)
    {
    	String ext = MyUtils.get_filename_ext_from_url(iconUrl);
    	String fname = MD5Util.getMD5Encoding(iconUrl);
    	
    	
    	if( ext.length() > 0 )
    		return String.format("%s.%s", fname, ext);
    	else
    		return String.format("%s", fname);
    }
    
}
