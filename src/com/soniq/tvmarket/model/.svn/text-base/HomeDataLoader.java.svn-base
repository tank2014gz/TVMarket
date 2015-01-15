package com.soniq.tvmarket.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.AppInfo;
import com.soniq.tvmarket.data.DataModule;
import com.soniq.tvmarket.data.RecommendInfo;
import com.soniq.tvmarket.data.WAPI;
import com.soniq.tvmarket.utils.MD5Util;
import com.soniq.tvmarket.utils.MyUtils;

public class HomeDataLoader {
	private Context _context = null;

	private static HomeDataLoader _dataLoader = null;

	public List<RecommendInfo> _recommendList = new ArrayList<RecommendInfo>();

	public static HomeDataLoader getInstance(Context context) {
		if (_dataLoader == null)
			_dataLoader = new HomeDataLoader(context);

		return _dataLoader;
	}

	public HomeDataLoader(Context context) {
		_context = context;
	}

	public int getRecommendList(String adpos,
			List<RecommendInfo> recommendList, int max) {
		int cnt = 0;

		for (int i = 0; i < _recommendList.size(); i++) {
			RecommendInfo ri = _recommendList.get(i);
			if (ri.adPos.equalsIgnoreCase(adpos)) {
				recommendList.add(ri);
				cnt++;
				if (max > 0 && cnt >= max)
					break;
			}
		}

		return cnt;
	}

	public int downloadFile(Context context, String remote_file,
			String local_file) {

		try {
			URL url = new URL(remote_file);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(1000);
			conn.setRequestMethod("GET");
			conn.connect();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();

				File file = new File(local_file);

				FileOutputStream fos = new FileOutputStream(file, true);

				byte[] buffer = new byte[1024];
				int len = 0;

				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}

				fos.close();

				return 0;
			}

		} catch (Exception e) {

		}

		return 1;
	}

	public int loadFromLocal() {
		String xmlFile = AppConfig.getHomeDataXMLFileName(_context);
		if (!MyUtils.FileExist(xmlFile))
			return 1;

		String content = MyUtils.load_content_from_file(_context, xmlFile);
		if (content == null || content.length() < 1)
			return 2;

		_recommendList.clear();
		int iret = WAPI
				.parseHomeJSONResponse(_context, content, _recommendList);
		if (iret != 0)
			return 3; // 解析失败

		return 0;
	}

	public int loadDefault() {
		RecommendInfo ri;

		ri = new RecommendInfo();
		ri.id = 0;
		ri.adPos = "home-lefttop";
		ri.title = "";
		ri.imageUrl = "";
		ri.action = "";
		ri.link = "";

		_recommendList.add(ri);

		return 0;
	}

	public static int loadAllAppList(Context context, List<AppInfo> appList) {
		String dataFile = AppConfig.getAllAppListDataFileName(context);
		if (!MyUtils.FileExist(dataFile))
			return 1;

		String content = MyUtils.load_content_from_file(context, dataFile);
		return WAPI.parseAppListJSONResponse(context, content, appList);
	}

	public static int loadAllAppListFromServer(Context context) {

		String urlString;

		// 先获取md5, 没更新就不用去下载大量数据了
		urlString = WAPI.getAllAppListURLString(context, true);
		AppConfig.showLog(urlString);
		String responseString = WAPI.http_get_content(urlString);
		if (responseString == null)
			return 1;

		String s_server_md5 = WAPI.getAllAppListMD5String(context,
				responseString);
		if (s_server_md5 == null)
			return 2;

		// 本地的md5
		String dataFile = AppConfig.getAllAppListDataFileName(context);
		if (MyUtils.FileExist(dataFile)) {
			String localString = MyUtils.load_content_from_file(context,
					dataFile);

			String s_local_md5 = MD5Util.getMD5Encoding(localString);

			AppConfig.showLog("local md5:" + s_local_md5 + " server md5:"
					+ s_server_md5);

			if (s_local_md5.equals(s_server_md5)) {
				AppConfig.showLog("allAppList no change!");
				return 0; // 没变化,不需要更新
			}
		}

		urlString = WAPI.getAllAppListURLString(context, false);
		responseString = WAPI.http_get_content(urlString);
		if (responseString == null)
			return 1; // 获取接口失败

		int len = responseString.length();
		// AppConfig.showLog(responseString);
		AppConfig.showLog("len=" + len + " len1="
				+ responseString.getBytes().length);
		AppConfig.showLog("b=" + responseString.substring(0, 1) + " e="
				+ responseString.substring(len - 1));

		// boolean bCheckLocal = true;
		// if( bCheckLocal )
		// {
		// if( MyUtils.FileExist(dataFile) )
		// {
		// String localString = MyUtils.load_content_from_file(context,
		// dataFile);
		//
		// String s_server = MD5Util.getMD5Encoding(responseString);
		// String s_local = MD5Util.getMD5Encoding(localString);
		//
		// if( s_server.equals(s_local) )
		// {
		// // 没有更新
		// return 0;
		// }
		// }
		// }

		List<AppInfo> appList = new ArrayList<AppInfo>();

		int iret = WAPI.parseAppListJSONResponse(context, responseString,
				appList);
		if (iret != 0)
			return 2; // 解析失败

		String tempDataFile = AppConfig.getTempFileName(dataFile);
		MyUtils.deleteFile(tempDataFile);
		if (MyUtils.writeToFile(responseString, tempDataFile)) {
			String s_local_md5 = MD5Util.getMD5Encoding(responseString);
			AppConfig.showLog(s_local_md5);
			len = responseString.length();
			AppConfig.showLog("len=" + len + " len1="
					+ responseString.getBytes().length);
			AppConfig.showLog("b=" + responseString.substring(0, 1) + " e="
					+ responseString.substring(len - 1));

			MyUtils.deleteFile(dataFile);

			MyUtils.renameFile(tempDataFile, dataFile);
		}

		appList.clear();

		AppConfig.showLog("AllAppList download ok");

		return 0;

	}

	public int loadFromServer(boolean bCheckLocal) {
		String urlString = WAPI.getHomeURLString(_context);
		String responseString = WAPI.http_get_content(urlString);
		if (responseString == null)
			return 1; // 获取接口失败

		if (bCheckLocal) {
			String xmlFile = AppConfig.getHomeDataXMLFileName(_context);
			if (MyUtils.FileExist(xmlFile)) {
				String localString = MyUtils.load_content_from_file(_context,
						xmlFile);

				String s_server = MD5Util.getMD5Encoding(responseString);
				String s_local = MD5Util.getMD5Encoding(localString);

				if (s_server.equals(s_local)) {
					// 没有更新
					return 0;
				}
			}
		}

		List<RecommendInfo> recommendList = new ArrayList<RecommendInfo>();
		int iret = WAPI.parseHomeJSONResponse(_context, responseString,
				recommendList);
		if (iret != 0)
			return 2; // 解析失败

		int code = 0;

		// 下载图片
		for (int i = 0; i < recommendList.size(); i++) {
			RecommendInfo ri = recommendList.get(i);

			String remote_file = ri.imageUrl;
			String local_file = ri.getLocalImageFileName(_context);

			if (MyUtils.FileExist(local_file))
				continue;

			AppConfig.showLog("downloading " + remote_file);
			iret = downloadFile(_context, remote_file, local_file);
			if (iret != 0) {
				AppConfig.showLog("failed");
				code = 1;
				break;
			}
		}

		if (code == 0) {
			// 全部下载成功
			String tempXmlFile = AppConfig.getTempHomeDataXMLFileName(_context);
			MyUtils.deleteFile(tempXmlFile);
			if (MyUtils.writeToFile(responseString, tempXmlFile)) {
				String xmlFile = AppConfig.getHomeDataXMLFileName(_context);
				MyUtils.deleteFile(xmlFile);

				MyUtils.renameFile(tempXmlFile, xmlFile);
			}
		}

		recommendList.clear();

		return code;
	}

	public void startUpdate() {
		new UpdateThread().start();
	}

	private class UpdateThread extends Thread {
		public void run() {
			try {
				// 首页资源
				loadFromServer(true);

				// 所有应用列表的数据文件
				// HomeDataLoader.loadAllAppListFromServer(_context);
			} catch (Exception e) {

			}
		}
	}
}
