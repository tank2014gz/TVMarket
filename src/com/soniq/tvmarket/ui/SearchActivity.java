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
import com.soniq.tvmarket.model.HomeDataLoader;
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
import android.util.TypedValue;
import android.view.Gravity;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchActivity extends MyBaseActivity implements OnItemClickListener{
	
	private final static int CHAR_ID_BASE = 100;
	private final static int CHAR_ID_SPACE = 1000;
	private final static int CHAR_ID_CLEAR = 2000;
	private final static int CHAR_ID_BACKSPACE = 3000;
	
	private final static String _charString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

	private String _searchKeyword = "";
	
	private ListView _listView = null;
	
	private TextView _inputBoxTextView = null;
	
	private ViewGroup _loadingLayer = null;
	private ImageView _loadingImageView = null;
	private TextView _loadingTextView = null;
	private Animation _loadingAnimation = null;
	
	private LoadThread _loadingThread = null;
	
	private List<AppInfo> _allAppList = new ArrayList<AppInfo>();
	private List<AppInfo> _appList = new ArrayList<AppInfo>();
	private MyAdapter _adapter = null;
	
	private View _lastSelectedView = null;
	
	private ImageDownloader _imgDownloader = null;

	private AppDetailDialog _appDetailDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search);
		
	
		
		_imgDownloader = new ImageDownloader(this);
		_imgDownloader.setImageFilePrefix(AppConfig.IMAGE_CACHE_FILE_PREFIX);
		
		// 标题
		TextView titleTextView = (TextView)this.findViewById(R.id.textViewTitle);
		titleTextView.setText("搜索");
		
		
		
		_loadingLayer = (ViewGroup)findViewById(R.id.loading_layer);
		_loadingImageView = (ImageView)_loadingLayer.findViewById(R.id.imageViewLoading);
		_loadingTextView = (TextView)_loadingLayer.findViewById(R.id.textViewLoading);
		
		_inputBoxTextView = (TextView)this.findViewById(R.id.textViewInputBox);
		
		
		_listView = (ListView)this.findViewById(R.id.listView);
		_listView.setDividerHeight(0);
		_listView.setSelector(R.drawable.bg_download_list_selected);
		_listView.setVerticalScrollBarEnabled(true);
		_listView.setOnItemClickListener(this);
		
		
		_adapter = new MyAdapter(this);
		_listView.setAdapter(_adapter);
		
		buildKeyboard();
		
//		HomeDataLoader.loadAllAppList(this, _allAppList);
	}
	
	
	private OnClickListener _charOnClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();

			String string = _inputBoxTextView.getText().toString();
			
			if( id == CHAR_ID_SPACE )
			{
				string += " ";
				
			}
			else if( id == CHAR_ID_CLEAR )
			{
				string = "";
			}
			else if( id == CHAR_ID_BACKSPACE )
			{
				if( string.length() > 0 )
					string = string.substring(0, string.length() - 1);
			}
			else
			{
				int idx = id - CHAR_ID_BASE;
				String s = _charString.substring(idx, idx  + 1);
				
				string += s;
			}
			
			
			_inputBoxTextView.setText(string);
			
			doSearch(string);
			
		}
		
	};
	

	
	private void buildKeyboard()
	{
		RelativeLayout keyboard = (RelativeLayout)this.findViewById(R.id.keyboard);

		
		
		int baseId = CHAR_ID_BASE;
		
		int idx = 0;
		int line = 0;
		int lineStartIndex = 0;
		int total = _charString.length();
		for(int i = 0;i < total; i++ )
		{
			int id = baseId + i;
			
			TextView tv = new TextView(this);
			tv.setGravity(Gravity.CENTER);
			tv.setId(id);
			tv.setText(_charString.substring(i, i + 1));
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.getResources().getDimension(R.dimen.search_char_text_size));
			tv.setTextColor(Color.WHITE);
			tv.setHighlightColor(Color.BLACK);
			tv.setBackgroundResource(R.drawable.btn_search_char_selector);
			tv.setFocusable(true);
			
			tv.setOnClickListener(_charOnClickListener);
			
			int w = MyUtils.dip2px(this, this.getResources().getDimension(R.dimen.search_char_view_width));
			int h = MyUtils.dip2px(this, this.getResources().getDimension(R.dimen.search_char_view_height));
			
			RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(w, h);
			
			if( idx > 0 )
			{
				rllp.addRule(RelativeLayout.RIGHT_OF, id - 1);
				rllp.leftMargin = MyUtils.dip2px(this, this.getResources().getDimension(R.dimen.search_char_view_space));
				rllp.addRule(RelativeLayout.ALIGN_TOP, id - 1);
			}
			else if( line > 0 )
			{
				rllp.addRule(RelativeLayout.BELOW, id - 6);
				rllp.addRule(RelativeLayout.ALIGN_LEFT, id - 6);
				rllp.topMargin = MyUtils.dip2px(this, this.getResources().getDimension(R.dimen.search_char_view_space));
			}
			
			
			keyboard.addView(tv, rllp);
			
			idx++;
			
			if( idx >= 6)
			{
				idx = 0;
				line++;
				if( i < total - 1)
					lineStartIndex = id + 1;
			}
		}
		
		
		
		TextView tv = new TextView(this);
		tv.setGravity(Gravity.CENTER);
		tv.setId(CHAR_ID_SPACE);
		tv.setText("空格");
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.getResources().getDimension(R.dimen.search_char_text_size));
		tv.setTextColor(Color.WHITE);
		tv.setHighlightColor(Color.BLACK);
		tv.setBackgroundResource(R.drawable.btn_search_char_selector);
		tv.setFocusable(true);
		tv.setOnClickListener(_charOnClickListener);
		
		int w = MyUtils.dip2px(this, this.getResources().getDimension(R.dimen.search_char_view_width));
		int h = MyUtils.dip2px(this, this.getResources().getDimension(R.dimen.search_char_view_height));
		
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(w*2, h);
		rllp.addRule(RelativeLayout.BELOW, lineStartIndex);
		rllp.addRule(RelativeLayout.ALIGN_LEFT, lineStartIndex);
		rllp.addRule(RelativeLayout.ALIGN_RIGHT, lineStartIndex + 1);
		rllp.topMargin = MyUtils.dip2px(this, this.getResources().getDimension(R.dimen.search_char_view_space));
		keyboard.addView(tv, rllp);
		
		
		tv = new TextView(this);
		tv.setGravity(Gravity.CENTER);
		tv.setId(CHAR_ID_CLEAR);
		tv.setText("清空");
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.getResources().getDimension(R.dimen.search_char_text_size));
		tv.setTextColor(Color.WHITE);
		tv.setHighlightColor(Color.BLACK);
		tv.setBackgroundResource(R.drawable.btn_search_char_selector);
		tv.setFocusable(true);
		tv.setOnClickListener(_charOnClickListener);
		
		w = MyUtils.dip2px(this, this.getResources().getDimension(R.dimen.search_char_view_width));
		h = MyUtils.dip2px(this, this.getResources().getDimension(R.dimen.search_char_view_height));
		
		rllp = new RelativeLayout.LayoutParams(w*2, h);
		rllp.addRule(RelativeLayout.BELOW, lineStartIndex);
		rllp.addRule(RelativeLayout.ALIGN_LEFT, lineStartIndex + 2);
		rllp.addRule(RelativeLayout.ALIGN_RIGHT, lineStartIndex + 3);
		rllp.addRule(RelativeLayout.ALIGN_TOP, CHAR_ID_SPACE);
		keyboard.addView(tv, rllp);
		
		tv = new TextView(this);
		tv.setGravity(Gravity.CENTER);
		tv.setId(CHAR_ID_BACKSPACE);
		tv.setText("退格");
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.getResources().getDimension(R.dimen.search_char_text_size));
		tv.setTextColor(Color.WHITE);
		tv.setHighlightColor(Color.BLACK);
		tv.setBackgroundResource(R.drawable.btn_search_char_selector);
		tv.setFocusable(true);
		tv.setOnClickListener(_charOnClickListener);
		
		w = MyUtils.dip2px(this, this.getResources().getDimension(R.dimen.search_char_view_width));
		h = MyUtils.dip2px(this, this.getResources().getDimension(R.dimen.search_char_view_height));
		
		rllp = new RelativeLayout.LayoutParams(w*2, h);
		rllp.addRule(RelativeLayout.BELOW, lineStartIndex);
		rllp.addRule(RelativeLayout.ALIGN_LEFT, lineStartIndex + 4);
		rllp.addRule(RelativeLayout.ALIGN_RIGHT, lineStartIndex + 5);
		rllp.addRule(RelativeLayout.ALIGN_TOP, CHAR_ID_SPACE);
		keyboard.addView(tv, rllp);
	}
	
	
	
	private void doSearch(String keyword)
	{
		
		_appList.clear();
		_adapter.notifyDataSetChanged();
		
		_searchKeyword = keyword;
		
		_loadingAnimation = AnimationUtils.loadAnimation(
				this, R.anim.loading_animation);
		// 使用ImageView显示动画
		_loadingImageView.startAnimation(_loadingAnimation);
		
		_loadingLayer.setVisibility(View.VISIBLE);
		
		_loadingThread = new LoadThread();
		_loadingThread.start();
	}
	
	public int doSearchInLocal(String _pyKeyword)
	{
		if( _pyKeyword == null || _pyKeyword.length() < 1 )
			return 0;

		String s = _pyKeyword.toLowerCase();
		
		for(int i = 0;i < _allAppList.size(); i++ )
		{
			AppInfo ai = _allAppList.get(i);
			AppConfig.showLog(ai.py + "---" + s);
			if( ai.py.indexOf(s) >= 0 )
			{
				_appList.add(ai);
			}
		}
		
		return 0;
	}
	
	public int doLoadData()
	{
		if( _allAppList.size() > 0 )
		{
			return doSearchInLocal(_searchKeyword);
		}
		
		String urlString = WAPI.getSearchAppListURLString(this, _searchKeyword);
		String content = WAPI.http_get_content(urlString);
		if( content == null || content.length() < 1)
			return 1;
		
		
		int iret = WAPI.parseAppListJSONResponse(this, content, _appList);
		if( iret != 0 )
			return iret;
		
		// 成功了
		return 0;
	}
	

	private class LoadThread extends Thread
	{
		public void run()
		{
			int iret = 1;
			try
			{
				iret = doLoadData();
			}
			catch(Exception e)
			{
				
			}
			
			Message msg = new Message();
			msg.what = iret;
			handler.sendMessage(msg);
		}
	}	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			if( msg.what == 0 )
			{
				// 加载成功
				if( _appList.size() == 0 )
				{
					// 没有数据
					_loadingTextView.setText("暂时没有数据!");
					_loadingAnimation.cancel();
					_loadingImageView.setAnimation(null);
					_loadingImageView.setVisibility(View.GONE);
				}
				else
				{
					//TODO: update
					_adapter.notifyDataSetChanged();
					_listView.setVisibility(View.VISIBLE);
					
					
					_loadingAnimation.cancel();
					_loadingImageView.setAnimation(null);
					_loadingLayer.setVisibility(View.INVISIBLE);
				}
			}
			else
			{
				_loadingAnimation.cancel();
				_loadingImageView.setAnimation(null);
				_loadingImageView.setVisibility(View.GONE);
				_loadingTextView.setText("加载数据失败，请稍后再试一次！");
			}
		}
	};	
	
	
	private class MyAdapter extends BaseAdapter
	{
		private Context _context;
		private LayoutInflater _inflater;
		
		public MyAdapter(Context context)
		{
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
		
		private class ViewHolder
		{
			ImageView imageView;
			TextView textViewTitle;
			TextView textViewInfo;
			
			int keyId;
		};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if( convertView == null )
			{
				convertView = _inflater.inflate(R.layout.layout_search_app_item, null);
				
				holder = new ViewHolder();
				holder.imageView = (ImageView)convertView.findViewById(R.id.imageViewIcon);
				holder.textViewTitle = (TextView)convertView.findViewById(R.id.textViewTitle);
				holder.textViewInfo = (TextView)convertView.findViewById(R.id.textViewInfo);
				convertView.setTag(holder);
						
			}
			else
				holder = (ViewHolder)convertView.getTag();
			
				
			AppInfo appInfo = _appList.get(position);
			
			holder.keyId = appInfo.id;
			
			holder.textViewTitle.setText(appInfo.name);
			String s = String.format("版本：%s  大小：%s", appInfo.version, MyUtils.GetSizeString(appInfo.size));
			holder.textViewInfo.setText(s);
			
			holder.imageView.setImageResource(R.drawable.app_loading);
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("appinfo", appInfo);
			params.put("view", convertView);
			params.put("roundcorner", 15);

			_imgDownloader.downloadImage(_context, appInfo.icon, new OnDownloadTaskListener() {
				
				@Override
				public void onDownloadSuccessfully(DownloadTask downloadTask) {
					// TODO Auto-generated method stub
					AppInfo appInfo = (AppInfo)downloadTask.params.get("appinfo");
					View view = (View)downloadTask.params.get("view");
					ViewHolder holder = (ViewHolder)view.getTag();
					if( appInfo.id == holder.keyId )
					{
						holder.imageView.setImageBitmap(downloadTask.getBitmap());
					}
				}

				@Override
				public void onDownloadFailed(int errCode,
						DownloadTask downloadTask) {
					// TODO Auto-generated method stub
					
				}
			}, params);
			
//			Bitmap bmp = ImageUtils.loadBitmapFromResource(_context, R.drawable.ad1);
//			if( bmp != null )
//			{
//				bmp = ImageUtils.getRoundedCornerBitmap(bmp, 30);
//				holder.imageView.setImageBitmap(bmp);
//			}
			
			
			
			return convertView;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.app_list, menu);
		return true;
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		AppInfo appInfo = _appList.get(arg2);
		_appDetailDialog = AppDetailDialog.showDetailDialog(this, appInfo, _onAppDetailDismissListener);
	}	

	
	@Override
	public void onResume()
	{
		AppConfig.showLog("onResume(), ....");
		if( _appDetailDialog != null )
			_appDetailDialog.onResume();
		
		super.onResume();
	}
	
	private AppDetailDialog.OnAppDetailDismissListener _onAppDetailDismissListener = new AppDetailDialog.OnAppDetailDismissListener()
	{

		@Override
		public void onAppDetailDismiss() {
			// TODO Auto-generated method stub
			_appDetailDialog = null;
		}
		
	};
	
}
