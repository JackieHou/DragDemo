package com.jackiehou.assistive.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.jackiehou.assistive.helper.AssistiveHelper;
import com.jackiehou.dragdemo.entity.FloatCoords;

import io.paperdb.Paper;


/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/12/1 15:03
 ************************************************************/

@SuppressLint("AppCompatCustomView")
public class AssistiveView extends Button {

    public static final String TAG = AssistiveView.class.getSimpleName();

    AssistiveHelper mAssistiveHelper;

    private final int parentWidth;

    private final int parentHeigth;

    private final int mTouchSlop;

    private final int naviBarHeight;

    private float lastX;
    private float lastY;

    private boolean ismoving;


    public AssistiveView(Context context, AssistiveHelper assistiveHelper){
        super(context);
        this.mAssistiveHelper = assistiveHelper;
        mTouchSlop = assistiveHelper.getTouchSlop()/2;
        Pair<Integer,Integer> pair = mAssistiveHelper.getContentSize();
        //android.R.id.content的宽度
        parentWidth = pair.first;
        //android.R.id.content的高度
        parentHeigth = pair.second;
        Log.w(TAG,"AssistiveView parentWidth = "+parentWidth+",parentHeigth = "+parentHeigth);
        Log.w(TAG,"AssistiveView getNavHolePos = "+assistiveHelper.getNavHolePos());
        naviBarHeight = (int) (parentWidth* NaviBarView.PERCENT);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getRawX();
        float y = e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ismoving = false;
                lastX = x;
                lastY = y;
                //todo 显示导航栏
                break;
            case MotionEvent.ACTION_MOVE:
                if (ismoving || Math.abs(x - lastX) > mTouchSlop || Math.abs(y - lastY) > mTouchSlop) {
                    ismoving = true;
                }
                updateViewPosition(x, y);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                endDrag(e);
                break;
            default:
                break;
        }

        //return ismoving;
        return super.onTouchEvent(e);
    }

    private void updateViewPosition(float x, float y) {
        Log.w(TAG,"updateViewPosition ismoving ="+ismoving);
        if(ismoving){
            float moveX = (x - lastX);
            float moveY = (y - lastY);

            float transX = getTranslationX();
            float transY = getTranslationY();

            transX += moveX;
            transY += moveY;

            setTranslationX(transX);
            setTranslationY(transY);

        }
        lastX = x;
        lastY = y;
    }

    private void endDrag(MotionEvent e) {
        if(!ismoving && e.getAction() == MotionEvent.ACTION_UP){
            Toast.makeText(getContext(),"endDrag",Toast.LENGTH_SHORT).show();
        }
        if(ismoving){
            ObjectAnimator objectAnimator;
            float transX = getTranslationX();
            float transY = getTranslationY();
            if(getTranslationY() > parentHeigth -(naviBarHeight*2)){
                Pair<Integer,Integer> endPair = mAssistiveHelper.getNavHolePos();
                objectAnimator = ObjectAnimator.ofObject(this,"translationPair",new PairEvaluator(),
                        new Pair<Integer,Integer>((int)transX,(int)transY),endPair);
            }else{
                float minX = Math.min(transX,(parentWidth-transX-getWidth()));
                float minY = Math.min(transY,(parentHeigth-transY-getHeight()));
                if (minX <= minY) {
                    float toX = (minX == transX) ? 0:(parentWidth-getWidth());
                    objectAnimator = ObjectAnimator.ofFloat(this, "translationX",toX);
                }else {
                    float toY = (minY == transY) ? 0:(parentHeigth-getHeight());
                    objectAnimator = ObjectAnimator.ofFloat(this, "translationY",toY);
                }
            }
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    saveLastCoords();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    saveLastCoords();
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            objectAnimator.setInterpolator(new AccelerateInterpolator());
            objectAnimator.setDuration(200);
            objectAnimator.start();
        }
    }

    public void saveLastCoords(){
          Paper.book().write(AssistiveHelper.KEY_COORDS,new FloatCoords((int)getTranslationX(),(int)getTranslationY()));

    }

    public void setTranslationPair(Pair<Integer,Integer> pair){
        setTranslationX(pair.first);
        setTranslationY(pair.second);
    }

    /**
     * 动画估值器
     */
    public class PairEvaluator implements TypeEvaluator<Pair<Integer,Integer>> {

        @Override
        public Pair<Integer, Integer> evaluate(float fraction, Pair<Integer, Integer> startValue, Pair<Integer, Integer> endValue) {
            //Log.w(TAG,"PairEvaluator fraction = "+fraction+"startValue = "+startValue);
            if(fraction <=0f){
                return startValue;
            }
            if(fraction >=1f){
                return endValue;
            }
            int devX = (int) ((endValue.first-startValue.first)*fraction);
            int devY = (int) ((endValue.second-startValue.second)*fraction);
            return new Pair<Integer, Integer>(startValue.first+devX,startValue.second+devY);
        }
    }

    /**
     * 当球在坑里面的时候
     * @return
     */
    public ObjectAnimator getShowObjectAnimator(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "translationY",parentHeigth,parentHeigth-getHeight());
        objectAnimator.setDuration(250);
        objectAnimator.setInterpolator(new BounceInterpolator());
        return objectAnimator;
    }

    public ObjectAnimator getHideObjectAnimator(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "translationY",parentHeigth-getHeight(),parentHeigth);
        objectAnimator.setDuration(250);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        return objectAnimator;
    }


}
