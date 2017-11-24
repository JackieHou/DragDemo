package com.jackiehou.dragdemo.widget1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.zhy.android.percent.support.PercentRelativeLayout;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/13 17:44
 ************************************************************/

public class CircleLayout1 extends PercentRelativeLayout {

    public static final String TAG = CircleLayout1.class.getSimpleName();

    DragHelper1 dragHelper1;

    public CircleLayout1(Context context) {
        super(context);
        init(context);
    }

    public CircleLayout1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleLayout1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        //final ViewConfiguration vc = ViewConfiguration.get(context);
        //touchSlop = vc.getScaledTouchSlop();
        dragHelper1 = new DragHelper1(this, ViewConfiguration.get(context));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int radius = Math.max(getMeasuredWidth(), getMeasuredHeight());
        dragHelper1.setCenter(radius/2,radius/2);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return dragHelper1.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        return dragHelper1.onTouchEvent(e);
    }


    public boolean getDragging(){
        return dragHelper1.getDragging();
    }



}
