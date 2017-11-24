package com.jackiehou.dragdemo.widget1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.nelson.circlelayout.CircleLayout;


/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/13 17:44
 ************************************************************/

public class MyCircleLayout extends CircleLayout {

    public static final String TAG = MyCircleLayout.class.getSimpleName();


    //DragHelper1 dragHelper1;

    RotateHelper rotateHelper;


    public MyCircleLayout(Context context) {

        super(context);
        init(context);
    }

    public MyCircleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyCircleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        //final ViewConfiguration vc = ViewConfiguration.get(context);
        //touchSlop = vc.getScaledTouchSlop();
        rotateHelper = new RotateHelper(this, context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int radius = Math.max(getMeasuredWidth(), getMeasuredHeight());
        //setRadius(radius/2);
        rotateHelper.setCenter(radius/2,radius/2);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return rotateHelper.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        return rotateHelper.onTouchEvent(e);
    }


    public boolean getDragging(){
        return rotateHelper.getDragging();
    }
}
