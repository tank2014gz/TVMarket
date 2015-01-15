package com.soniq.tvmarket.ui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.soniq.tvmarket.R;
import com.soniq.tvmarket.data.AppConfig;
import com.soniq.tvmarket.data.RecommendInfo;
import com.soniq.tvmarket.ui.MainActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AutoSlideMetroItemView extends MetroItemView
{
	private ImageView _imgView1 = null;
	private ImageView _imgView2 = null;
	
	private Context _context = null;
	private TextView _titleTextView = null;

  private List<RecommendInfo> _recommendList = null;
  
  
  private int mCurScreen = 0;   						    
  private int _step = 1;
  private Timer _timer = null;
  
  public AutoSlideMetroItemView(Context paramContext)
  {
    super(paramContext);
    this._context = paramContext;
    init();
  }

  public AutoSlideMetroItemView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this._context = paramContext;
    init();
  }

  public AutoSlideMetroItemView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this._context = paramContext;
    init();
  }

  private void init()
  {
    addView(((LayoutInflater)this._context.getSystemService("layout_inflater")).
    		inflate(R.layout.view_auto_slide_metro_item, null));
    this._imgView1 = ((ImageView)findViewById(R.id.item_image));
    
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
  
  public void setRecommendList(List<RecommendInfo> recommendList)
  {
	  _recommendList = recommendList;
	  
	  if( recommendList == null || recommendList.size() == 0 )
		  return;
	  
	  
	  RecommendInfo ri = _recommendList.get(0);
	  
	  _imgView1.setImageResource(ri.resId);
	  
	
	  startAutoSlide();	  
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
  
  public void startAutoSlide()
  {
		TimerTask timerTask = new TimerTask() { 

            @Override 
            public void run() { 
            	
            	Log.v(AppConfig.TAG, "timer....");

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
          	int cnt = _recommendList.size();
          	
          	int nextScreen = mCurScreen;
          	
          	if( mCurScreen == cnt - 1 )
          	{
          		_step = -1;
          	}
          	else if( mCurScreen == 0 )
          		_step = 1;
          	
          	
          	nextScreen += _step;
          	
          	RecommendInfo ri = _recommendList.get(nextScreen);
          	_imgView1.setImageResource(ri.resId);
          	AutoSlideMetroItemView.this.requestLayout();
          	AutoSlideMetroItemView.this.invalidate();
          	
          	mCurScreen = nextScreen;
          } 
      } 
  };   
}