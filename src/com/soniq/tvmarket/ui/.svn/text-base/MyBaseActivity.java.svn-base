package com.soniq.tvmarket.ui;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.soniq.tvmarket.R;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.MyProfile;
import com.soniq.tvmarket.data.RecommendInfo;
import com.soniq.tvmarket.model.HomeDataLoader;
import com.soniq.tvmarket.utils.ImageUtils;
import com.soniq.tvmarket.utils.MyUtils;

public class MyBaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplation.addActivity(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("lists",
				(Serializable) HomeDataLoader.getInstance(this)._recommendList);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		HomeDataLoader.getInstance(this)._recommendList
				.addAll((List<RecommendInfo>) savedInstanceState
						.getSerializable("lists"));
		// if (HomeDataLoader.getInstance(this)._recommendList.size() == 0) {
		// Intent intent = new Intent();
		// intent.setClass(this, SplashScreenActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(intent);
		// finish();
		// // MyApplation.exitAPP();
		// // /** 重启应用 **/
		// // finish();
		// // Intent launch = getBaseContext().getPackageManager()
		// // .getLaunchIntentForPackage(
		// // getBaseContext().getPackageName());
		// // launch.addFlags(Intent.);
		// // startActivity(launch);
		// }
	}

	@Override
	public void onDestroy() {
		View v = (View) this.findViewById(R.id.mainView);
		if (v != null) {
			BitmapDrawable bd = (BitmapDrawable) v.getBackground();
			if (bd != null) {
				v.setBackgroundResource(0);
				bd.setCallback(null);
				bd.getBitmap().recycle();
			}
		}

		super.onDestroy();
	}

	@Override
	public void onResume() {

		View v = (View) this.findViewById(R.id.mainView);
		if (v != null) {
			int resId = MyProfile.get_profile_int_value(this, "backgroundimg",
					0);
			if (resId > 0) {
				v.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(
						this, resId));
			} else {
				v.setBackgroundDrawable(ImageUtils.loadDrawableFromResource(
						this, R.drawable.bghigh1));
			}
		}
		MyUtils.showMemoryInfo(this, "memory");

		super.onResume();
	}

	private IntentFilter _wifiIntentFilter = null;

	protected void createWifiStateReceiver() {
		_wifiIntentFilter = new IntentFilter();
		_wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
	}

	protected void registerWifiStateReceiver() {
		this.registerReceiver(_wifiIntentReceiver, _wifiIntentFilter);
	}

	protected void unregisterWifiStateReceiver() {
		this.unregisterReceiver(_wifiIntentReceiver);
	}

	// 声明wifi消息处理过程
	private BroadcastReceiver _wifiIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int wifi_state = intent.getIntExtra("wifi_state", 0);
			int level = Math.abs(((WifiManager) getSystemService(WIFI_SERVICE))
					.getConnectionInfo().getRssi());
			Log.v(AppConfig.TAG, "1111:" + level);
			switch (wifi_state) {
			case WifiManager.WIFI_STATE_DISABLING:
				Log.i(AppConfig.TAG, "1111:" + WifiManager.WIFI_STATE_DISABLING);
				// wifi_image.setImageResource(R.drawable.wifi_sel);
				// wifi_image.setImageLevel(level);
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				Log.i(AppConfig.TAG, "2222:" + WifiManager.WIFI_STATE_DISABLED);
				// wifi_image.setImageResource(R.drawable.wifi_sel);
				// wifi_image.setImageLevel(level);
				break;
			case WifiManager.WIFI_STATE_ENABLING:
				// wifi_image.setImageResource(R.drawable.wifi_sel);
				// wifi_image.setImageLevel(level);
				Log.i(AppConfig.TAG, "33333:" + WifiManager.WIFI_STATE_ENABLING);
				break;
			case WifiManager.WIFI_STATE_ENABLED:
				Log.i(AppConfig.TAG, "4444:" + WifiManager.WIFI_STATE_ENABLED);
				// wifi_image.setImageResource(R.drawable.wifi_sel);
				// wifi_image.setImageLevel(level);
				break;
			case WifiManager.WIFI_STATE_UNKNOWN:
				Log.i(AppConfig.TAG, "5555:" + WifiManager.WIFI_STATE_UNKNOWN);
				// wifi_image.setImageResource(R.drawable.wifi_sel);
				// wifi_image.setImageLevel(level);
				break;
			}
		}
	};
}
