package com.soniq.tvmarket.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class MyApplation extends Application {
	private static List<Activity> activityList = new ArrayList<Activity>();
	private static MyApplation instance;

	public static MyApplation getInstance() {
		return instance;
	}

	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	// 添加Activity到容器中
	public static void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish
	public static void exitAPP() {
		for (int i = 0; i < activityList.size(); i++) {
			Activity activity = activityList.get(i);
			if (activity != null) {
				activity.finish();
			}
		}
		activityList.clear();
		System.exit(0);

	}
}
