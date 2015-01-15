package com.soniq.tvmarket.ui.widget;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.soniq.tvmarket.R;
import com.soniq.tvmarket.component.CustomViewPager;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.RecommendInfo;
import com.soniq.tvmarket.utils.ImageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class GalleryMetroItemView extends MetroItemView
{
  private CustomViewPager _viewPager = null;
  private Context _context = null;
  private TextView _titleTextView = null;
  
  private MetroItemView _reflectedView = null;
  private int _reflectedScaleY = 2;

  private List<RecommendInfo> _recommendList = new ArrayList<RecommendInfo>();
  
  private List<View> _viewCache = new ArrayList<View>();
  
  
  private int _currentIndex = 0;   						    
  private int _step = 1;
  private Timer _timer = null;
  
  
  public GalleryMetroItemView(Context paramContext)
  {
    super(paramContext);
    this._context = paramContext;
    init();
  }

  public GalleryMetroItemView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this._context = paramContext;
    init();
  }

  public GalleryMetroItemView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this._context = paramContext;
    init();
  }

  private void init()
  {
    addView(((LayoutInflater)this._context.getSystemService("layout_inflater")).
    		inflate(R.layout.view_gallery_metro_item, null));
    
    this._viewPager = ((CustomViewPager)findViewById(R.id.viewpager));
//    this._viewPager.setBackgroundColor(Color.RED);
    this._viewPager.setSaveEnabled(false);
    
    
    changeViewPageScroller();
    
    this._titleTextView = ((TextView)findViewById(R.id.item_text));
  }

  public void onImageButtonFocusChanged(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this._titleTextView.setTextColor(this._context.getResources().getColor(R.color.album_text_focus));
    }
    else
    {
    	this._titleTextView.setTextColor(this._context.getResources().getColor(R.color.album_text_normal));
    }
  }
  
  public void setReflectedView(MetroItemView imgView, int scaleY)
  {
	  _reflectedView = imgView;
	  _reflectedScaleY = scaleY;
  }
  
  public void setRecommendImageRes(int paramInt)
  {
    this._viewPager.setBackgroundResource(paramInt);
  }
  
  public void setRecommendList(List<RecommendInfo> recommendList)
  {
	  for(int i = 0; i < recommendList.size(); i++ )
	  {
		  RecommendInfo ri = recommendList.get(i);
		  
		  Bitmap bmp = ri.getLocalImageBitmap(_context);
		  if( bmp == null )
			  continue;

		  ImageView imgView = new ImageView(_context);
		  imgView.setScaleType(ScaleType.FIT_XY);
//		  imgView.setImageResource(ri.resId);
		  imgView.setImageBitmap(bmp);
		  
		  _viewCache.add(imgView);
		  
		  
		  _recommendList.add(ri);
	  }
	  
	  
	  _viewPager.setAdapter(new MyAdapter());
	  if( _recommendList.size() > 0 )
		  setCurrentItem(0);
		
		if( recommendList.size() > 1 )
			this.startAutoSlide();
  }
  
  public int getItemCount()
  {
	  return _recommendList.size();
  }
  
  public int getCurrentItemIndex()
  {
	  return _viewPager.getCurrentItem();
  }
  
  private void setCurrentItem(int imgIndex)
  {
	  _viewPager.setCurrentItem(imgIndex, false);
	  
	  if( _reflectedView != null )
	  {
		  RecommendInfo ri = _recommendList.get(imgIndex);
		  
		  Bitmap bmp = ri.getLocalImageBitmap(_context);
		  if( bmp != null )
		  {
			  _reflectedView.setRecommendImageBitmap(ImageUtils.createReflectedImage(bmp, _reflectedScaleY));
		  }
		  
	  }
	  
  }


  public void setRecommendTitleText(String paramString)
  {
    this._titleTextView.setText(paramString);
    setRecommendTitleVisibility(true);
  }

  public void setRecommendTitleVisibility(boolean visibility)
  {
    if (visibility)
    {
    	this._titleTextView.setVisibility(View.VISIBLE);
    }
    else
    {
    	this._titleTextView.setVisibility(View.INVISIBLE);
    }
  }
  
  public void onScaleFinished(boolean b)
  {
	  for(int i = 0;i < _viewCache.size(); i++ )
	  {
		  View v = _viewCache.get(i);
//		  v.requestLayout();
		  v.invalidate();
	  }
	  
	  _viewPager.invalidate();
//	  _viewPager.requestLayout();
  } 
  
  
  private class MyAdapter extends PagerAdapter
  {

	@Override
	public int getCount() {
		return _viewCache.size();
	}
	
	@Override
    public Object instantiateItem(ViewGroup container, int position)
    {
	      View v = GalleryMetroItemView.this._viewCache.get(position);
	      if( v.getParent() != null )
	      {
	    	  ((ViewGroup)v.getParent()).removeView(v);
	      }
	      
	      container.addView(v, 0);

	      return v;
    }
	
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
    	((ViewPager)container).removeView((View)GalleryMetroItemView.this._viewCache.get(position));
    }
	

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;//false;
	}
	  
  }
  
  
  
  private void changeViewPageScroller()
  {
    try
    {
      Field localField = ViewPager.class.getDeclaredField("mScroller");
      localField.setAccessible(true);
      FixedSpeedScroller localFixedSpeedScroller = 
    		  new FixedSpeedScroller(_context, new AccelerateDecelerateInterpolator());
      localField.set(this._viewPager, localFixedSpeedScroller);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }		

private class FixedSpeedScroller extends Scroller
  {
    private int mDuration = 500;

    public FixedSpeedScroller(Context arg2)
    {
      super(arg2);
    }

    public FixedSpeedScroller(Context paramInterpolator, Interpolator arg3)
    {
      super(paramInterpolator, arg3);
    }

    public int getmDuration()
    {
    	return this.mDuration;
    }

    public void setmDuration(int paramInt)
    {
      this.mDuration = paramInt;
    }

    public void startScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.startScroll(paramInt1, paramInt2, paramInt3, paramInt4, this.mDuration);
    }

    public void startScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      super.startScroll(paramInt1, paramInt2, paramInt3, paramInt4, this.mDuration);
    }
  }	  
  
  
  
  public void startAutoSlide()
  {
		TimerTask timerTask = new TimerTask() { 

            @Override 
            public void run() { 
            	
//            	Log.v(AppConfig.TAG, "timer....");

            	// 定义一个消息传过去  
                Message msg = new Message(); 
                msg.what = 1; 
                handler.sendMessage(msg); 
            } 
        }; 		
        
		// Timer在cancel之后不能再次schedule
		// 所以每次都new一个新的
        _timer = new Timer();
		_timer.schedule(timerTask, 5000, 5000);
	  
  }
  
	private void stopAutoSlide()
	{
		if( _timer != null )
		{
			_timer.cancel();
		}
		
	}
	  
  
  
  Handler handler = new Handler() { 
		 
      @Override 
      public void handleMessage(Message msg) { 
          super.handleMessage(msg); 
//
//          Log.d("debug", "handleMessage方法所在的线程：" 
//                  + Thread.currentThread().getName()); 
//
          // Handler处理消息  
          if (msg.what == 1 ) 
          { 
          	int cnt = _viewCache.size();
          	
          	int nextScreen = _currentIndex;
          	
          	if( _currentIndex == cnt - 1 )
          	{
          		_step = -1;
          	}
          	else if( _currentIndex == 0 )
          		_step = 1;
          	
          	
          	nextScreen += _step;
          	
          	setCurrentItem(_currentIndex);
          	
          	_currentIndex = nextScreen;
          } 
      } 
  }; 
}