package com.jackiehou.dragdemo.widget1;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/23 17:18
 ************************************************************/

public class NaviBarLayout extends RelativeLayout {
    public NaviBarLayout(Context context) {
        super(context);
    }

    public NaviBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NaviBarLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = (int) (getMeasuredWidth()*0.176f);
        /*int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);*/

        setMeasuredDimension(width,height);
    }
}
