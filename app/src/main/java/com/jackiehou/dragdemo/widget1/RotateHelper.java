package com.jackiehou.dragdemo.widget1;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/14 13:42
 ************************************************************/

public class RotateHelper implements GestureDetector.OnGestureListener{

    public static final String TAG = RotateHelper.class.getSimpleName();


    private final int mTouchSlop;
    private final int mMinimumVelocity;
    private final int mMaximumVelocity;
    private double mMinimumCornerVelocity;

    private GestureDetectorCompat mDetector;

    private float mCenterX;
    private float mCenterY;

    private double mChangeCorner = 0.0;
    private Pair<Float, Float> mStart;

    private boolean isDragging = false;
    private FlingRunnable mFlingRunnable;

    private Pair<Float, Float> beforeFling;

    private View targetView;
    //速度计算
    VelocityTracker mVelocityTracker;

    //当前旋转的度数
    double currAngle;

    private float lastX;
    private float lastY;
    private boolean isFling;

    private volatile boolean mDragging;

    ObjectAnimator mFlingAnim;

    public RotateHelper(View targetView,Context context) {
        this(targetView,context,0,0);
    }

    public RotateHelper(View targetView, Context context, float centerX,float centerY) {
        this.targetView = targetView;

        mDetector = new GestureDetectorCompat(context,this);
        mVelocityTracker = VelocityTracker.obtain();
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.mCenterX = centerX;
        this.mCenterY = centerY;
        currAngle = targetView.getRotation();
    }

    public void setCenter(float x,float y) {
        this.mCenterX = x;
        this.mCenterY = y;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            return false;
        }

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();

                mStart = null;
                if (mFlingRunnable != null) {
                    mFlingRunnable.endFling();
                }
                if (isFling) {
                    ViewParent parent = targetView.getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                isDragging = Math.sqrt(Math.pow((x - lastX), 2) + Math.pow((y - lastY), 2)) > mTouchSlop;
                return isDragging;


        }
        return isDragging;
    }

/*    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Objects.requireNonNull(center);

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDragging = false;
                mXPos = ev.getX();
                mYPos = ev.getY();
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(ev);
                if(mFlingAnim != null){
                    mFlingAnim.cancel();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG,"onInterceptTouchEvent MOVE");
                mVelocityTracker.addMovement(ev);
                if(mDragging){
                    return true;
                }
                float x = ev.getX();
                float y = ev.getY();
                mDragging = Math.sqrt(Math.pow((x - mXPos), 2) + Math.pow((y - mYPos), 2)) > mTouchSlop;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                break;
            default:
                break;
        }
        return mDragging;
    }*/

    public boolean onTouchEvent(MotionEvent e) {
        if(mFlingAnim != null && mFlingAnim.isRunning()){
            return false;
        }
        return mDetector.onTouchEvent(e);
    }



    private void endDrag(VelocityTracker velocityTracker,MotionEvent e) {
        /*velocityTracker.computeCurrentVelocity(1000,10000);
        //平均速度
        float velocity = getVelocity(velocityTracker,e)/2;
        float angle = 0;
        //float lastMoveAngle = mTmpAngle;
        //updateTmpAngle(e.getX(),e.getY());
        if(order){
            angle  = velocity * 0.0225f+mTmpAngle;
        }else{
            angle  = -velocity * 0.0225f+mTmpAngle;
        }
        Log.i(TAG,"endDrag angle = "+angle+",velocity="+velocity+",mTmpAngle="+mTmpAngle+",order="+order);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(targetView,View.ROTATION,angle);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(SNAP_ANIM_LEN);
        objectAnimator.start();*/
    }



    public boolean getDragging(){
        return mDragging;
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

        targetView.setRotation((float) mChangeCorner);
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
        Log.i(TAG,"onFling vc ="+vc+"v ="+v);
        mMinimumCornerVelocity = 180 * mMinimumVelocity * sin / (Math.PI * oe2);

        Pair<Float, Float> end = new Pair<>(e2.getX() - mCenterX, e2.getY() - mCenterY);

        double flingDirection = beforeFling.first * end.second - beforeFling.second * end.first;

        if (mFlingRunnable != null) {
            targetView.removeCallbacks(mFlingRunnable);
        }

        targetView.post(mFlingRunnable = new FlingRunnable(flingDirection > 0 ? vc : -vc));
        return true;
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
                targetView.postDelayed(this, 16);
                targetView.setRotation((float) mChangeCorner);
                //requestLayout();
            } else {
                endFling();
            }

        }

        private void endFling() {
            isFling = false;
            targetView.removeCallbacks(this);
        }
    }
}
