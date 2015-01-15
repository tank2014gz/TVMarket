package com.soniq.tvmarket.ui;

import java.util.ArrayList;

import com.soniq.tvmarket.component.CustomUI;
import com.soniq.tvmarket.data.RecommendInfo;
import com.soniq.tvmarket.model.ClientUpgrade;
import com.soniq.tvmarket.utils.*;

import java.util.List;

import com.soniq.tvmarket.ui.widget.GalleryMetroItemView;
import com.soniq.tvmarket.ui.widget.MetroItemView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.soniq.tvmarket.R;

public class SettingPage extends BasePage {

	  private MetroItemView _itemRemoteController = null;
	  private MetroItemView _itemChangeBackground = null;
	  private MetroItemView _itemDownload = null;
	  private MetroItemView _itemApps = null;
	  private MetroItemView _itemUpgrade = null;
	  private MetroItemView _itemHelp = null;
	  private MetroItemView _itemFeedback = null;
	  private MetroItemView _itemAbout = null;
	  
	  private MetroItemView _reflected_image_item_remote_controller = null;
	  private MetroItemView _reflected_image_item_download = null;
	  private MetroItemView _reflected_image_item_apps = null;
	  private MetroItemView _reflected_image_item_upgrade = null;
	  private MetroItemView _reflected_image_item_about = null;

	  private MetroItemView mLastFocus = null;
	  private MetroItemView mLastLoseFocus = null;
	  
	  private List<MetroItemView> _buttonList = null;
	  
	  
	  private Dialog _checkVersionDialog = null;
	
	
	
	  private RelativeLayout mContainer = null;
	  private Context _context = null;

	public SettingPage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		_context = context;
		
		init();
	}
	
	
	@Override
	public void setCurrentFocusItemWithDefault()
	{
		this._itemDownload.requestFocus();
	}
	
	@Override
	public void setCurrentFocusItemWithLeft()
	{
		if( this.mLastLoseFocus != null )
			this.mLastLoseFocus.requestFocus();
		else
			this._itemRemoteController.requestFocus();
	}
	
	@Override
	public void setCurrentFocusItemWithRight()
	{
		if( this.mLastLoseFocus != null )
			this.mLastLoseFocus.requestFocus();
		else
			this._itemAbout.requestFocus();
	}
	
	private void setViewFocusState(View v, boolean focused)
	{
		if( focused )
		{
			
			ObjectAnimator animX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 1.05f);
			animX.setDuration(200);
			
			ObjectAnimator animY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 1.05f);
			animY.setDuration(200);
			
			AnimatorSet as = new AnimatorSet();
			as.play(animX);
			as.play(animY);
			as.start();
			
		}
		else
		{
			ObjectAnimator animX = ObjectAnimator.ofFloat(v, "scaleX", 1.05f, 1.0f);
			animX.setDuration(200);
			
			ObjectAnimator animY = ObjectAnimator.ofFloat(v, "scaleY", 1.05f, 1.0f);
			animY.setDuration(200);
			
			AnimatorSet as = new AnimatorSet();
			as.play(animX);
			as.play(animY);
			as.start();
		}
	}

	
	private View.OnFocusChangeListener mFocusListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			
			if( hasFocus )
				v.bringToFront();
			
			
			if( ( v instanceof MetroItemView) )
			{
				((MetroItemView)v).onImageButtonFocusChanged(hasFocus);
				
				
				if( hasFocus )
				{
					setViewFocusState(v, true);

					SettingPage.this.mLastFocus = (MetroItemView)v;
				}
				else
				{
					setViewFocusState(v, false);
					SettingPage.this.mLastLoseFocus = (MetroItemView)v;
				}
				
				
				if( v.getId() == R.id.button_item_remote_controller )
				{
					setViewFocusState(_reflected_image_item_remote_controller, hasFocus);
				}
				else if( v.getId() == R.id.button_item_download )
				{
					setViewFocusState(_reflected_image_item_download, hasFocus);
				}
				
				else if( v.getId() == R.id.button_item_apps )
				{
					setViewFocusState(_reflected_image_item_apps, hasFocus);
				}
				else if( v.getId() == R.id.button_item_upgrade )
				{
					setViewFocusState(_reflected_image_item_upgrade, hasFocus);
				}
				else if( v.getId() == R.id.button_item_about )
				{
					setViewFocusState(_reflected_image_item_about, hasFocus);
				}
				
				
				
			}
			
			SettingPage.this.mContainer.invalidate();
			
		}
	};
	  
	  private View.OnClickListener mOnClickListener = new View.OnClickListener()
	  {
	    public void onClick(View View)
	    {
	    	switch(View.getId() )
	    	{
	    	case R.id.button_item_remote_controller:
	    	{
	    		CustomUI.showTipsDialog(_context, "稍后推出，敬请期待");
	    	}
	    	break;
	    	case R.id.button_item_download:
	    	{
	    		Intent intent = new Intent(_context, DownloadManageActivity.class);
	    		_context.startActivity(intent);
	    	}
	    	break;
	    	case R.id.button_item_apps:
	    	{
	    		Intent intent = new Intent(_context, AppManageActivity.class);
	    		_context.startActivity(intent);
	    	}
	    	break;
	    	case R.id.button_item_change_background:
	    	{
	    		Intent intent = new Intent(_context, SetBackgroundActivity.class);
	    		_context.startActivity(intent);
	    	}
	    	break;
	    	case R.id.button_item_upgrade:
	    	{
	    		if( _checkVersionDialog != null)
	    			return;
	    		
	    		_checkVersionDialog = CustomUI.createLoadingDialog(_context, "正在检查更新...");
	    		_checkVersionDialog.show();
	    		
	    		ClientUpgrade cu = new ClientUpgrade(_context);
	    		cu.startCheckVersion(new ClientUpgrade.ClientUpgradeCallback() {
					
					@Override
					public void onCheckFinished(int state) {
						_checkVersionDialog.dismiss();
						_checkVersionDialog = null;
						
						if( state == ClientUpgrade.STATE_ALREADY_NEW_VERSION )
						{
							CustomUI.showTipsDialog(_context, "已经是最新版本了！");
						}
						else if( state < 0 )
						{
							CustomUI.showTipsDialog(_context, "获取版本信息失败！");
						}
					}
				});
	    	}
	    	break;
	    	case R.id.button_item_feedback:
	    	{
	    		Intent intent = new Intent(_context, FeedbackActivity.class);
	    		_context.startActivity(intent);
	    	}
	    	break;
	    	case R.id.button_item_about:
	    		CustomUI.showAboutDialog(_context, "应用市场","版本号: "+getVersion(_context),"版权所有: 声光智能科技");
	    	break;
	    	case R.id.button_item_help:
	    		CustomUI.showHelpDialog(_context, "请致电:400-605-1801，我们将提供帮助！");
	    	break;
	    	default:
	    		break;
	    	
	    	}
	    }
	  };	
	  public  String getVersion(Context context) {
			PackageManager pm = context.getPackageManager();
			try {
				// 代表的就是清单文件的信息。
				PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
				return packageInfo.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				// 肯定不会发生。
				// can't reach
				return "";
			}
			
		}
	  private void init()
	  {
		    addView(((LayoutInflater)this._context.getSystemService("layout_inflater")).
		    		inflate(R.layout.view_setting, null));
		  
		    this.mContainer = ((RelativeLayout)findViewById(R.id.tab_container));
		    
		    this.mContainer.setClipChildren(false);
		    mContainer.setOnKeyListener(new OnKeyListener(){

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// TODO Auto-generated method stub
					switch (keyCode) {  
					      case KeyEvent.KEYCODE_DPAD_DOWN://按向下键  
					    	  _itemRemoteController.requestFocus();
					            break;  
					        case KeyEvent.KEYCODE_DPAD_UP:// 按向上键  
					        	_itemChangeBackground.requestFocus();
					        	break;
					        case KeyEvent.KEYCODE_DPAD_LEFT://按向左键  
					        	_itemDownload.requestFocus();
					        	break;
					        case KeyEvent.KEYCODE_DPAD_RIGHT://按向右键  
					        	_itemApps.requestFocus();
					        	break;
					        default:  
					        	_itemRemoteController.requestFocus();
					            break;  
					}
					return false;
				}
		    	
		    });
		    
		    this._itemRemoteController = ((MetroItemView)findViewById(R.id.button_item_remote_controller));
		    this._itemChangeBackground = ((MetroItemView)findViewById(R.id.button_item_change_background));
		    this._itemDownload = ((MetroItemView)findViewById(R.id.button_item_download));
		    this._itemApps = ((MetroItemView)findViewById(R.id.button_item_apps));
		    this._itemUpgrade = ((MetroItemView)findViewById(R.id.button_item_upgrade));
		    this._itemHelp = ((MetroItemView)findViewById(R.id.button_item_help));
		    this._itemFeedback = ((MetroItemView)findViewById(R.id.button_item_feedback));
		    this._itemAbout = ((MetroItemView)findViewById(R.id.button_item_about));
		    
		    
			this._reflected_image_item_remote_controller = (MetroItemView)findViewById(R.id.reflected_image_button_item_remote_controller);
			this._reflected_image_item_download = (MetroItemView)findViewById(R.id.reflected_image_button_item_download);
			this._reflected_image_item_apps = (MetroItemView)findViewById(R.id.reflected_image_button_item_apps);
			this._reflected_image_item_upgrade = (MetroItemView)findViewById(R.id.reflected_image_button_item_upgrade);
			this._reflected_image_item_about = (MetroItemView)findViewById(R.id.reflected_image_button_item_about);
		    
		    
		    this._buttonList = new ArrayList<MetroItemView>();
		    this._buttonList.add(this._itemRemoteController);
		    this._buttonList.add(this._itemChangeBackground);
		    this._buttonList.add(this._itemDownload);
		    this._buttonList.add(this._itemApps);
		    this._buttonList.add(this._itemUpgrade);
		    this._buttonList.add(this._itemHelp);
		    this._buttonList.add(this._itemFeedback);
		    this._buttonList.add(this._itemAbout);
		    
		    this._itemRemoteController.setOnFocusChangeListener(this.mFocusListener);
		    this._itemChangeBackground.setOnFocusChangeListener(this.mFocusListener);
		    this._itemDownload.setOnFocusChangeListener(this.mFocusListener);
		    this._itemApps.setOnFocusChangeListener(this.mFocusListener);
		    this._itemUpgrade.setOnFocusChangeListener(this.mFocusListener);
		    this._itemHelp.setOnFocusChangeListener(this.mFocusListener);
		    this._itemFeedback.setOnFocusChangeListener(this.mFocusListener);
		    this._itemAbout.setOnFocusChangeListener(this.mFocusListener);

		    this._itemRemoteController.setOnClickListener(this.mOnClickListener);
		    this._itemChangeBackground.setOnClickListener(this.mOnClickListener);
		    this._itemDownload.setOnClickListener(this.mOnClickListener);
		    this._itemApps.setOnClickListener(this.mOnClickListener);
		    this._itemUpgrade.setOnClickListener(this.mOnClickListener);
		    this._itemHelp.setOnClickListener(this.mOnClickListener);
		    this._itemFeedback.setOnClickListener(this.mOnClickListener);
		    this._itemAbout.setOnClickListener(this.mOnClickListener);
		    
		    initChildrenFocusID();
		    
		    initData();
	  }
	  
	  private void initChildrenFocusID()
	  {
	    this._itemRemoteController.setNextFocusLeftId(-1);
	    this._itemRemoteController.setNextFocusUpId(-1);
	    this._itemRemoteController.setNextFocusRightId(R.id.button_item_change_background);
	    this._itemRemoteController.setNextFocusDownId(-1);
	    
	    this._itemChangeBackground.setNextFocusLeftId(R.id.button_item_remote_controller);
	    this._itemChangeBackground.setNextFocusUpId(-1);
	    this._itemChangeBackground.setNextFocusRightId(R.id.button_item_upgrade);
	    this._itemChangeBackground.setNextFocusDownId(R.id.button_item_download);

	    this._itemDownload.setNextFocusLeftId(R.id.button_item_remote_controller);
	    this._itemDownload.setNextFocusUpId(R.id.button_item_change_background);
	    this._itemDownload.setNextFocusRightId(R.id.button_item_apps);
	    this._itemDownload.setNextFocusDownId(-1);

	    this._itemApps.setNextFocusLeftId(R.id.button_item_download);
	    this._itemApps.setNextFocusUpId(R.id.button_item_change_background);
	    this._itemApps.setNextFocusRightId(R.id.button_item_upgrade);
	    this._itemApps.setNextFocusDownId(-1);

	    this._itemUpgrade.setNextFocusLeftId(R.id.button_item_change_background);
	    this._itemUpgrade.setNextFocusUpId(-1);
	    this._itemUpgrade.setNextFocusRightId(R.id.button_item_help);
	    this._itemUpgrade.setNextFocusDownId(-1);

	    this._itemHelp.setNextFocusLeftId(R.id.button_item_upgrade);
	    this._itemHelp.setNextFocusUpId(-1);
	    this._itemHelp.setNextFocusRightId(R.id.button_item_feedback);
	    this._itemHelp.setNextFocusDownId(R.id.button_item_about);

	    this._itemFeedback.setNextFocusLeftId(R.id.button_item_help);
	    this._itemFeedback.setNextFocusUpId(R.id.btn_search);
	    this._itemFeedback.setNextFocusRightId(-1);
	    this._itemFeedback.setNextFocusDownId(R.id.button_item_about);
	    
	    this._itemAbout.setNextFocusLeftId(R.id.button_item_upgrade);
	    this._itemAbout.setNextFocusUpId(R.id.button_item_help);
	    this._itemAbout.setNextFocusRightId(-1);
	    this._itemAbout.setNextFocusDownId(-1);

	  }

	  private void initData()
	  {
			this._itemRemoteController.setRecommendImageRes(R.drawable.setting_remote_controller);
			this._itemChangeBackground.setRecommendImageRes(R.drawable.setting_change_background);
			this._itemDownload.setRecommendImageRes(R.drawable.setting_download);
			this._itemApps.setRecommendImageRes(R.drawable.setting_apps);
			this._itemUpgrade.setRecommendImageRes(R.drawable.setting_upgrade);
			this._itemHelp.setRecommendImageRes(R.drawable.setting_help);
			this._itemFeedback.setRecommendImageRes(R.drawable.setting_feedback);
			this._itemAbout.setRecommendImageRes(R.drawable.setting_about);
		  		  
			Bitmap  bitmap = ImageUtils.loadBitmapFromResource(_context, R.drawable.setting_remote_controller);
			this._reflected_image_item_remote_controller.setRecommendImageBitmap(ImageUtils.createReflectedImage(bitmap, 4));
			  
			bitmap = ImageUtils.loadBitmapFromResource(_context, R.drawable.setting_download);
			this._reflected_image_item_download.setRecommendImageBitmap(ImageUtils.createReflectedImage(bitmap));
			  
			bitmap = ImageUtils.loadBitmapFromResource(_context, R.drawable.setting_apps);
			this._reflected_image_item_apps.setRecommendImageBitmap(ImageUtils.createReflectedImage(bitmap));
			
			bitmap = ImageUtils.loadBitmapFromResource(_context, R.drawable.setting_upgrade);
			this._reflected_image_item_upgrade.setRecommendImageBitmap(ImageUtils.createReflectedImage(bitmap, 4));
				
			bitmap = ImageUtils.loadBitmapFromResource(_context, R.drawable.setting_about);
			this._reflected_image_item_about.setRecommendImageBitmap(ImageUtils.createReflectedImage(bitmap));
	  }
}
