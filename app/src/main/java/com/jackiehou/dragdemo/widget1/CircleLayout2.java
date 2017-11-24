package com.jackiehou.dragdemo.widget1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/15 15:01
 ************************************************************/

public class CircleLayout2 extends ViewGroup {

    public static final String TAG = CircleLayout2.class.getSimpleName();

    DragHelper1 dragHelper1;

    private int mMaxWidth = Integer.MAX_VALUE;
    private int mMaxHeight = Integer.MAX_VALUE;

    private View mCenterView;

    private double mChangeCorner = 0.0;

    private int mCenterX;
    private int mCenterY;

    private int mRadius = 250;

    public CircleLayout2(Context context) {
        this(context, null);
    }

    public CircleLayout2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        dragHelper1 = new DragHelper1(this, ViewConfiguration.get(context));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight())/2;
        dragHelper1.setCenter(mRadius,mRadius);
        mRadius *= 0.8;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = mCenterView == null ? getChildCount() : getChildCount() - 1;
        mCenterX = (getMeasuredWidth() - getPaddingStart() - getPaddingEnd()) / 2;
        mCenterY = (getMeasuredHeight() - getPaddingBottom() - getPaddingTop()) / 2;



        View child;
        int childWidth;
        int childHeight;
        double corner;

        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }
            corner = 360 / childCount * i;

            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();


            int cX = (int) (mCenterX - mRadius * Math.cos(Math.toRadians(corner + mChangeCorner)));
            int cY = (int) (mCenterY - mRadius * Math.sin(Math.toRadians(corner + mChangeCorner)));

            child.layout(cX - childWidth / 2, cY - childHeight / 2, cX + childWidth / 2, cY + childHeight / 2);

        }
        if (mCenterView != null) {
            mCenterView.layout(mCenterX - mCenterView.getMeasuredWidth() / 2, mCenterY - mCenterView.getMeasuredHeight() / 2
                    , mCenterX + mCenterView.getMeasuredWidth() / 2, mCenterY + mCenterView.getMeasuredHeight() / 2);
        }

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

    public void setCenterView(@NonNull View view) {
        if (mCenterView == null) {
            mCenterView = view;
            addView(mCenterView);
        }
        requestLayout();
    }

    public void removeCenterView() {
        if (mCenterView != null) {
            removeView(mCenterView);
            mCenterView = null;
        }
    }

    public View getCenterView(){
        return mCenterView;
    }

    public double getChangeCorner() {
        return mChangeCorner;
    }
}
