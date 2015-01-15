package com.soniq.tvmarket.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import com.soniq.tvmarket.R;
import com.soniq.tvmarket.component.CustomUI;
import com.soniq.tvmarket.data.AppClassInfo;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.RecommendInfo;
import com.soniq.tvmarket.model.ClientUpgrade;
import com.soniq.tvmarket.model.HomeDataLoader;
import com.soniq.tvmarket.service.DownloadService;
import com.soniq.tvmarket.service.DownloadTaskInfo;
import com.soniq.tvmarket.ui.widget.MetroItemView;
import com.soniq.tvmarket.utils.MyUtils;
import com.soniq.tvmarket.utils.PackageUtils;

public class MainActivity extends MyBaseActivity {

	private boolean _bLoop = true; // 是否支持循环

	private int _maxPageCount = 200; // 左右可以最多滑动100页
	private int _loopCenter = 100;

	private boolean _bKeepMenuState = false;
	private ViewPager _viewPager = null;
	private int _lastPageIndex = 0;
	private boolean _bMenuFocused = false;
	private MyPagerAdapter _viewPageAdapter = null;
	private List<View> _viewList = new ArrayList<View>();

	// 菜单列表
	private List<ImageView> _menuList = new ArrayList<ImageView>();

	// 首页
	private RecommendPage _recommendPage;
	private ApplicationPage _applicationPage;
	private GamePage _gamePage;
	private SettingPage _settingPage;

	private ImageView _menuRecommend = null;
	private ImageView _menuApplication = null;
	private ImageView _menuGame = null;
	private ImageView _menuSetting = null;

	private AppDetailDialog _appDetailDialog = null;

	private Button _btnSearch = null;

	static MainActivity _mainActivity = null;

	public static MainActivity getInstance() {
		return _mainActivity;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			HomeDataLoader.getInstance(this)._recommendList
					.addAll((List<RecommendInfo>) savedInstanceState
							.getSerializable("lists"));
		}
		setContentView(R.layout.activity_main);
		_mainActivity = this;
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int w = dm.widthPixels;
		int h = dm.heightPixels;
		int densityDpi = dm.densityDpi;
		float density = dm.density;
		String log = "screen: " + w + "x" + h + " density=" + density
				+ " densityDpi=" + densityDpi;
		Log.v(AppConfig.TAG, log);

		// 启动后，自动检测版本更新
		ClientUpgrade cu = new ClientUpgrade(this);
		cu.startCheckVersion(null);

		initUI();

		// createWifiStateReceiver();

		// 启动下载服务
		this.createReceiver();
		DownloadService.startService(this);
		DownloadService.bindService(this);

		HomeDataLoader.getInstance(this).startUpdate();

		if (!MyUtils.isNetworkConnect(this)) {
			CustomUI.showTipsDialog(this, "当前网络不可用，请检查网络配置！");
		}

		// CustomUI.showHelpDialog(this, log);

	}

	@Override
	public void onDestroy() {
		Log.v(AppConfig.TAG, "MainActivity onDestroy");

		this.destroyReceiver();
		// 停止下载服务
		DownloadService.stopService(this);
		DownloadService.unbindService(this);

		super.onDestroy();
	}

	@Override
	public void onResume() {
		Log.v(AppConfig.TAG, "onResume");

		// registerWifiStateReceiver();
		if (_appDetailDialog != null)
			_appDetailDialog.onResume();

		super.onResume();
	}

	@Override
	public void onPause() {
		// unregisterWifiStateReceiver();

		super.onPause();
	}

	protected void initUI() {
		this._viewPager = (ViewPager) this.findViewById(R.id.viewpager);
		changeViewPageScroller();
		this._viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

		this._recommendPage = new RecommendPage(this);
		this._viewList.add(this._recommendPage);

		this._applicationPage = new ApplicationPage(this);
		this._viewList.add(this._applicationPage);

		this._gamePage = new GamePage(this);
		this._viewList.add(this._gamePage);

		this._settingPage = new SettingPage(this);
		this._viewList.add(this._settingPage);

		this._viewPageAdapter = new MyPagerAdapter();
		this._viewPager.setAdapter(this._viewPageAdapter);
		if (_bLoop)
			_viewPager.setCurrentItem(_loopCenter);
		else
			this._viewPager.setCurrentItem(0);
		this._viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

		_menuRecommend = (ImageView) this.findViewById(R.id.tab_menu_recommend);
		_menuApplication = (ImageView) this
				.findViewById(R.id.tab_menu_application);
		_menuGame = (ImageView) this.findViewById(R.id.tab_menu_game);
		_menuSetting = (ImageView) this.findViewById(R.id.tab_menu_setting);

		_menuList.add(_menuRecommend);
		_menuList.add(_menuApplication);
		_menuList.add(_menuGame);
		_menuList.add(_menuSetting);

		_menuRecommend.setImageResource(R.drawable.menu_recommend_selected);
		_menuApplication.setImageResource(R.drawable.menu_app_normal);
		_menuGame.setImageResource(R.drawable.menu_game_normal);
		_menuSetting.setImageResource(R.drawable.menu_setting_normal);

		_menuRecommend.setOnFocusChangeListener(_menuFocusChangeListener);
		_menuApplication.setOnFocusChangeListener(_menuFocusChangeListener);
		_menuGame.setOnFocusChangeListener(_menuFocusChangeListener);
		_menuSetting.setOnFocusChangeListener(_menuFocusChangeListener);

		_menuRecommend.setNextFocusRightId(R.id.tab_menu_application);
		_menuRecommend.setNextFocusLeftId(R.id.tab_menu_setting);
		_menuRecommend.setNextFocusDownId(-1);
		_menuRecommend.setNextFocusUpId(-1);

		_menuApplication.setNextFocusLeftId(R.id.tab_menu_recommend);
		_menuApplication.setNextFocusRightId(R.id.tab_menu_game);
		_menuApplication.setNextFocusDownId(-1);
		_menuApplication.setNextFocusUpId(-1);

		_menuGame.setNextFocusLeftId(R.id.tab_menu_application);
		_menuGame.setNextFocusRightId(R.id.tab_menu_setting);
		_menuGame.setNextFocusDownId(-1);
		_menuGame.setNextFocusUpId(-1);

		_menuSetting.setNextFocusLeftId(R.id.tab_menu_game);
		_menuSetting.setNextFocusRightId(R.id.btn_search);
		_menuSetting.setNextFocusDownId(-1);
		_menuSetting.setNextFocusUpId(-1);

		_btnSearch = (Button) this.findViewById(R.id.btn_search);
		_btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainActivity.this,
						SearchActivity.class);
				MainActivity.this.startActivity(intent);
			}

		});

		// 设置默认焦点
		_recommendPage.setCurrentFocusItemWithDefault();
	}

	private View.OnFocusChangeListener _menuFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			int idx = _menuList.indexOf(v);
			AppConfig.showLog("OnMenuFocusChangeListener: " + idx);
			if (hasFocus) {
				_bMenuFocused = true;

				setMenuFocused(idx, true);

				if (_bLoop) {
					int pos = _viewPager.getCurrentItem();
					int pos1 = getRealPosition(pos);
					if (pos1 != idx) {
						if (pos1 == 0 && idx == (_menuList.size() - 1))
							idx = pos - 1;
						else if (pos1 == (_menuList.size() - 1) && idx == 0)
							idx = pos + 1;
						else
							idx = pos + idx - pos1;

						AppConfig.showLog("cur=" + pos + " to=" + idx);
						_viewPager.setCurrentItem(idx);
					}
				} else {
					_viewPager.setCurrentItem(idx);
				}

			} else {
				if (_bKeepMenuState) {
					setMenuSelected(idx, true);
					_bKeepMenuState = false;
				} else
					setMenuFocused(idx, false);
			}
		}
	};

	private void setMenuFocused(int menuIndex, boolean focus) {
		ImageView menuImageView = _menuList.get(menuIndex);

		int resId = 0;
		int id = menuImageView.getId();

		if (focus) {
			switch (id) {
			case R.id.tab_menu_recommend:
				resId = R.drawable.menu_recommend_focused;
				break;
			case R.id.tab_menu_application:
				resId = R.drawable.menu_app_focused;
				break;
			case R.id.tab_menu_game:
				resId = R.drawable.menu_game_focused;
				break;
			case R.id.tab_menu_setting:
				resId = R.drawable.menu_setting_focused;
				break;
			default:
				break;
			}
		} else {
			switch (id) {
			case R.id.tab_menu_recommend:
				resId = R.drawable.menu_recommend_normal;
				break;
			case R.id.tab_menu_application:
				resId = R.drawable.menu_app_normal;
				break;
			case R.id.tab_menu_game:
				resId = R.drawable.menu_game_normal;
				break;
			case R.id.tab_menu_setting:
				resId = R.drawable.menu_setting_normal;
				break;
			default:
				break;
			}
		}

		menuImageView.setImageResource(resId);
	}

	private void setMenuSelected(int menuIndex, boolean selected) {
		ImageView menuImageView = _menuList.get(menuIndex);

		int resId = 0;
		int id = menuImageView.getId();

		if (selected) {
			switch (id) {
			case R.id.tab_menu_recommend:
				resId = R.drawable.menu_recommend_selected;
				break;
			case R.id.tab_menu_application:
				resId = R.drawable.menu_app_selected;
				break;
			case R.id.tab_menu_game:
				resId = R.drawable.menu_game_selected;
				break;
			case R.id.tab_menu_setting:
				resId = R.drawable.menu_setting_selected;
				break;
			default:
				return;
			}
		} else {
			switch (id) {
			case R.id.tab_menu_recommend:
				resId = R.drawable.menu_recommend_normal;
				break;
			case R.id.tab_menu_application:
				resId = R.drawable.menu_app_normal;
				break;
			case R.id.tab_menu_game:
				resId = R.drawable.menu_game_normal;
				break;
			case R.id.tab_menu_setting:
				resId = R.drawable.menu_setting_normal;
				break;
			default:
				return;
			}
		}
		menuImageView.setImageResource(resId);

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();

		View v = this.getCurrentFocus();
		if (v == null) {
			// 没有任何一个有焦点，焦点切换到当前页的第一个项目上吧
			return super.dispatchKeyEvent(event);
		}

		if (action == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				Log.v(AppConfig.TAG, "up");

				if ((v instanceof MetroItemView)) {
					if (v.getNextFocusUpId() == -1) {
						// 焦点移动到菜单上面
						int idx = _viewPager.getCurrentItem();
						if (idx >= 0) {
							if (_bLoop)
								idx = getRealPosition(idx);
							ImageView menuImageView = _menuList.get(idx);
							menuImageView.requestFocus();
							return true;
						}
					}
				}
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				if ((v instanceof ImageView)) {
					_bKeepMenuState = true;
				}

				_bMenuFocused = false;
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				if ((v instanceof ImageView)) {
					if (v.getNextFocusLeftId() == -1)
						return true;
				}
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				if ((v instanceof ImageView)) {
					if (v.getNextFocusRightId() == -1)
						return true;
				}
			}
		}

		return super.dispatchKeyEvent(event);
	}

	private void changeViewPageScroller() {
		try {
			Field localField = ViewPager.class.getDeclaredField("mScroller");
			localField.setAccessible(true);
			FixedSpeedScroller localFixedSpeedScroller = new FixedSpeedScroller(
					this, new AccelerateDecelerateInterpolator());
			localField.set(this._viewPager, localFixedSpeedScroller);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	class FixedSpeedScroller extends Scroller {
		private int mDuration = 300;

		public FixedSpeedScroller(Context arg2) {
			super(arg2);
		}

		public FixedSpeedScroller(Context paramInterpolator, Interpolator arg3) {
			super(paramInterpolator, arg3);
		}

		public int getmDuration() {
			return this.mDuration;
		}

		public void setmDuration(int paramInt) {
			this.mDuration = paramInt;
		}

		public void startScroll(int paramInt1, int paramInt2, int paramInt3,
				int paramInt4) {
			super.startScroll(paramInt1, paramInt2, paramInt3, paramInt4,
					this.mDuration);
		}

		public void startScroll(int paramInt1, int paramInt2, int paramInt3,
				int paramInt4, int paramInt5) {
			super.startScroll(paramInt1, paramInt2, paramInt3, paramInt4,
					this.mDuration);
		}
	}

	public class MyOnPageChangeListener implements
			ViewPager.OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			Log.v("alex", "onPageSelected: " + arg0);

			if (_bLoop)
				arg0 = getRealPosition(arg0);

			if (_bMenuFocused == false) {
				if (arg0 > _lastPageIndex) {
					// 后翻页
					BasePage page = (BasePage) MainActivity.this._viewList
							.get(arg0);
					page.setCurrentFocusItemWithLeft();

				} else if (arg0 < _lastPageIndex) {
					// 前翻页
					BasePage page = (BasePage) MainActivity.this._viewList
							.get(arg0);
					page.setCurrentFocusItemWithRight();

				}
			}

			if (arg0 != _lastPageIndex) {
				View v = MainActivity.this.getCurrentFocus();
				if (v != null && (v instanceof ImageView)) {
					// 焦点在菜单里
					MainActivity.this.setMenuFocused(_lastPageIndex, false);
					MainActivity.this.setMenuFocused(arg0, true);
				} else {
					MainActivity.this.setMenuSelected(_lastPageIndex, false);
					MainActivity.this.setMenuSelected(arg0, true);
				}
				_lastPageIndex = arg0;
			}

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private int getRealPosition(int position) {
		/*
		 * private boolean _bLoop = true; private int _maxPageCount = 200;
		 * private int _loopCenter = 100;
		 */
		int realSize = MainActivity.this._viewList.size();
		int offset = _loopCenter % realSize;

		int pos = (position - offset) % realSize;
		if (pos < 0)
			pos = realSize + pos;

		return pos;
	}

	class MyPagerAdapter extends PagerAdapter {

		MyPagerAdapter() {
		}

		public void destroyItem(View paramView, int paramInt, Object paramObject) {
			AppConfig.showLog("destroyItem " + paramInt);
			if (_bLoop) {
				// int oldPosition = paramInt;
				//
				// paramInt = getRealPosition(paramInt);
				//
				// AppConfig.showLog("real pos: " + paramInt);
				// View v = MainActivity.this._viewList.get(paramInt);
				// ((ViewPager)paramView).removeView(v);
				//
				// int pos = ((Integer)v.getTag()).intValue();
				// if( pos == oldPosition )
				// {
				// AppConfig.showLog("remove page " + oldPosition);
				// ((ViewPager)paramView).removeView(v);
				// }
			} else {

				View v = MainActivity.this._viewList.get(paramInt);
				((ViewPager) paramView).removeView(v);
			}
		}

		public void finishUpdate(View paramView) {
		}

		public int getCount() {
			int count = 0;
			if (_bLoop)
				count = _maxPageCount;
			else
				count = MainActivity.this._viewList.size();

			return count;
		}

		public Object instantiateItem(View collection, int position) {
			int oldPosition = position;
			Log.v(AppConfig.TAG, "instantiateItem:" + position);
			if (_bLoop) {
				position = getRealPosition(position);
			}

			Log.v(AppConfig.TAG, "real position:" + position);

			View v = MainActivity.this._viewList.get(position);

			if (_bLoop)
				v.setTag(oldPosition);

			if (v.getParent() != null) {
				AppConfig.showLog("remove it");
				((ViewPager) collection).removeView(v);
			}

			AppConfig.showLog("show page " + oldPosition);
			((ViewPager) collection).addView(v, 0);

			return v;
		}

		public boolean isViewFromObject(View paramView, Object paramObject) {
			return paramView == paramObject;
		}

		public void restoreState(Parcelable paramParcelable,
				ClassLoader paramClassLoader) {
		}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View paramView) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			int idx = _viewPager.getCurrentItem();
			AppConfig.showLog("current page " + idx);
			int firstIdx = 0;
			if (_bLoop) {
				int pos = getRealPosition(idx);
				firstIdx = idx - pos;

				idx = pos;
			}

			if (idx == 0)
				showQuitDialog();
			else {
				AppConfig.showLog("goto page " + firstIdx);
				if (this._bMenuFocused) {
					View v = _menuList.get(0);
					v.requestFocus();
				} else {
					_viewPager.setCurrentItem(firstIdx, true);
				}
			}
			return true;
		}

		return false;
	}

	private void showQuitDialog() {
		final Dialog dlg = new Dialog(this, R.style.dialog);
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_yesno, null);

		TextView textView = (TextView) v.findViewById(R.id.textView);
		textView.setText(this.getResources().getString(R.string.quit_tips));

		Button yesButton = (Button) v.findViewById(R.id.dialog_text_sure);
		yesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				MainActivity.this.finish();
				MyApplation.exitAPP();
			}

		});

		Button cancelButton = (Button) v.findViewById(R.id.dialog_text_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}

		});

		int w = MyUtils.dip2px(this, 668);
		int h = MyUtils.dip2px(this, 308);

		dlg.setContentView(v, new ViewGroup.LayoutParams(w, h));
		dlg.show();
	}

	private PackageUtils.InstallApkCallback _installApkCallback = new PackageUtils.InstallApkCallback() {

		@Override
		public void onInstallFinished(String packageName, int resultCode) {
			Log.v(AppConfig.TAG, "**********packageName:" + packageName
					+ " resultCode:" + resultCode);
			if (resultCode == 1)
				DownloadService.getBindService().sendInstallBroadcast(
						DownloadService.ACTION_INSTALL_SUCCESSED, packageName);
			else
				DownloadService.getBindService().sendInstallBroadcast(
						DownloadService.ACTION_INSTALL_FAILED, packageName);

		}
	};

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data)
	// {
	// Log.v(AppConfig.TAG, "onActivityResult: requestCode=" + requestCode +
	// " resultCode=" + resultCode);
	// if( data != null )
	// Log.v(AppConfig.TAG, data.toString());
	//
	// }

	private BroadcastReceiver _downloadReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(DownloadService.ACTION_FINISHED)) {
				Log.v(AppConfig.TAG, "receive ACTION_FINISHED");

				Bundle b = intent.getExtras();
				DownloadTaskInfo taskInfo = (DownloadTaskInfo) b
						.getParcelable("task");

				Log.d(AppConfig.TAG, "download " + taskInfo.title + " finished");

				String filename = MyUtils.get_filename_from_url(
						taskInfo.downloadUrl, true);
				filename = AppConfig.getApkDirName(MainActivity.this) + "/"
						+ filename;

				// DownloadService.getBindService().sendInstallBroadcast(DownloadService.ACTION_INSTALLING,
				// taskInfo.packageName);
				//
				// File file = new File(filename);
				// MyUtils.execCmd("chmod 777 " + file.toString());
				//
				//
				// PackageUtils.installApk(MainActivity.this, filename, 100,
				// _installApkCallback);

				DownloadService.getBindService().notifyDownload(
						MainActivity.this, taskInfo.taskId);

				installApk(filename, taskInfo.packageName);
			}
		}
	};

	public void installApk(String apkFilename, String packageName) {
		DownloadService.getBindService().sendInstallBroadcast(
				DownloadService.ACTION_INSTALLING, packageName);
		File file = new File(apkFilename);
		MyUtils.execCmd("chmod 777 " + file.toString());

		PackageUtils.installApk(this, apkFilename, 100, _installApkCallback);
	}

	public void createReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadService.ACTION_UPDATE);
		filter.addAction(DownloadService.ACTION_FINISHED);
		this.registerReceiver(_downloadReceiver, filter);
	}

	public void destroyReceiver() {
		if (_downloadReceiver != null) {
			this.unregisterReceiver(_downloadReceiver);
			_downloadReceiver = null;
		}
	}

	public void OnAdClicked(RecommendInfo recommendInfo) {
		if (recommendInfo.action
				.equalsIgnoreCase(RecommendInfo.AD_ACTION_WEBPAGE)) {
			if (MyUtils.isHttpUrl(recommendInfo.link)) {
				Uri uri = Uri.parse(recommendInfo.link);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				this.startActivity(intent);
			}
		} else if (recommendInfo.action
				.equalsIgnoreCase(RecommendInfo.AD_ACTION_APPLIST)) {
			AppClassInfo aci = new AppClassInfo();
			aci.name = recommendInfo.title;
			aci.key = recommendInfo.link;

			Intent intent = new Intent(this, AppListActivity.class);
			Bundle b = new Bundle();
			b.putParcelable("class", aci);
			b.putString("type", "ranklist");
			intent.putExtras(b);
			this.startActivity(intent);

		} else if (recommendInfo.action
				.equalsIgnoreCase(RecommendInfo.AD_ACTION_APP)) {
			// 显示app详情页
			int appid = 0;
			try {
				appid = Integer.parseInt(recommendInfo.link);
			} catch (Exception e) {

			}

			if (appid > 0)
				_appDetailDialog = AppDetailDialog.showDetailDialog(this,
						appid, _onAppDetailDismissListener);
		}
	}

	private AppDetailDialog.OnAppDetailDismissListener _onAppDetailDismissListener = new AppDetailDialog.OnAppDetailDismissListener() {

		@Override
		public void onAppDetailDismiss() {
			// TODO Auto-generated method stub
			_appDetailDialog = null;
		}

	};

}
