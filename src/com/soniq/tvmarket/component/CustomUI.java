package com.soniq.tvmarket.component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soniq.tvmarket.R;
import com.soniq.tvmarket.ui.MainActivity;
import com.soniq.tvmarket.utils.ImageUtils;
import com.soniq.tvmarket.utils.MyUtils;

public class CustomUI {
	
	/**
	 * 得到自定义的progressDialog
	 * @param context
	 * @param msg
	 * @return
	 */
//	public static Dialog createLoadingDialog(Context context, String msg) {
//		LayoutInflater inflater = LayoutInflater.from(context);
//		View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
//		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
//		// main.xml中的ImageView
//		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
//		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
//		// 加载动画
//		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//				context, R.anim.loading_animation);
//		// 使用ImageView显示动画
//		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
//		tipTextView.setText(msg);// 设置加载信息
//
//		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
//
//		loadingDialog.setCancelable(false);// 不可以用“返回键”取消
//		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.MATCH_PARENT,
//				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
//		return loadingDialog;
//
//	}
	
	public static void showAlertDialog(Context context, String title, String message)
	{
		Dialog dlg = new AlertDialog.Builder(context)
							.setTitle(title)
							.setMessage(message)
							.setPositiveButton("确定", null)
							.create();
		dlg.show();
	}
	
	
	
	public static Dialog createLoadingDialog(Context context, String message)
	{
		final Dialog dlg = new Dialog(context, R.style.dialog);
		dlg.setCancelable(false);
		View v = LayoutInflater.from(context).inflate(R.layout.dialog_tips, null);
		
		
		ImageView imgView = (ImageView)v.findViewById(R.id.imageViewLoading);
		imgView.setVisibility(View.VISIBLE);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
		context, R.anim.loading_animation);
// 使用ImageView显示动画
		imgView.startAnimation(hyperspaceJumpAnimation);
		
		TextView textView = (TextView)v.findViewById(R.id.textView);
		
		textView.setText(message);
		
		
		int w = MyUtils.dip2px(context,500);
		int h = MyUtils.dip2px(context, 140);
		
		dlg.setContentView(v,
				new ViewGroup.LayoutParams(w, h));
		
		return dlg;
	}
	
	public static Dialog createFeedbackDialog(Context context, String message)
	{
		final Dialog dlg = new Dialog(context, R.style.dialog);
		dlg.setCancelable(false);
		View v = LayoutInflater.from(context).inflate(R.layout.dialog_tips, null);
		
		
		ImageView imgView = (ImageView)v.findViewById(R.id.imageViewLoading);
		imgView.setVisibility(View.VISIBLE);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
		context, R.anim.loading_animation);
// 使用ImageView显示动画
		imgView.startAnimation(hyperspaceJumpAnimation);
		
		TextView textView = (TextView)v.findViewById(R.id.textView);
		
		textView.setText(message);
		
		
		int w = MyUtils.dip2px(context,550);
		int h = MyUtils.dip2px(context, 140);
		
		dlg.setContentView(v,
				new ViewGroup.LayoutParams(w, h));
		
		return dlg;
	}
    	
	public static void showTipsDialog(Context context, String message)
	{
		showTipsDialog(context, message, 2000);
	}
	
	public static void showTipsDialog(Context context, String message, long duration)
	{
		final Dialog dlg = new Dialog(context, R.style.dialog);
		View v = LayoutInflater.from(context).inflate(R.layout.dialog_tips, null);
		
		
		TextView textView = (TextView)v.findViewById(R.id.textView);
		
		textView.setText(message);
		
		
		int w = MyUtils.dip2px(context,550);
		int h = MyUtils.dip2px(context, 140);
		
		dlg.setContentView(v,
				new ViewGroup.LayoutParams(w, h));
		
		
		Runnable runner = new Runnable() { 
            public void run() { 
                dlg.dismiss(); 
            } 
        };
        
        Executors.newSingleThreadScheduledExecutor().schedule(runner, duration, TimeUnit.MILLISECONDS);
        
        dlg.show();
		
		return;
	}

	public static void showFeedbackTipsDialog(Context context, String message)
	{
		showTipsDialog(context, message, 2000);
	}
	
	public static void showFeedbackTipsDialog(Context context, String message, long duration)
	{
		final Dialog dlg = new Dialog(context, R.style.dialog);
		View v = LayoutInflater.from(context).inflate(R.layout.dialog_tips, null);
		
		
		TextView textView = (TextView)v.findViewById(R.id.textView);
		
		textView.setText(message);
		
		
		int w = MyUtils.dip2px(context,550);
		int h = MyUtils.dip2px(context, 140);
		
		dlg.setContentView(v,
				new ViewGroup.LayoutParams(w, h));
		
		
		Runnable runner = new Runnable() { 
            public void run() { 
                dlg.dismiss(); 
            } 
        };
        
        Executors.newSingleThreadScheduledExecutor().schedule(runner, duration, TimeUnit.MILLISECONDS);
        
        dlg.show();
		
		return;
	}
	
	public static void showHelpDialog(Context context, String message)
	{
		final Dialog dlg = new Dialog(context, R.style.dialog);
		View v = LayoutInflater.from(context).inflate(R.layout.dialog_tips, null);
		
		
		TextView textView = (TextView)v.findViewById(R.id.textView);
		
		textView.setText(message);
		
		
		int w = MyUtils.dip2px(context,680);
		int h = MyUtils.dip2px(context, 140);
		
		dlg.setContentView(v,
				new ViewGroup.LayoutParams(w, h));
		
        
        dlg.show();
		
		return;
	}
	
	public static void showAboutDialog(Context context, String message1,String message2,String message3)
	{
		final Dialog dlg = new Dialog(context, R.style.dialog);
		View v = LayoutInflater.from(context).inflate(R.layout.dialog_about, null);
		LinearLayout ll = (LinearLayout) v.findViewById(R.id.dialog_about_ll);
		ll.setOrientation(0);
		
		ImageView image = (ImageView) v.findViewById(R.id.about_image);
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.about_ic_launcher);
		
		bitmap = ImageUtils.getRoundedCornerBitmap(bitmap, 15);
		image.setImageBitmap(bitmap);
		
		TextView textView1 = (TextView)v.findViewById(R.id.about_text1);
		textView1.setText(message1);
		
        TextView textView2 = (TextView)v.findViewById(R.id.about_text2);
		textView2.setText(message2);
		
		TextView textView3 = (TextView)v.findViewById(R.id.about_text3);
		textView3.setText(message3);
		
		int w = MyUtils.dip2px(context,500);
		int h = MyUtils.dip2px(context, 300);
		
		dlg.setContentView(v,
				new ViewGroup.LayoutParams(w, h));
		
				        
        dlg.show();
		
		return;
	}
}
