package com.soniq.tvmarket.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.androidquery.callback.ImageOptions;
import com.soniq.tvmarket.R;
import com.soniq.tvmarket.component.ImageDownloader;
import com.soniq.tvmarket.component.ImageDownloader.DownloadTask;
import com.soniq.tvmarket.component.ImageDownloader.OnDownloadTaskListener;
import com.soniq.tvmarket.data.AppClassInfo;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.AppInfo;
import com.soniq.tvmarket.data.MyProfile;
import com.soniq.tvmarket.data.WAPI;
import com.soniq.tvmarket.service.DownloadService;
import com.soniq.tvmarket.utils.ImageUtils;
import com.soniq.tvmarket.utils.MyUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppListActivity extends MyBaseActivity implements
		OnItemClickListener {

	private ViewGroup _loadingLayer = null;
	private ImageView _loadingImageView = null;
	private TextView _loadingTextView = null;
	private Animation _loadingAnimation = null;

	private LoadThread _loadingThread = null;

	private GridView _gridView = null;

	private AppClassInfo _appClassInfo = null;
	private String _dataType = "";

	private List<AppInfo> _appList = new ArrayList<AppInfo>();
	private MyAdapter _adapter = null;

	private View _lastSelectedView = null;

	private ImageDownloader _imgDownloader = null;

	private AppDetailDialog _appDetailDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle b = intent.getExtras();

		_appClassInfo = b.getParcelable("class");

		String s = b.getString("type");
		if (s != null && s.equalsIgnoreCase("ranklist")) {
			_dataType = "ranklist";
		}

		setContentView(R.layout.activity_app_list);

		_imgDownloader = new ImageDownloader(this);
		_imgDownloader.setImageFilePrefix(AppConfig.IMAGE_CACHE_FILE_PREFIX);

		// 标题
		TextView titleTextView = (TextView) this
				.findViewById(R.id.textViewTitle);
		titleTextView.setText(_appClassInfo.name);

		_gridView = (GridView) findViewById(R.id.gridView);
		_gridView.setFocusable(true);

		_gridView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

				Log.v(AppConfig.TAG, "onFocusChange");

				AppConfig.showLog(v.toString());

			}

		});

		_gridView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				String s = String.format("onItemSelected: %d, %d", arg2, arg3);
				Log.v(AppConfig.TAG, s);

				if (arg1 == null) {
					AppConfig.showLog("arg1 == null");
					return;
				}

				if (_lastSelectedView != null) {
					ViewGroup vg = (ViewGroup) _lastSelectedView;
					View btn = (View) vg.findViewById(R.id.imageViewButton);
					if (btn != null)
						btn.setVisibility(View.INVISIBLE);
				}

				_lastSelectedView = arg1;

				ViewGroup vg = (ViewGroup) _lastSelectedView;
				ImageView btn = (ImageView) vg
						.findViewById(R.id.imageViewButton);
				if (btn != null) {
					btn.setVisibility(View.VISIBLE);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				Log.v(AppConfig.TAG, "onNothingSeleced");
			}

		});

		_gridView.setSelector(R.drawable.bg_app_focused);
		_gridView.setOnItemClickListener(this);

		_loadingLayer = (ViewGroup) findViewById(R.id.loading_layer);
		_loadingImageView = (ImageView) _loadingLayer
				.findViewById(R.id.imageViewLoading);
		_loadingTextView = (TextView) _loadingLayer
				.findViewById(R.id.textViewLoading);

		_adapter = new MyAdapter(this);
		_gridView.setAdapter(_adapter);

		loadData();
	}

	private void loadData() {
		_loadingAnimation = AnimationUtils.loadAnimation(this,
				R.anim.loading_animation);
		// 使用ImageView显示动画
		_loadingImageView.startAnimation(_loadingAnimation);

		_loadingThread = new LoadThread();
		_loadingThread.start();
	}

	public int doLoadData() {
		String urlString;

		if (_dataType.equalsIgnoreCase("ranklist"))
			urlString = WAPI.getRankAppListURLString(this, _appClassInfo.key);
		else
			urlString = WAPI.getAppListURLString(this, _appClassInfo.key);

		AppConfig.showLog(urlString);
		String content = WAPI.http_get_content(urlString);
		if (content == null || content.length() < 1)
			return 1;

		int iret = WAPI.parseAppListJSONResponse(this, content, _appList);
		if (iret != 0)
			return iret;

		// 成功了
		return 0;
	}

	private class LoadThread extends Thread {
		public void run() {
			int iret = 1;
			try {
				iret = doLoadData();
			} catch (Exception e) {

			}

			Message msg = new Message();
			msg.what = iret;
			handler.sendMessage(msg);
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				// 加载成功
				if (_appList.size() == 0) {
					// 没有数据
					_loadingTextView.setText("暂时没有数据!");
					_loadingAnimation.cancel();
					_loadingImageView.setAnimation(null);
					_loadingImageView.setVisibility(View.GONE);
				} else {
					// TODO: update
					AppConfig.showLog("set adapter");
					// _gridView.setAdapter(_adapter);
					_adapter.notifyDataSetChanged();
					AppConfig.showLog("set adapter ok");

					_gridView.setSelection(0);

					_gridView.setVisibility(View.VISIBLE);

					_loadingAnimation.cancel();
					_loadingImageView.setAnimation(null);
					_loadingLayer.setVisibility(View.INVISIBLE);
				}
			} else {
				_loadingAnimation.cancel();
				_loadingImageView.setAnimation(null);
				_loadingImageView.setVisibility(View.GONE);
				_loadingTextView.setText("加载数据失败，请稍后再试一次！");
			}
		}
	};

	private class MyAdapter extends BaseAdapter {
		private Context _context;
		private LayoutInflater _inflater;

		public MyAdapter(Context context) {
			_context = context;
			_inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return _appList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		private class ViewHolder {
			ImageView imageView;
			TextView textView;
			ImageView button;

			int keyId;
		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			AppConfig.showLog("getView: " + position);
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = _inflater.inflate(R.layout.layout_app_item, null);

				holder = new ViewHolder();
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.imageViewIcon);
				holder.textView = (TextView) convertView
						.findViewById(R.id.textViewTitle);
				holder.button = (ImageView) convertView
						.findViewById(R.id.imageViewButton);
				convertView.setTag(holder);

			} else
				holder = (ViewHolder) convertView.getTag();

			AppInfo appInfo = _appList.get(position);

			holder.keyId = appInfo.id;

			holder.textView.setText(appInfo.name);

			holder.imageView.setImageResource(R.drawable.app_loading);

			// if( position == _gridView.getSelectedItemPosition() )
			// {
			// holder.button.setVisibility(View.VISIBLE);
			// _lastSelectedView = convertView;
			// }
			// else
			// holder.button.setVisibility(View.INVISIBLE);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("appinfo", appInfo);
			params.put("view", convertView);
			params.put("roundcorner", 15);

			_imgDownloader.downloadImage(_context, appInfo.icon,
					new OnDownloadTaskListener() {

						@Override
						public void onDownloadSuccessfully(
								DownloadTask downloadTask) {
							// TODO Auto-generated method stub
							AppInfo appInfo = (AppInfo) downloadTask.params
									.get("appinfo");
							View view = (View) downloadTask.params.get("view");
							ViewHolder holder = (ViewHolder) view.getTag();
							if (appInfo.id == holder.keyId) {
								holder.imageView.setImageBitmap(downloadTask
										.getBitmap());
							}
						}

						@Override
						public void onDownloadFailed(int errCode,
								DownloadTask downloadTask) {
							// TODO Auto-generated method stub

						}
					}, params);

			// Bitmap bmp = ImageUtils.loadBitmapFromResource(_context,
			// R.drawable.ad1);
			// if( bmp != null )
			// {
			// bmp = ImageUtils.getRoundedCornerBitmap(bmp, 30);
			// holder.imageView.setImageBitmap(bmp);
			// }

			return convertView;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.app_list, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		AppInfo appInfo = _appList.get(arg2);
		_appDetailDialog = AppDetailDialog.showDetailDialog(this, appInfo,
				_onAppDetailDismissListener);
	}

	// @Override
	// public void onResume() {
	// AppConfig.showLog("onResume(), ....");
	// if (_appDetailDialog != null)
	// _appDetailDialog.onResume();
	//
	// super.onResume();
	// }

	private AppDetailDialog.OnAppDetailDismissListener _onAppDetailDismissListener = new AppDetailDialog.OnAppDetailDismissListener() {

		@Override
		public void onAppDetailDismiss() {
			// TODO Auto-generated method stub
			_appDetailDialog = null;
		}

	};

}
