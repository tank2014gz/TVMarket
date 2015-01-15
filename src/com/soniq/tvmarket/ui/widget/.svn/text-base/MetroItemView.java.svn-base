package com.soniq.tvmarket.ui.widget;

import com.soniq.tvmarket.R;
import com.soniq.tvmarket.utils.ImageUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class MetroItemView extends FrameLayout
{
  private ImageView _imageView = null;
  private Context mContext = null;
  private ImageView _highLightImageView = null;
  private TextView _titleTextView = null;
  
  
  public Rect originRect = null;

  public MetroItemView(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    init();
  }

  public MetroItemView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    init();
  }

  public MetroItemView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mContext = paramContext;
    init();
  }

  private void init()
  {
    addView(((LayoutInflater)this.mContext.getSystemService("layout_inflater")).
    		inflate(R.layout.view_metro_item, null));
    this._imageView = ((ImageView)findViewById(R.id.item_image));
    
    this._titleTextView = ((TextView)findViewById(R.id.item_text));
    
    this._highLightImageView = (ImageView)findViewById(R.id.item_highlight_image);
  }
  
  public void onScaleFinished(boolean b)
  {
	  
  }

  public void onImageButtonFocusChanged(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this._titleTextView.setTextColor(this.mContext.getResources().getColor(R.color.album_text_focus));
    }
    else
    {
    	this._titleTextView.setTextColor(this.mContext.getResources().getColor(R.color.album_text_normal));
    }
  }

  public void setHighLightImageRes(int paramInt)
  {
  }

  public void setRecommendImageBitmap(Bitmap paramBitmap)
  {
    this._imageView.setImageBitmap(paramBitmap);
  }
  

  public void setRecommendImageRes(int resId)
  {
	  this._imageView.setImageResource(resId);
  }
  

  public void playAnimation()
  {
	  AnimationDrawable ad = (AnimationDrawable)this._imageView.getDrawable();
	  if( ad != null )
	  {
		  ad.stop();
		  ad.start();
	  }
  }
  

  public void setRecommendTitleText(String text)
  {
	  this._titleTextView.setText(text);
//	  if( text != null && text.length() > 0 )
//		  setRecommendTitleVisibility(true);
//	  else
//		  setRecommendTitleVisibility(false);
  }

  public void setRecommendTitleVisibility(boolean visibility)
  {
		String s= _titleTextView.getText().toString();
		if( s.length() > 0 )
		{
			if (visibility)
			{
				this._titleTextView.setVisibility(View.VISIBLE);
				this._highLightImageView.setVisibility(View.VISIBLE);
			}
			else
			{
				this._titleTextView.setVisibility(View.INVISIBLE);
				this._highLightImageView.setVisibility(View.INVISIBLE);
			}
		}
		else
		{
			this._titleTextView.setVisibility(View.INVISIBLE);
			this._highLightImageView.setVisibility(View.INVISIBLE);
		}
  }
			
}