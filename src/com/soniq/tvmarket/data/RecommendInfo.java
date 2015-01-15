package com.soniq.tvmarket.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.soniq.tvmarket.utils.MyUtils;

import android.content.Context;
import android.graphics.Bitmap;

public class RecommendInfo implements Serializable {
	public final static String AD_POS_HOME_LEFT_TOP = "home_lefttop";
	public final static String AD_POS_HOME_LEFT_BOTTOM = "home_leftbottom";
	public final static String AD_POS_HOME_RIGHT_TOP = "home_righttop";
	public final static String AD_POS_HOME_RIGHT_BOTTOM = "home_rightbottom";
	public final static String AD_POS_HOME_MIDDLE = "home_middle";
	public final static String AD_POS_APP = "app";
	public final static String AD_POS_GAME = "game";

	public final static String AD_ACTION_WEBPAGE = "webpage";
	public final static String AD_ACTION_APPLIST = "applist";
	public final static String AD_ACTION_APP = "app";

	public int id; // id
	public String adPos; // 广告位名称
	public String title; // 标题
	public String imageUrl; // 图片名称
	public String action;
	public String link;

	public int resId;

	public String getLocalImageFileName(Context context) {

		return String.format("%s/%s", context.getFilesDir(),
				DataModule.getLocalImageFileNameByUrl(this.imageUrl, "ad-"));
	}

	public Bitmap getLocalImageBitmap(Context context) {
		String filename = getLocalImageFileName(context);
		if (!MyUtils.FileExist(filename))
			return null;

		return MyUtils.loadBitmapFromFile(filename);
	}
}
