package com.soniq.tvmarket.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class CustomOrderRelativeLayout extends RelativeLayout
{
  public static final String TAG = "CustomOrderRelativeLayout";

  public CustomOrderRelativeLayout(Context paramContext)
  {
    super(paramContext);
    init();
  }

  public CustomOrderRelativeLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }

  public CustomOrderRelativeLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init();
  }

  private void init()
  {
    super.setChildrenDrawingOrderEnabled(true);
  }

  
  /*
  //protected int getChildDrawingOrder(int childCount, int i)
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
	  String s = String.format("%d, %d", paramInt1, paramInt2);
	  
	  Log.v("alex", s);
	  
    View localView = getFocusedChild();
    int i = -1;
    for (int j = 0; ; j++)
    {
		  if (j < paramInt1)
		  {
		    if (getChildAt(j) == localView)
		      i = j;
		  }
		  else
		  {
		    if ((i >= 0) && (paramInt2 >= i))
		      break;
		    return paramInt2;
		  }
    }
    if (paramInt2 > i)
      return paramInt2 - 1;
    return paramInt1 - 1;
  }
  */
}