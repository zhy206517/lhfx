package com.youa.mobile.login.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class CustomGallery extends Gallery {

    public CustomGallery(Context context, AttributeSet attrs) {
            super(context, attrs);
            // TODO Auto-generated constructor stub
           
    }
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		int kEvent;
		if (isScrollingLeft(e1, e2)) {
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(kEvent, null);
		return true;
		//return false;
    }
    @Override
    public void setBackgroundDrawable(Drawable d) {
    	super.setBackgroundDrawable(null);
    }

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}
    /*@Override
    public void offsetLeftAndRight(int offset) {
    	// TODO Auto-generated method stub
    	super.offsetLeftAndRight(offset);
    }*/
    
    /*@Override
    public boolean onSingleTapUp(MotionEvent e) {
    	// TODO Auto-generated method stub
    	return false;
    }*/
    
//    @Override
//    public void bringChildToFront(View child) {
//    	// TODO Auto-generated method stub
//    	//super.bringChildToFront(child);
//    }
//    
//    @Override
//    public void bringToFront() {
//    	// TODO Auto-generated method stub
//    	//super.bringToFront();
//    }
}