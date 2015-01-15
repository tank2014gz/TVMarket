package com.soniq.tvmarket.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class AppClassInfo implements Parcelable{
	public int id;
	public String key;
	public String name;
	
	public AppClassInfo()
	{
		
	}
	
	public AppClassInfo(int id, String key, String name)
	{
		this.id = id;
		this.key = key;
		this.name = name;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(id);
		dest.writeString(key);
		dest.writeString(name);
	}
	
	

	public static final Parcelable.Creator<AppClassInfo> CREATOR = new Creator<AppClassInfo>() {  
        public AppClassInfo createFromParcel(Parcel source) {  
        	AppClassInfo appInfo = new AppClassInfo();  
        	appInfo.id = source.readInt();
        	appInfo.key = source.readString();
        	appInfo.name = source.readString();
        	
            return appInfo;  
        }

		@Override
		public AppClassInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}  	
	};
}
