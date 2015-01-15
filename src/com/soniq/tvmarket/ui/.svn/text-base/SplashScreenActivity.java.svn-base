package com.soniq.tvmarket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soniq.tvmarket.R;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.MyDB;
import com.soniq.tvmarket.model.HomeDataLoader;
import com.soniq.tvmarket.utils.MyUtils;

public class SplashScreenActivity extends MyBaseActivity {

	private ImageView _loadingImageView = null;
	private TextView _loadingTextView = null;
	private Animation _animation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		Log.v(AppConfig.TAG, "deviceid=" + AppConfig.getDeviceId(this));

		initUI();

		// String hz = "我时中国人12了";
		// AppConfig.showLog(PinyinUtils.makeStringByStringSet(PinyinUtils.getPinyin(hz)));
		//
		loadData();
	}

	private void initUI() {
		_loadingImageView = (ImageView) this
				.findViewById(R.id.imageViewLoading);
		_loadingTextView = (TextView) this.findViewById(R.id.textViewLoading);

		_animation = AnimationUtils.loadAnimation(this,
				R.anim.loading_animation);
		// 使用ImageView显示动画
		_loadingImageView.startAnimation(_animation);
	}

	private void loadData() {
		LoadThread loadThread = new LoadThread();
		loadThread.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

	public int doLoadData() {
		int iret = 1;
		try {
			MyDB.getInstance().db_open(this);

			String xmlFileName = AppConfig.getHomeDataXMLFileName(this);
			if (!MyUtils.FileExist(xmlFileName)) {
				notifyMessage(1, 0, "第一次运行，正在初始化数据...");
				HomeDataLoader loader = HomeDataLoader.getInstance(this);
				if (loader.loadFromServer(false) == 0) {
					// 加载成功
					notifyMessage(1, 0, "初始化成功，准备进入应用...");
					iret = 0;
					Thread.sleep(500);
				}
			} else
				iret = 0;

			if (iret == 0) {
				iret = 1;
				if (HomeDataLoader.getInstance(this).loadFromLocal() == 0) {
					// TODO: 删除私有目录下的无用的图片缓存，否则会越来越多
					iret = 0;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return iret;
	}

	private void notifyMessage(int what, int result, String message) {
		Message msg = new Message();
		msg.what = what;
		msg.arg1 = result;
		msg.obj = message;
		handler.sendMessage(msg);
	}

	private class LoadThread extends Thread {
		public void run() {
			int ret = 1;

			try {
				// todo: 这里加入加载代码
				long start = MyUtils.getTickCount();

				ret = doLoadData();
				long end = MyUtils.getTickCount();

				long usec = end - start;

				usec = 2000 - usec;

				if (usec > 0)
					Thread.sleep(usec);

			} catch (Exception e) {

			}

			// 给主线程发消息
			notifyMessage(0, 0, null);
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				if (msg.arg1 == 0) {
					if (HomeDataLoader.getInstance(SplashScreenActivity.this)._recommendList
							.size() == 0) {
						Toast.makeText(SplashScreenActivity.this,
								"数据获取失败，请重新进入", 200).show();
						return;
					}
					// 加载完成
					Intent intent = new Intent(SplashScreenActivity.this,
							MainActivity.class);
					SplashScreenActivity.this.startActivity(intent);

					SplashScreenActivity.this.finish();
				} else {
					_loadingTextView.setText("初始化数据失败，请稍后再试一次！");
					_animation.cancel();
				}
			} else if (msg.what == 1) {
				String message = (String) msg.obj;
				_loadingTextView.setText(message);
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	};
}
