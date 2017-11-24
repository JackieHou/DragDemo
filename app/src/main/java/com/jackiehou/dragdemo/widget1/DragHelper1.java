package com.jackiehou.dragdemo.widget1;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/14 13:42
 ************************************************************/

public class DragHelper1 {

    public static final String TAG = DragHelper1.class.getSimpleName();


    private static final int SNAP_ANIM_LEN = 250; // ms

    private View targetView;
    //速度计算
    VelocityTracker mVelocityTracker;

    private float centerX;

    private float centerY;


    ObjectAnimator mFlingAnim;

    private final int mTouchSlop;
    private final int mMinimumVelocity;
    private final int mMaximumVelocity;

    private float mBeforeAngle;

    //检测按下到抬起时旋转的角度
    private float mCurrAngle;

    private Pair<Float,Float> beforePair;

    private Pair<Float,Float> mCurrPair;

    private float mXPos;
    private float mYPos;
    private volatile boolean mDragging;

    public DragHelper1(View targetView, ViewConfiguration viewConfiguration) {
        this(targetView,0,0,viewConfiguration);
    }

    public DragHelper1(View targetView, float centerX, float centerY, ViewConfiguration configuration) {
        this.targetView = targetView;
        this.centerX = centerX;
        this.centerY = centerY;
        mVelocityTracker = VelocityTracker.obtain();
        mTouchSlop = configuration.getScaledPagingTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mCurrAngle = targetView.getRotation();
    }

    public void setCenter(float centerX,float centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
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
                //mVelocityTracker.addMovement(ev);

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
                Log.i(TAG,"onInterceptTouchEvent action = "+ev.getAction()+",mDragging = "+mDragging);
                break;
            default:
                break;
        }
        return mDragging;
    }

    public boolean onTouchEvent(MotionEvent e) {
        /*if(!mDragging){
            onInterceptTouchEvent(e);
        }*/

        mVelocityTracker.addMovement(e);
        switch (e.getAction()){
            case MotionEvent.ACTION_MOVE:
                updateAngle(e.getX(),e.getY());
                Log.i(TAG,"onTouchEvent mCurrAngle = "+mCurrAngle);
                targetView.setRotation(mCurrAngle);
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                endDrag(mVelocityTracker,e);
                break;
            default:
                break;
        }

        return true;
    }

    private void endDrag(VelocityTracker vt,MotionEvent e) {
        vt.computeCurrentVelocity(1000,10000);


        double v = Math.sqrt(Math.pow(vt.getXVelocity(), 2) + Math.pow(vt.getYVelocity(), 2));

        double oe1 = Math.sqrt(Math.pow(beforePair.first, 2) + Math.pow(beforePair.second, 2));
        double oe2 = Math.sqrt(Math.pow(e.getX() - centerX, 2) + Math.pow(e.getY() - centerY, 2));
        double e1e2 = Math.sqrt(Math.pow(e.getX() - beforePair.first, 2) + Math.pow(e.getY() - beforePair.second, 2));
        Log.i(TAG,"endDrag oe1 = "+oe1+" ,oe2="+oe2+", v = "+v);

        double sin = Math.sqrt(Math.pow(1 - (Math.pow(oe2, 2) + Math.pow(e1e2, 2) - Math.pow(oe1, 2)) / (2 * oe2 * e1e2), 2));
        double vc = 180 * v * sin / (Math.PI * oe2);
        //平均速度
        float angle = 0;
        Log.i(TAG,"endDrag mCurrAngle = "+mCurrAngle+" ,mBeforeAngle="+mBeforeAngle+", vc = "+vc);
        if(mBeforeAngle == mCurrAngle){
            return;
        }
        vc = (mCurrAngle > mBeforeAngle) ? vc :-vc;
        //Log.i(TAG,"endDrag angle = "+angle+",velocity="+velocity);

        //(mChangeCorner + v / 1000 * 16) % 360;

        mFlingAnim = ObjectAnimator.ofFloat(targetView,View.ROTATION,mCurrAngle+(float)(vc /2));
        mFlingAnim.setInterpolator(new DecelerateInterpolator());
        mFlingAnim.setDuration(SNAP_ANIM_LEN);
        mFlingAnim.start();
    }

    public boolean getDragging(){
        return mDragging;
    }

    /**
     * 更新按下到抬起时旋转的角度
     * @param x
     * @param y
     */
    private void updateAngle(float x,float y){
        /**
         * 获得开始的角度
         */
        float start = getAngle(mXPos, mYPos);
        /**
         * 获得当前的角度
         */
        float end = getAngle(x,y);
        mBeforeAngle = mCurrAngle;
        if(mCurrPair == null){
            beforePair = new Pair<Float,Float>(mXPos-centerX,mYPos-centerY);
        }else {
            beforePair = mCurrPair;
        }
        if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
            mCurrAngle += end - start;
        } else {// 二、三象限，色角度值是负值
            mCurrAngle += start - end;
        }
        mCurrAngle = mCurrAngle % 360;
        mCurrPair = new Pair<>(x-centerX,y-centerY);
    }


    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch){
        double x = xTouch - centerX;
        double y = yTouch - centerY;
        //Log.i(TAG,"x = "+x+",y = "+y+"  xTouch = "+xTouch+",yTouch = "+yTouch);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置计算象限
     *
     * @param x
     * @param y
     * @return
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - centerX);
        int tmpY = (int) (y - centerY);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }

    }
}
