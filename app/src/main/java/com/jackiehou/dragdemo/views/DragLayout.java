package com.jackiehou.dragdemo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zhy.android.percent.support.PercentRelativeLayout;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/16 15:24
 ************************************************************/

public class DragLayout extends PercentRelativeLayout {

    DragHelper dragHelper;

    public DragLayout(Context context) {
        super(context);
        init(context);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
    }

    public void setDragHelper(DragHelper dragHelper) {
        this.dragHelper = dragHelper;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(dragHelper != null){
           dragHelper.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(dragHelper != null){
            return dragHelper.onInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(dragHelper != null){
            return dragHelper.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }
}
