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

public class RecommendPage extends BasePage {

	  private MetroItemView _item1 = null;
	  private MetroItemView _item2 = null;
	  private MetroItemView _item3 = null;
	  private MetroItemView _item4 = null;
	  private MetroItemView _item5 = null;
	  
	  private MetroItemView _reflected_image_item2 = null;
	  private MetroItemView _reflected_image_item3 = null;
	  private MetroItemView _reflected_image_item5 = null;

	  private MetroItemView mLastLoseFocus = null;
	  
	  private List<MetroItemView> _buttonlList = null;
	
		
	  private RelativeLayout mContainer = null;
	  private Context _context = null;

	public RecommendPage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		_context = context;
		
		init();
	}
	
	
	@Override
	public void setCurrentFocusItemWithDefault()
	{
		this._item3.requestFocus();
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
			this._item5.requestFocus();
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
			
			
			((MetroItemView)v).setRecommendTitleVisibility(true);
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
			((MetroItemView)v).setRecommendTitleVisibility(false);
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
				}
				else
				{
					setViewFocusState(v, false);
					RecommendPage.this.mLastLoseFocus = (MetroItemView)v;
				}
				
				
				if( v.getId() == R.id.button_item2 )
				{
//					this._reflected_image_item2 = (MetroItemView)findViewById(R.id.reflected_image_button_item2);
					setViewFocusState(_reflected_image_item2, hasFocus);
				}
				else if( v.getId() == R.id.button_item3 )
				{
					setViewFocusState(_reflected_image_item3, hasFocus);
				}
				
				else if( v.getId() == R.id.button_item5 )
				{
					setViewFocusState(_reflected_image_item5, hasFocus);
				}
				
			}
			
			RecommendPage.this.mContainer.invalidate();
			
		}
	};
	
	  
	  private View.OnClickListener mOnClickListener = new View.OnClickListener()
	  {
	    public void onClick(View view)
	    {

	    	int num = 1;
	    	int cur = 0;
	    	String pos = "";
	    	// 广告点击
	    	switch(view.getId())
	    	{
	    	case R.id.button_item1:
	    		pos = RecommendInfo.AD_POS_HOME_LEFT_TOP;
	    		break;
	    	case R.id.button_item2:
	    		pos = RecommendInfo.AD_POS_HOME_LEFT_BOTTOM;
	    		break;
	    	case R.id.button_item3:
	    	{
	    		GalleryMetroItemView gmiv = (GalleryMetroItemView)_item3;
	    		num = gmiv.getItemCount();
	    		cur = gmiv.getCurrentItemIndex();
	    		pos = RecommendInfo.AD_POS_HOME_MIDDLE;
	    	}
	    		break;
	    	case R.id.button_item4:
	    		pos = RecommendInfo.AD_POS_HOME_RIGHT_TOP;
	    		break;
	    	case R.id.button_item5:
	    		pos = RecommendInfo.AD_POS_HOME_RIGHT_BOTTOM;
	    		break;
	    	}
	    	
	    	
	    	String s = String.format("num=" + num + " cur=" + cur);
	    	Log.v(AppConfig.TAG, s);
	    	
			List<RecommendInfo> list = new ArrayList<RecommendInfo>();
	    	int cnt = HomeDataLoader.getInstance(_context).getRecommendList(pos,list, num);
	    	
	    	RecommendInfo recommendInfo = list.get(cur);
	    	Log.v(AppConfig.TAG, recommendInfo.title);
	    	
	    	MainActivity.getInstance().OnAdClicked(recommendInfo);
	    	
	    	
	    }
	  };
	  
	  
	
	  
	  
	  
	  private void init()
	  {
		    addView(((LayoutInflater)this._context.getSystemService("layout_inflater")).
		    		inflate(R.layout.view_recommend, null));
		  
		    this.mContainer = ((RelativeLayout)findViewById(R.id.tab_container));
		    
//		    this.mContainer.setClipChildren(false);
		    
		    
		    this._item1 = ((MetroItemView)findViewById(R.id.button_item1));
		    this._item2 = ((MetroItemView)findViewById(R.id.button_item2));
		    this._item3 = ((MetroItemView)findViewById(R.id.button_item3));
		    this._item4 = ((MetroItemView)findViewById(R.id.button_item4));
		    this._item5 = ((MetroItemView)findViewById(R.id.button_item5));
		    
		    
			this._reflected_image_item2 = (MetroItemView)findViewById(R.id.reflected_image_button_item2);
			this._reflected_image_item5 = (MetroItemView)findViewById(R.id.reflected_image_button_item5);
			this._reflected_image_item3 = (MetroItemView)findViewById(R.id.reflected_image_button_item3);

			this._buttonlList = new ArrayList<MetroItemView>();
		    this._buttonlList.add(this._item1);
		    this._buttonlList.add(this._item2);
		    this._buttonlList.add(this._item3);
		    this._buttonlList.add(this._item4);
		    this._buttonlList.add(this._item5);
		    
		    this._item1.setOnFocusChangeListener(this.mFocusListener);
		    this._item2.setOnFocusChangeListener(this.mFocusListener);
		    this._item3.setOnFocusChangeListener(this.mFocusListener);
		    this._item4.setOnFocusChangeListener(this.mFocusListener);
		    this._item5.setOnFocusChangeListener(this.mFocusListener);

		    this._item1.setOnClickListener(this.mOnClickListener);
		    this._item2.setOnClickListener(this.mOnClickListener);
		    this._item3.setOnClickListener(this.mOnClickListener);
		    this._item4.setOnClickListener(this.mOnClickListener);
		    this._item5.setOnClickListener(this.mOnClickListener);
		    
		    initChildrenFocusID();
		    
		    initData();
	  }
	  
	  
	  private void initChildrenFocusID()
	  {
	    this._item1.setNextFocusLeftId(-1);
	    this._item1.setNextFocusUpId(-1);
	    this._item1.setNextFocusRightId(R.id.button_item3);
	    this._item1.setNextFocusDownId(R.id.button_item2);
	    
	    this._item2.setNextFocusLeftId(-1);
	    this._item2.setNextFocusUpId(R.id.button_item1);
	    this._item2.setNextFocusRightId(R.id.button_item3);
	    this._item2.setNextFocusDownId(-1);

	    this._item3.setNextFocusLeftId(R.id.button_item1);
	    this._item3.setNextFocusUpId(-1);
	    this._item3.setNextFocusRightId(R.id.button_item4);
	    this._item3.setNextFocusDownId(-1);

	    this._item4.setNextFocusLeftId(R.id.button_item3);
	    this._item4.setNextFocusUpId(R.id.btn_search);
	    this._item4.setNextFocusRightId(-1);
	    this._item4.setNextFocusDownId(R.id.button_item5);

	    this._item5.setNextFocusLeftId(R.id.button_item3);
	    this._item5.setNextFocusUpId(R.id.button_item4);
	    this._item5.setNextFocusRightId(-1);
	    this._item5.setNextFocusDownId(-1);

	  }

	  private void initData()
	  {
		  List<RecommendInfo> list = new ArrayList<RecommendInfo>();
		  
		  Bitmap bmp = null;
		  int cnt = HomeDataLoader.getInstance(_context).getRecommendList(RecommendInfo.AD_POS_HOME_LEFT_TOP,list, 1);
		  if( cnt > 0 )
		  {
			  bmp = list.get(0).getLocalImageBitmap(_context);
			  if( bmp != null )
			  {
				  this._item1.setRecommendImageBitmap(bmp);
				  this._item1.setRecommendTitleText(list.get(0).title);
			  }
		  }
		  if( bmp == null )
			  this._item1.setRecommendImageRes(R.drawable.ad_placeholder);
		  
		  
		  bmp = null;
		  list.clear();
		  cnt = HomeDataLoader.getInstance(_context).getRecommendList(RecommendInfo.AD_POS_HOME_LEFT_BOTTOM,list, 1);
		  if( cnt > 0 )
		  {
			  bmp = list.get(0).getLocalImageBitmap(_context);
			  if( bmp != null )
			  {
				  this._item2.setRecommendImageBitmap(bmp);
				  this._item2.setRecommendTitleText(list.get(0).title);
				  
				  this._reflected_image_item2.setRecommendImageBitmap(ImageUtils.createReflectedImage(bmp));
				  
			  }
		  }
		  if( bmp == null )
		  {
			  this._item2.setRecommendImageRes(R.drawable.ad_placeholder);
						  
		  }
		  
		  bmp = null;
		  list.clear();
		  cnt = HomeDataLoader.getInstance(_context).getRecommendList(RecommendInfo.AD_POS_HOME_RIGHT_TOP,list, 1);
		  if( cnt > 0 )
		  {
			  bmp = list.get(0).getLocalImageBitmap(_context);
			  if( bmp != null )
			  {
				  this._item4.setRecommendImageBitmap(bmp);
				  this._item4.setRecommendTitleText(list.get(0).title);
			  }
		  }
		  if( bmp == null )
			  this._item4.setRecommendImageRes(R.drawable.ad_placeholder);

		  bmp = null;
		  list.clear();
		  cnt = HomeDataLoader.getInstance(_context).getRecommendList(RecommendInfo.AD_POS_HOME_RIGHT_BOTTOM,list, 1);
		  if( cnt > 0 )
		  {
			  bmp = list.get(0).getLocalImageBitmap(_context);
			  if( bmp != null )
			  {
				  this._item5.setRecommendImageBitmap(bmp);
				  this._item5.setRecommendTitleText(list.get(0).title);
					this._reflected_image_item5.setRecommendImageBitmap(ImageUtils.createReflectedImage(bmp));
			  }
		  }
		  if( bmp == null )
			  this._item5.setRecommendImageRes(R.drawable.ad_placeholder);
		  
	  
		  GalleryMetroItemView gmiv = (GalleryMetroItemView)this._item3;
		  
		  gmiv.setReflectedView(_reflected_image_item3, 4);

		  list.clear();
		  HomeDataLoader.getInstance(_context).getRecommendList(RecommendInfo.AD_POS_HOME_MIDDLE,list, 3);
		  
		  gmiv.setRecommendList(list);
		  
		  if( list.size() == 0 )
		  {
			  gmiv.setRecommendImageRes(R.drawable.ad_placeholder);
		  }
	  }
	  
}
