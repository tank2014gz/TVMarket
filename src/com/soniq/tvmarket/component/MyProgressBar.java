package com.soniq.tvmarket.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class MyProgressBar extends ProgressBar{
	String text;
	Paint mPaint;
	 
	public MyProgressBar(Context context) {
		super(context);
		initText(); 
	}
	 
	public MyProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initText();
	}
	 
	 
	public MyProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initText();
	}
	 
	@Override
	public synchronized void setProgress(int progress) {
		// TODO Auto-generated method stub
		setText(progress);
		super.setProgress(progress);
	}
	 
	@Override
	protected synchronized void onDraw(Canvas canvas) 
	{
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//this.setText();
		Rect rect = new Rect();
		this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
		int x = (getWidth() / 2) - rect.centerX(); 
		int y = (getHeight() / 2) - rect.centerY(); 
		
		int n = this.getProgress() * 100 / this.getMax();
		String s = String.format("%d%%", n);
		canvas.drawText(s, x, y, this.mPaint); 
	}
	 
	//初始化，画笔
	private void initText(){
		this.mPaint = new Paint();
		this.mPaint.setColor(Color.WHITE);
		mPaint.setTextSize(20);
	 
	}
	 
	private void setText(){
		setText(this.getProgress());
	}
	 
	//设置文字内容
	private void setText(int progress){
		int i = (progress * 100)/this.getMax();
		this.text = String.valueOf(i) + "%";
		this.invalidate();
	}
	 
 
}