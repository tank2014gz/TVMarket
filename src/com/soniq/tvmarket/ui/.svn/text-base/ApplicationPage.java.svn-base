package com.soniq.tvmarket.ui;

import java.util.ArrayList;

import com.soniq.tvmarket.data.AppClassInfo;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.RecommendInfo;
import com.soniq.tvmarket.model.HomeDataLoader;
import com.soniq.tvmarket.utils.*;

import java.util.List;

import com.soniq.tvmarket.ui.widget.GalleryMetroItemView;
import com.soniq.tvmarket.ui.widget.MetroItemView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.soniq.tvmarket.R;

public class ApplicationPage extends BasePage {

	  private MetroItemView _item1 = null;
	  private MetroItemView _item2 = null;
	  private MetroItemView _item3 = null;
	  private MetroItemView _item4 = null;
	  private MetroItemView _item5 = null;
	  private MetroItemView _item6 = null;
	  private MetroItemView _item7 = null;
	  private MetroItemView _item8 = null;
	  private MetroItemView _item9 = null;
	  
	  private MetroItemView _reflected_image_item1 = null;
	  private MetroItemView _reflected_image_item3 = null;
	  private MetroItemView _reflected_image_item5 = null;
	  private MetroItemView _reflected_image_item7 = null;
	  private MetroItemView _reflected_image_item9 = null;
	  
	  private AppClassInfo[] _appClassList = null;

	  private MetroItemView mLastFocus = null;
	  private MetroItemView mLastLoseFocus = null;
	  
	  private List<MetroItemView> _buttonList = null;
	
	
	
	  private RelativeLayout mContainer = null;
	  private Context _context = null;

	public ApplicationPage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		_context = context;
		
		init();
	}
	
	
	@Override
	public void setCurrentFocusItemWithDefault()
	{
		this._item1.requestFocus();
	}
	
	@Override
	public void setCurrentFocusItemWithLeft()
	{
		if( this.mLastLoseFocus != null )
			this.mLastLoseFocus.requestFocus();
		else
			this._item1.requestFocus();
	}
	
	@Override
	public void setCurrentFocusItemWithRight()
	{
		if( this.mLastLoseFocus != null )
			this.mLastLoseFocus.requestFocus();
		else
			this._item8.requestFocus();
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
			
			if( v.getId() == R.id.button_item2) 
			{
				MetroItemView miv = (MetroItemView)v;
				miv.playAnimation();
			}
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

					ApplicationPage.this.mLastFocus = (MetroItemView)v;
				}
				else
				{
					setViewFocusState(v, false);
					ApplicationPage.this.mLastLoseFocus = (MetroItemView)v;
				}
				
				
				
				if( v.getId() == R.id.button_item1 )
				{
					setViewFocusState(_reflected_image_item1, hasFocus);
				}
				else if( v.getId() == R.id.button_item3 )
				{
					setViewFocusState(_reflected_image_item3, hasFocus);
				}
				
				else if( v.getId() == R.id.button_item5 )
				{
					setViewFocusState(_reflected_image_item5, hasFocus);
				}
				else if( v.getId() == R.id.button_item7 )
				{
					setViewFocusState(_reflected_image_item7, hasFocus);
				}
				else if( v.getId() == R.id.button_item9 )
				{
					setViewFocusState(_reflected_image_item9, hasFocus);
				}
				
				
			}
			
			ApplicationPage.this.mContainer.invalidate();
			
		}
	};
	
	
	public void showAppList(int dataIndex)
	{
		AppClassInfo aci = _appClassList[dataIndex];
		
		Intent intent = new Intent(_context, AppListActivity.class);
		Bundle b = new Bundle();
		b.putParcelable("class", aci);
		intent.putExtras(b);
		_context.startActivity(intent);
	}
	
	  
	  private View.OnClickListener mOnClickListener = new View.OnClickListener()
	  {
	    public void onClick(View view)
	    {
	    	if( view.getId() == R.id.button_item1 )
	    	{
	    		// 广告点击
	    		GalleryMetroItemView gmiv = (GalleryMetroItemView)view;
	    		int num = gmiv.getItemCount();
	    		int cur = gmiv.getCurrentItemIndex();
	    		String pos = RecommendInfo.AD_POS_APP;
	    		
				List<RecommendInfo> list = new ArrayList<RecommendInfo>();
		    	int cnt = HomeDataLoader.getInstance(_context).getRecommendList(pos,list, num);
		    	
		    	RecommendInfo recommendInfo = list.get(cur);
		    	Log.v(AppConfig.TAG, recommendInfo.title);
		    	
		    	MainActivity.getInstance().OnAdClicked(recommendInfo);
		    	
	    		
	    	}
	    	else
	    	{
		    	Integer n = (Integer)view.getTag();
		    	showAppList(n.intValue());
	    	}
	    }
	  };	
	  
	  
	
	  
	  private void init()
	  {
		    addView(((LayoutInflater)this._context.getSystemService("layout_inflater")).
		    		inflate(R.layout.view_application, null));
		  
		    this.mContainer = ((RelativeLayout)findViewById(R.id.tab_container));
		    
		    
		    this._item1 = ((MetroItemView)findViewById(R.id.button_item1));
		    this._item2 = ((MetroItemView)findViewById(R.id.button_item2));
		    this._item3 = ((MetroItemView)findViewById(R.id.button_item3));
		    this._item4 = ((MetroItemView)findViewById(R.id.button_item4));
		    this._item5 = ((MetroItemView)findViewById(R.id.button_item5));
		    this._item6 = ((MetroItemView)findViewById(R.id.button_item6));
		    this._item7 = ((MetroItemView)findViewById(R.id.button_item7));
		    this._item8 = ((MetroItemView)findViewById(R.id.button_item8));
		    this._item9 = ((MetroItemView)findViewById(R.id.button_item9));
		    
		    this._reflected_image_item1 = (MetroItemView)findViewById(R.id.reflected_image_button_item1);
		    this._reflected_image_item3 = (MetroItemView)findViewById(R.id.reflected_image_button_item3);
		    this._reflected_image_item5 = (MetroItemView)findViewById(R.id.reflected_image_button_item5);
		    this._reflected_image_item7 = (MetroItemView)findViewById(R.id.reflected_image_button_item7);
		    this._reflected_image_item9 = (MetroItemView)findViewById(R.id.reflected_image_button_item9);
		    
		    
		    
		    this._buttonList = new ArrayList<MetroItemView>();
		    this._buttonList.add(this._item1);
		    this._buttonList.add(this._item2);
		    this._buttonList.add(this._item3);
		    this._buttonList.add(this._item4);
		    this._buttonList.add(this._item5);
		    this._buttonList.add(this._item6);
		    this._buttonList.add(this._item7);
		    this._buttonList.add(this._item8);
		    this._buttonList.add(this._item9);
		    
		    this._item1.setOnFocusChangeListener(this.mFocusListener);
		    this._item2.setOnFocusChangeListener(this.mFocusListener);
		    this._item3.setOnFocusChangeListener(this.mFocusListener);
		    this._item4.setOnFocusChangeListener(this.mFocusListener);
		    this._item5.setOnFocusChangeListener(this.mFocusListener);
		    this._item6.setOnFocusChangeListener(this.mFocusListener);
		    this._item7.setOnFocusChangeListener(this.mFocusListener);
		    this._item8.setOnFocusChangeListener(this.mFocusListener);
		    this._item9.setOnFocusChangeListener(this.mFocusListener);

		    this._item1.setOnClickListener(this.mOnClickListener);
		    this._item2.setOnClickListener(this.mOnClickListener);
		    this._item3.setOnClickListener(this.mOnClickListener);
		    this._item4.setOnClickListener(this.mOnClickListener);
		    this._item5.setOnClickListener(this.mOnClickListener);
		    this._item6.setOnClickListener(this.mOnClickListener);
		    this._item7.setOnClickListener(this.mOnClickListener);
		    this._item8.setOnClickListener(this.mOnClickListener);
		    this._item9.setOnClickListener(this.mOnClickListener);
		    
		    initChildrenFocusID();
		    
		    initData();
	  }
	  
	  
	  private void initChildrenFocusID()
	  {
	    this._item1.setNextFocusLeftId(-1);
	    this._item1.setNextFocusUpId(-1);
	    this._item1.setNextFocusRightId(R.id.button_item2);
	    this._item1.setNextFocusDownId(-1);
	    
	    this._item2.setNextFocusLeftId(R.id.button_item1);
	    this._item2.setNextFocusUpId(-1);
	    this._item2.setNextFocusRightId(R.id.button_item4);
	    this._item2.setNextFocusDownId(R.id.button_item3);

	    this._item3.setNextFocusLeftId(R.id.button_item1);
	    this._item3.setNextFocusUpId(R.id.button_item2);
	    this._item3.setNextFocusRightId(R.id.button_item5);
	    this._item3.setNextFocusDownId(-1);
	    
	    
	    this._item4.setNextFocusLeftId(R.id.button_item2);
	    this._item4.setNextFocusUpId(-1);
	    this._item4.setNextFocusRightId(R.id.button_item6);
	    this._item4.setNextFocusDownId(R.id.button_item5);

	    this._item5.setNextFocusLeftId(R.id.button_item3);
	    this._item5.setNextFocusUpId(R.id.button_item4);
	    this._item5.setNextFocusRightId(R.id.button_item7);
	    this._item5.setNextFocusDownId(-1);
	    

	    this._item6.setNextFocusLeftId(R.id.button_item4);
	    this._item6.setNextFocusUpId(-1);
	    this._item6.setNextFocusRightId(R.id.button_item8);
	    this._item6.setNextFocusDownId(R.id.button_item7);

	    this._item7.setNextFocusLeftId(R.id.button_item5);
	    this._item7.setNextFocusUpId(R.id.button_item6);
	    this._item7.setNextFocusRightId(R.id.button_item9);
	    this._item7.setNextFocusDownId(-1);
	    
	    this._item8.setNextFocusLeftId(R.id.button_item6);
	    this._item8.setNextFocusUpId(R.id.btn_search);
	    this._item8.setNextFocusRightId(-1);
	    this._item8.setNextFocusDownId(R.id.button_item9);

	    this._item9.setNextFocusLeftId(R.id.button_item7);
	    this._item9.setNextFocusUpId(R.id.button_item8);
	    this._item9.setNextFocusRightId(-1);
	    this._item9.setNextFocusDownId(-1);
	  }

	  private void initData()
	  {
		  _appClassList = new AppClassInfo[]
				  {
				  		new AppClassInfo(0, "AD", "AD"),
				  		
				  		new AppClassInfo(0, "class_life", "生活"),
				  		new AppClassInfo(0, "class_ecommerce", "电商"),
				  		new AppClassInfo(0, "class_social", "社交"),
				  		new AppClassInfo(0, "class_reading", "阅读"),
				  		new AppClassInfo(0, "class_video", "影音"),
				  		new AppClassInfo(0, "class_map", "地图"),
				  		new AppClassInfo(0, "class_safe", "安全"),
				  		new AppClassInfo(0, "class_system", "系统")
				  };
		  
		  
		  this._item1.setTag(0);
		  
		  this._item2.setRecommendImageRes(R.drawable.metro_item_life);
		  this._item2.setTag(1);
		  this._item3.setRecommendImageRes(R.drawable.app3);
		  this._item3.setTag(2);
		  this._item4.setRecommendImageRes(R.drawable.app4);
		  this._item4.setTag(3);
		  this._item5.setRecommendImageRes(R.drawable.app5);
		  this._item5.setTag(4);
		  this._item6.setRecommendImageRes(R.drawable.app6);
		  this._item6.setTag(5);
		  this._item7.setRecommendImageRes(R.drawable.app7);
		  this._item7.setTag(6);
		  this._item8.setRecommendImageRes(R.drawable.app8);
		  this._item8.setTag(7);
		  this._item9.setRecommendImageRes(R.drawable.app9);
		  this._item9.setTag(8);
		  
		  
		  GalleryMetroItemView gmiv = (GalleryMetroItemView)this._item1;
		  gmiv.setReflectedView(_reflected_image_item1, 4);
		  
		  
		  ArrayList<RecommendInfo> list = new ArrayList<RecommendInfo>();
		  HomeDataLoader.getInstance(_context).getRecommendList(RecommendInfo.AD_POS_APP,list, 3);
		  gmiv.setRecommendList(list);
		  
		  
		  if( list.size() == 0 )
			  gmiv.setRecommendImageRes(R.drawable.ad_placeholder);
	  
		  Bitmap bitmap;
		  
		bitmap = ImageUtils.loadBitmapFromResource(_context, R.drawable.app3);
	    this._reflected_image_item3.setRecommendImageBitmap(ImageUtils.createReflectedImage(bitmap));
	  
		bitmap = ImageUtils.loadBitmapFromResource(_context, R.drawable.app5);
	    this._reflected_image_item5.setRecommendImageBitmap(ImageUtils.createReflectedImage(bitmap));

		bitmap = ImageUtils.loadBitmapFromResource(_context, R.drawable.app7);
	    this._reflected_image_item7.setRecommendImageBitmap(ImageUtils.createReflectedImage(bitmap));

		bitmap = ImageUtils.loadBitmapFromResource(_context, R.drawable.app9);
	    this._reflected_image_item9.setRecommendImageBitmap(ImageUtils.createReflectedImage(bitmap));
	  }
	  
}
