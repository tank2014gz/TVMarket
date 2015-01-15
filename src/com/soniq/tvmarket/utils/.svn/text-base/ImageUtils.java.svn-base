package com.soniq.tvmarket.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageUtils {


    public static Bitmap addReflectedImage(Bitmap originalImage)
    {
         int width = originalImage.getWidth();
         int height = originalImage.getHeight();
         Matrix matrix = new Matrix();
         // 实现图片翻转90度
         matrix.preScale(1, -1);
         // 创建倒影图片（是原始图片的一半大小）
         Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);
         // 创建总图片（原图片 + 倒影图片）
         Bitmap finalReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);
         // 创建画布
         Canvas canvas = new Canvas(finalReflection);
         canvas.drawBitmap(originalImage, 0, 0, null);
         //把倒影图片画到画布上
         canvas.drawBitmap(reflectionImage, 0, height + 1, null);
         Paint shaderPaint = new Paint();
         //创建线性渐变LinearGradient对象
         LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, finalReflection.getHeight() + 1, 0x70ffffff,
                   0x00ffffff, TileMode.MIRROR);
         shaderPaint.setShader(shader);
         shaderPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
         //画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果。
         canvas.drawRect(0, height + 1, width, finalReflection.getHeight(), shaderPaint);
         return finalReflection;
    }	
	
    public static Bitmap createReflectedImage(Bitmap originalImage)
    {
    	return createReflectedImage(originalImage, 2);
    }
    public static Bitmap createReflectedImage(Bitmap originalImage, int scaleY)
    {
         int width = originalImage.getWidth();
         int height = originalImage.getHeight();
         Matrix matrix = new Matrix();
         // 实现图片翻转90度
         matrix.preScale(1, -1);
         
         // 创建倒影图片（是原始图片的一半大小）
         Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height - height / scaleY, width, height / scaleY, 
        		 matrix, false);

         // 创建总图片（倒影图片）
         Bitmap finalReflection = Bitmap.createBitmap(width, height / scaleY, Config.ARGB_8888);
         // 创建画布
         Canvas canvas = new Canvas(finalReflection);

         //把倒影图片画到画布上
         canvas.drawBitmap(reflectionImage, 0, 0, null);
         
         Paint shaderPaint = new Paint();
         //创建线性渐变LinearGradient对象
         LinearGradient shader = new LinearGradient(0, 
        		 0, 
        		 0, finalReflection.getHeight() + 1, 
        		 0x50ffffff,
                   0x00ffffff, 
                   TileMode.MIRROR);
         shaderPaint.setShader(shader);
         shaderPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
         //画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果。
         canvas.drawRect(0, 0, width, finalReflection.getHeight(), shaderPaint);
         return finalReflection;
    }
    
    
    public static Bitmap loadBitmapFromResource(Context context, int resId)
    {
    	try{
    		 BitmapFactory.Options opt = new BitmapFactory.Options();
    		 opt.inPreferredConfig = Bitmap.Config.RGB_565;
//    		 opt.inSampleSize = 2;
    		 opt.inPurgeable = true; 
    		 opt.inInputShareable = true;
    		 
    		 InputStream is = context.getResources().openRawResource(resId);
    		 
    		 return BitmapFactory.decodeStream(is);
    	}
    	catch(Exception e)
    	{
    		
    	}
    	
    	return  null;
    	
//    	try{
//		Bitmap  bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
//		return bitmap;
//    	}
//    	catch(Exception e)
//    	{
//    		
//    	}
//    	
//    	return null;
    }
    
    public static Drawable loadDrawableFromResource(Context context, int resId)
    {
    	Bitmap bmp = loadBitmapFromResource(context, resId);
    	if( bmp != null )
    	{
    		BitmapDrawable bd= new BitmapDrawable(context.getResources(), bmp);
    		return bd;
    	}
    	
    	return null;
    }
    
    
  //获得圆角图片的方法
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx)
    {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
	    .getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	     
	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	     
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	     
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	     
	    return output;
    } 
    
    
	public static Bitmap loadBitmapFromFile(String filename)
	{
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(filename);
			
			ByteArrayOutputStream outStream=new ByteArrayOutputStream();
	        byte[] buffer=new byte[1024];
	        int len = 0;
	        while ((len=fis.read(buffer))!=-1){
	            outStream.write(buffer, 0, len);
	        }
	        outStream.close();
	        
	        byte[] data=outStream.toByteArray();
	        
	        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//	        Drawable frame = new BitmapDrawable(context.getResources(), bmp);	        
	        
	        return bmp;			
			
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			if( fis != null )
			{
				try{
					fis.close();
				}
				catch(Exception e)
				{
					
				}
			}
		}
		
		return null;
	}	
	    
}
