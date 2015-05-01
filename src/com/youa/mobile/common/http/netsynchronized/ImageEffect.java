package com.youa.mobile.common.http.netsynchronized;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageEffect {
	public static Bitmap createCornorBitmap(Bitmap bmp, float roundCornor)
	{
		if(bmp == null)
			return null;
		if(roundCornor <= 0)
			return bmp;
		int w = bmp.getWidth();
		int h = bmp.getHeight();	
		if(w<=0 || h<=0)
			return null;
		Bitmap rounder = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(rounder);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		if(bmp.getWidth()>0)
			roundCornor = bmp.getWidth()*roundCornor/96;
		final float roundPx = roundCornor;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bmp, rect, rect, paint);
		return rounder;
	}

	public static Bitmap createMaskBitmap(Bitmap bmp, float rate)
	{
		if(bmp == null)
			return null;
		if(rate>=1)
			rate = 1f;
		int w = bmp.getWidth();
		int h = bmp.getHeight();	
		if(w<=0 || h<=0)
			return null;
		Bitmap masked = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(masked);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		int dividerH = (int) (h*(1-rate));
		final Rect rect_below = new Rect(0, dividerH+1, w, h);
		final RectF rectF_below = new RectF(rect_below);

		
		final Rect rect_up = new Rect(0, 0, w, dividerH);
		final RectF rectF_up = new RectF(rect_up);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawBitmap(bmp,  0, 0, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		paint.setARGB(255, 255, 255, 255);
		canvas.drawRect(rectF_below, paint);
		paint.setARGB(200, 40, 40, 40);
		canvas.drawRect(rectF_up, paint);
		return masked;
	}

}
