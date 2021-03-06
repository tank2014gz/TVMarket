package com.soniq.tvmarket.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.soniq.tvmarket.R;
import com.soniq.tvmarket.component.CustomUI;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.WAPI;
import com.soniq.tvmarket.ui.MainActivity;
import com.soniq.tvmarket.utils.MyUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ClientUpgrade {

	private Context _context;
	private Thread _download_thread;

	private String _downloadApkUrl = null;
	private String _savePath = null;
	private String _saveFilename = null;
	private boolean _interceptFlag = false;
	private int _progress = 0;
	private ProgressBar _progressBar;
	private TextView _titleTextView;

	private Dialog _downloadDialog;

	private ClientUpgradeCallback _callback = null;

	private final static int DOWN_UPDATE = 1;
	private final static int DOWN_OVER = 2;
	private final static int DOWN_ERROR = 10;

	public final static int STATE_ALREADY_NEW_VERSION = 1;
	public final static int STATE_CHECK_ERROR = 2;
	public final static int STATE_UPGRADE = 3;

	public ClientUpgrade(Context context) {
		_context = context;
	}

	public interface ClientUpgradeCallback {
		public void onCheckFinished(int state);
	}

	private void doCallback(int state) {
		if (_callback != null)
			_callback.onCheckFinished(state);
	}

	private class CheckVersionAsyncTask extends
			AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				// Thread.sleep(5000);
			} catch (Exception e) {

			}
			return checkVersion();
		}

		@Override
		protected void onPostExecute(String result) {
			int state = doParseResult(result);
			doCallback(state);
		}

		private int doParseResult(String result) {
			// UPGRADE|%s|%s|%s|%s", version, desc, downloadurl, force;

			if (result == null)
				return -1;

			String[] ss = result.split("\\|");
			if (ss != null && ss.length > 0 && ss[0].equalsIgnoreCase("NOT"))
				return STATE_ALREADY_NEW_VERSION;

			Log.i("", "result=" + ss.length);
			if (ss == null || ss.length != 5)
				return -2;

			boolean force = false;
			if (ss[4].equalsIgnoreCase("yes"))
				force = true;

			_downloadApkUrl = ss[3];
			String[] tt = _downloadApkUrl.split("/");
			if (tt.length < 2)
				return -3;

			_savePath = "";
			_saveFilename = String.format("%s/%s", _context.getFilesDir(),
					tt[tt.length - 1]);
			// _saveFilename = "/sdcard/tvmarket.apk";
			Log.v(AppConfig.TAG, "path=" + _savePath);
			Log.v(AppConfig.TAG, "filename=" + _saveFilename);

			String msg = ss[2];
			if (msg.length() <= 0) {
				msg = String.format("新版本%s出来了，赶快升级吧！", ss[0]);
			}
			Dialog dlg;

			// if( force )
			// {
			// dlg = new AlertDialog.Builder(_context).setTitle("提示")
			// .setMessage(msg)
			// .setPositiveButton("确定", new DialogInterface.OnClickListener(){
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// downloadApk();
			// }})
			// .create();
			//
			// }
			// else
			// {
			// dlg = new AlertDialog.Builder(_context).setTitle("提示")
			// .setMessage(msg)
			// .setPositiveButton("确定", new DialogInterface.OnClickListener(){
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// downloadApk();
			//
			// }})
			// .setNegativeButton("取消", new DialogInterface.OnClickListener(){
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// dialog.dismiss();
			// }})
			// .create();
			// }
			// dlg.show();

			showUpgradeTipsDialog(msg, force);

			return STATE_UPGRADE;
		}

	}

	public void showUpgradeTipsDialog(String message, boolean force) {
		final Dialog dlg = new Dialog(_context, R.style.dialog);
		View v = LayoutInflater.from(_context).inflate(R.layout.dialog_yesno,
				null);

		TextView textView = (TextView) v.findViewById(R.id.textView);
		textView.setText(message);

		Button yesButton = (Button) v.findViewById(R.id.dialog_text_sure);
		yesButton.setText(_context.getResources().getString(
				R.string.title_btn_upgrade));
		yesButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				downloadApk();
				dlg.dismiss();
			}
		});

		Button cancelButton = (Button) v.findViewById(R.id.dialog_text_cancel);

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}

		});

		if (force) {
			cancelButton.setVisibility(View.GONE);
		}

		int w = MyUtils.dip2px(_context, 700);
		int h = MyUtils.dip2px(_context, 308);

		dlg.setContentView(v, new ViewGroup.LayoutParams(w, h));
		dlg.show();
	}

	public void startCheckVersion(ClientUpgradeCallback callback) {
		_callback = callback;

		CheckVersionAsyncTask ct = new CheckVersionAsyncTask();
		ct.execute(null, null, null);
	}

	private void downloadApk() {
		showDownloadDialog();

		_download_thread = new Thread(mDownloadRunnable);
		_download_thread.start();
	}

	private Runnable mDownloadRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				URL url = new URL(_downloadApkUrl);

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();

				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				if (_savePath.length() > 0) {
					File file = new File(_savePath);
					if (!file.exists())
						file.mkdirs();
				}

				String apkFilename = _saveFilename;
				File apkFile = new File(apkFilename);
				if (apkFile.exists())
					apkFile.delete();

				FileOutputStream fos = new FileOutputStream(apkFile);

				// FileOutputStream fos = _context.openFileOutput(_saveFilename,
				// Context.MODE_PRIVATE);
				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;

					Log.i("", "download: " + count + " total: " + length);
					_progress = (int) (((float) count / length) * 100);

					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}

					fos.write(buf, 0, numread);
				} while (!_interceptFlag);

				fos.close();
				is.close();

			} catch (Exception e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(DOWN_ERROR);
			}
		}

	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				_progressBar.setProgress(_progress);
				_titleTextView.setText(String.format("正在下载：%d%%", _progress));
				break;
			case DOWN_OVER:
				_progress = 100;
				_progressBar.setProgress(_progress);
				_titleTextView.setText(String.format("正在下载：%d%%", _progress));
				_downloadDialog.dismiss();
				// MainActivity.getInstance().finish();
				installApk();
				break;
			case DOWN_ERROR:
				_downloadDialog.dismiss();
				CustomUI.showAlertDialog(_context, "提示", "下载失败！");
				break;
			default:
				break;
			}
		}
	};

	private int installApk() {
		File file = new File(_saveFilename);
		if (!file.exists())
			return 1;

		MyUtils.execCmd("chmod 777 " + file.toString());

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + file.toString()),
				"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// String s = String.format("%s/%s", _context.getFilesDir(),
		// _saveFilename);
		// Log.d("", s);
		// intent.setDataAndType(Uri.parse("file://" + s),
		// "application/vnd.android.package-archive");
		_context.startActivity(intent);

		return 0;
	}

	private void showDownloadDialog() {

		final Dialog dlg = new Dialog(_context, R.style.dialog);
		View v = LayoutInflater.from(_context).inflate(R.layout.dialog_upgrade,
				null);

		_progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
		_titleTextView = (TextView) v.findViewById(R.id.textView);

		Button cancelButton = (Button) v.findViewById(R.id.dialog_text_cancel);

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				_interceptFlag = true;
			}

		});

		//
		// builder.setNegativeButton("取消", new OnClickListener(){
		// @Override
		// public void onClick(DialogInterface dialog, int which)
		// {
		// dialog.dismiss();
		// _interceptFlag = true;
		// }
		//
		// });

		int w = MyUtils.dip2px(_context, 700);
		int h = MyUtils.dip2px(_context, 360);

		dlg.setContentView(v, new ViewGroup.LayoutParams(w, h));

		_downloadDialog = dlg;
		dlg.show();

	}

	public static boolean hasNewVersion(String local_version_string,
			String server_version_string) {
		String[] local_version = local_version_string.split("\\.");
		String[] server_version = server_version_string.split("\\.");

		Log.v(AppConfig.TAG, "local=" + local_version.length);
		Log.v(AppConfig.TAG, "server=" + server_version.length);

		for (int i = 0; i < local_version.length || i < server_version.length; i++) {
			int cur_code, new_code;
			if (i >= local_version.length)
				cur_code = 0;
			else
				cur_code = Integer.parseInt(local_version[i]);

			if (i >= server_version.length)
				new_code = 0;
			else
				new_code = Integer.parseInt(server_version[i]);

			if (new_code > cur_code)
				return true;
			else if (new_code < cur_code)
				return false;

		}

		return false;
	}

	public String checkVersion() {
		try {
			String currentVersion = MyUtils.getVersionCode(_context);
			String urlString = WAPI.addGeneralParams(_context,
					WAPI.WAPI_CHECK_VERSION_URL);

			Log.v(AppConfig.TAG, urlString);

			String content = WAPI.get_content_from_remote_url(urlString);
			if (content == null)
				return null;

			ArrayList<String> fieldList = new ArrayList<String>();
			int iret = WAPI.parseVersionInfoResponse(_context, content,
					fieldList);
			if (iret == 0 && fieldList.size() == 4) {
				String version = fieldList.get(0);
				String desc = fieldList.get(1);
				String downloadurl = fieldList.get(2);
				String force = fieldList.get(3);

				if (hasNewVersion(currentVersion, version)) {
					;//
					String result = String.format("UPGRADE|%s|%s|%s|%s",
							version, desc, downloadurl, force);
					return result;
				} else {
					return "NOT";
				}
			}

		} catch (Exception e) {

		}

		return null;

	}
}
