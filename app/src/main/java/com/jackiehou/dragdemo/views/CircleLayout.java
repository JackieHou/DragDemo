package com.jackiehou.dragdemo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ScrollView;

import com.jackiehou.dragdemo.R;


/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/15 17:31
 ************************************************************/

public class CircleLayout extends ViewGroup implements GestureDetector.OnGestureListener {

    public static final String TAG = CircleLayout.class.getSimpleName();

    private final int mTouchSlop;
    private final int mMinimumVelocity;
    private final int mMaximumVelocity;
    private double mMinimumCornerVelocity;

    private int mRadius = 250;
    private float mPercent = 0.8f;
    private int mMaxWidth = Integer.MAX_VALUE;
    private int mMaxHeight = Integer.MAX_VALUE;
    private GestureDetectorCompat mDetector;

    private int mCenterX;
    private int mCenterY;
    private double mChangeCorner = 0.0;
    private Pair<Float, Float> mStart;
    private boolean isCanScroll = true;
    private boolean isCenterCanRotaior = true;
    private boolean isDragging = false;
    private FlingRunnable mFlingRunnable;

    private View mCenterView;

    private float lastX;
    private float lastY;
    private boolean isFling;

    private Pair<Float, Float> beforeFling;

    ScrollView scrollView;


    public CircleLayout(Context context) {
        this(context, null);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDetector = new GestureDetectorCompat(context, this);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CircleLayout, defStyleAttr, defStyleAttr);
        if (attrs != null) {
            try {
                mRadius = (int) a.getDimension(R.styleable.CircleLayout_radium, 250);
                mChangeCorner = (double) a.getFloat(R.styleable.CircleLayout_changeCorner, 90);
                mPercent = a.getFloat(R.styleable.CircleLayout_percent, 0.8f);
            } finally {
                a.recycle();
            }

        }
        Log.w(TAG, "CircleLayout mChangeCorner = " + mChangeCorner);

    }

    @Override
    protected void onFinishInflate() {
        //Log.w(TAG, "CircleLayout onFinishInflate before" );
        super.onFinishInflate();
        Log.w(TAG, "CircleLayout onFinishInflate end ");
        mCenterView = findViewById(R.id.center_view);

    }

    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight()) / 2;
        ///dragHelper.setCenter(mRadius,mRadius);
        mRadius *= mPercent;
    }

/*    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();


        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        int ps = getPaddingStart();
        int pe = getPaddingEnd();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();

        setMeasuredDimension(widthSize, heightSize);


        int childMaxWidth = 0;
        int childMaxHeight = 0;
        View child;
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize - ps - pe, View.MeasureSpec.UNSPECIFIED)
                    , View.MeasureSpec.makeMeasureSpec(heightSize - pt - pb, View.MeasureSpec.UNSPECIFIED));

            childMaxWidth = Math.max(childMaxWidth, child.getMeasuredWidth());
            childMaxHeight = Math.max(childMaxHeight, child.getMeasuredHeight());
        }

        int width = resolveAdjustedSize(mRadius * 2 + childMaxWidth + ps + pe, mMaxWidth, widthMeasureSpec);
        int height = resolveAdjustedSize(mRadius * 2 + childMaxHeight + pt + pb, mMaxHeight, heightMeasureSpec);

        int finalWidthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.getMode(widthMeasureSpec));
        int finalHeightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.getMode(heightMeasureSpec));
        setMeasuredDimension(finalWidthSpec, finalHeightSpec);

    }*/

    private int resolveAdjustedSize(int desiredSize, int maxSize,
                                    int measureSpec) {
        int result = desiredSize;
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                /* Parent says we can be as big as we want. Just don't be larger
                   than max size imposed on ourselves.
                */
                result = Math.min(desiredSize, maxSize);
                break;
            case MeasureSpec.AT_MOST:
                // Parent says we can be as big as we want, up to specSize.
                // Don't be larger than specSize, and don't be larger than
                // the max size imposed on ourselves.
                result = Math.min(Math.min(desiredSize, specSize), maxSize);
                break;
            case MeasureSpec.EXACTLY:
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.w(TAG, "CircleLayout onLayout" );
        //int childCount = mCenterView == null ? getChildCount() : getChildCount() - 1;
        int childCount = getChildCount();
        int start = mCenterView == null ? 0:1;
        mCenterX = (getMeasuredWidth() - getPaddingStart() - getPaddingEnd()) / 2;
        mCenterY = (getMeasuredHeight() - getPaddingBottom() - getPaddingTop()) / 2;

        View child;
        int childWidth;
        int childHeight;
        double corner;

        for (int i = start; i < childCount; i++) {
            child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }
            corner = 360 / (childCount-start) * (i-start);

            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();


            int cX = (int) (mCenterX - mRadius * Math.cos(Math.toRadians(corner + mChangeCorner)));
            int cY = (int) (mCenterY - mRadius * Math.sin(Math.toRadians(corner + mChangeCorner)));

            child.layout(cX - childWidth / 2, cY - childHeight / 2, cX + childWidth / 2, cY + childHeight / 2);

        }
        if (mCenterView != null) {
            mCenterView.layout(mCenterX - mCenterView.getMeasuredWidth() / 2, mCenterY - mCenterView.getMeasuredHeight() / 2
                    , mCenterX + mCenterView.getMeasuredWidth() / 2, mCenterY + mCenterView.getMeasuredHeight() / 2);
            if(isCenterCanRotaior){
                mCenterView.setRotation((float) (mChangeCorner - 90));
            }

        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            isDragging = false;
            return false;
        }

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                mStart = null;
                isDragging = false;
                if (mFlingRunnable != null) {
                    mFlingRunnable.endFling();
                }
                if (isFling) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    return true;
                }
                if(scrollView != null){
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                isDragging = Math.sqrt(Math.pow((x - lastX), 2) + Math.pow((y - lastY), 2)) > mTouchSlop;
               /* if(isDragging && scrollView != null){
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }*/

                return isDragging;
                //break;
            default:
                break;


        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b  =  mDetector.onTouchEvent(event);
        Log.w(TAG,"onTouchEvent  b = "+b+",isDragging = "+isDragging);
        return (isCanScroll || isFling) && b;
    }

    @SuppressWarnings("unused")
    public int getRadius() {
        return mRadius;
    }

    @SuppressWarnings("unused")
    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
        requestLayout();
    }

    @SuppressWarnings("unused")
    public int getMaxWidth() {
        return mMaxWidth;
    }

    @SuppressWarnings("unused")
    public void setMaxWidth(int mMaxWidth) {
        this.mMaxWidth = mMaxWidth;
    }

    @SuppressWarnings("unused")
    public int getMaxHeight() {
        return mMaxHeight;
    }

    @SuppressWarnings("unused")
    public void setMaxHeight(int mMaxHeight) {
        this.mMaxHeight = mMaxHeight;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        if (mStart == null) {
            mStart = new Pair<>(e2.getX() - mCenterX, e2.getY() - mCenterY);
        }

        Pair<Float, Float> end = new Pair<>(e2.getX() - mCenterX, e2.getY() - mCenterY);//结束向量
        //角度
        Double changeCorner = Math.toDegrees(Math.acos((mStart.first * end.first + mStart.second * end.second) / (Math.sqrt(mStart.first * mStart.first +
                mStart.second * mStart.second) * Math.sqrt(end.first * end.first + end.second * end.second))));

        //方向 >0 为顺时针 <0 为逆时针
        double changeDirection = mStart.first * end.second - mStart.second * end.first;

        if (!changeCorner.isNaN()) {
            if (changeDirection > 0) {
                mChangeCorner = (mChangeCorner + changeCorner) % 360;
            } else if (changeDirection < 0) {
                mChangeCorner = (mChangeCorner - changeCorner) % 360;
            }
        }

        requestLayout();
        beforeFling = new Pair<>(mStart.first, mStart.second);
        mStart = end;
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null) {
            return false;
        }
        isFling = true;

        double v = Math.min(Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2)), mMaximumVelocity);

        double oe1 = Math.sqrt(Math.pow(beforeFling.first, 2) + Math.pow(beforeFling.second, 2));
        double oe2 = Math.sqrt(Math.pow(e2.getX() - mCenterX, 2) + Math.pow(e2.getY() - mCenterY, 2));
        double e1e2 = Math.sqrt(Math.pow(e2.getX() - e1.getX(), 2) + Math.pow(e2.getY() - e1.getY(), 2));

        double sin = Math.sqrt(Math.pow(1 - (Math.pow(oe2, 2) + Math.pow(e1e2, 2) - Math.pow(oe1, 2)) / (2 * oe2 * e1e2), 2));
        double vc = 180 * v * sin / (Math.PI * oe2);
        mMinimumCornerVelocity = 180 * mMinimumVelocity * sin / (Math.PI * oe2);

        Pair<Float, Float> end = new Pair<>(e2.getX() - mCenterX, e2.getY() - mCenterY);

        double flingDirection = beforeFling.first * end.second - beforeFling.second * end.first;

        if (mFlingRunnable != null) {
            removeCallbacks(mFlingRunnable);
        }

        post(mFlingRunnable = new FlingRunnable(flingDirection > 0 ? vc : -vc));
        return true;
    }

    public boolean getDragging() {
        return isDragging;
    }

    private class FlingRunnable implements Runnable {
        double v;//初始速度

        FlingRunnable(double v) {
            this.v = v;
        }

        @Override
        public void run() {
            if (Math.abs(v) >= mMinimumCornerVelocity) {
                // Keep the fling alive a little longer
                v /= 1.0666F;
                mChangeCorner = (mChangeCorner + v / 1000 * 16) % 360;
                postDelayed(this, 16);
                requestLayout();
            } else {
                endFling();
            }

        }

        private void endFling() {
            isFling = false;
            removeCallbacks(this);
        }
    }

    @SuppressWarnings("unused")
    public boolean isCanScroll() {
        return isCanScroll;
    }

    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }

    public void setCenterCanRotation(boolean canRotaior){
        isCenterCanRotaior = canRotaior;
    }


    @SuppressWarnings("unused")
    public boolean isDragging() {
        return isDragging;
    }

    public void setCenterView(@NonNull View view) {
        Log.w(TAG, "CircleLayout setCenterView ");
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

    public View getCenterView() {
        return mCenterView;
    }

    public double getChangeCorner() {
        return mChangeCorner;
    }

    public void setChangeCorner(double changeCorner) {
        this.mChangeCorner = mChangeCorner;
        invalidate();
    }
}
