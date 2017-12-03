package com.jackiehou.assistive.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
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
    private boolean isAlreadyShow;

    View.OnClickListener onClickListener;

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
    public void setOnClickListener(@Nullable OnClickListener l) {
        //super.setOnClickListener(l);
        onClickListener = l;
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
                isAlreadyShow = false;
                lastX = x;
                lastY = y;

                break;
            case MotionEvent.ACTION_MOVE:
                if (ismoving || Math.abs(x - lastX) > mTouchSlop || Math.abs(y - lastY) > mTouchSlop) {
                    ismoving = true;
                    showNaviBar();
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

    private void showNaviBar(){
        if(isAlreadyShow){
            return;
        }
        if(mAssistiveHelper != null){
            mAssistiveHelper.showNavBar(true,false);
            isAlreadyShow = true;
        }
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
            //Toast.makeText(getContext(),"endDrag",Toast.LENGTH_SHORT).show();
            if(onClickListener != null){
                onClickListener.onClick(this);
            }
        }
        if(ismoving){
            AnimatorSet animatorSet = new AnimatorSet();
            float transX = getTranslationX();
            float transY = getTranslationY();
            if(getTranslationY() > parentHeigth -(naviBarHeight*2)){
                getInNavAnim(animatorSet);
            }else{
                float minX = Math.min(transX,(parentWidth-transX-getWidth()));
                float minY = Math.min(transY,(parentHeigth-transY-getHeight()));
                if (minX <= minY) {
                    float toX = (minX == transX) ? 0:(parentWidth-getWidth());
                    AnimatorSet.Builder builder = animatorSet.play(ObjectAnimator.ofFloat(this, "translationX",toX));
                    if(transY < 0){
                        builder.with(ObjectAnimator.ofFloat(this, "translationY",0));
                    }
                }else {
                    if(minY ==transY){
                        AnimatorSet.Builder builder = animatorSet.play(ObjectAnimator.ofFloat(this, "translationY",0));
                        if(transX < 0 || transX+getWidth() > parentWidth){
                            builder.with(ObjectAnimator.ofFloat(this, "translationX",(transX <0) ? 0 :parentWidth-getWidth()));
                        }
                    }else {
                        getInNavAnim(animatorSet);
                    }
                }
            }
            animatorSet.addListener(new Animator.AnimatorListener() {
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
            animatorSet.setInterpolator(new AccelerateInterpolator());
            animatorSet.setDuration(200);
            animatorSet.start();
            if(mAssistiveHelper != null){
                mAssistiveHelper.addHideBarCallback();
            }
        }

    }

    public void getInNavAnim(AnimatorSet animatorSet){
        Pair<Integer,Integer> endPair = mAssistiveHelper.getNavHolePos();
        ObjectAnimator tranX = ObjectAnimator.ofFloat(this, "translationX",endPair.first);
        ObjectAnimator tranY = ObjectAnimator.ofFloat(this, "translationY",endPair.second);
        animatorSet.play(tranX).with(tranY);
    }

    public void saveLastCoords(){
          Paper.book().write(AssistiveHelper.KEY_COORDS,new FloatCoords((int)getTranslationX(),(int)getTranslationY()));

    }

    /**
     * 当球在坑里面的时候
     * @return
     */
    public ObjectAnimator getShowObjectAnimator(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "translationY",parentHeigth,parentHeigth-getHeight());
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new BounceInterpolator());
        return objectAnimator;
    }

    public ObjectAnimator getHideObjectAnimator(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "translationY",parentHeigth-getHeight(),parentHeigth);
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        return objectAnimator;
    }

}
