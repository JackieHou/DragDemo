package com.jackiehou.assistive.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import com.jackiehou.assistive.helper.AssistiveHelper;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/23 17:18
 ************************************************************/

public class NaviBarView extends RelativeLayout {

    //长宽的比例
    public static final float PERCENT = 0.196f;

    public static final String TAG =NaviBarView.class.getSimpleName();
    private int parentWidth;
    private int parentHeigth;

    private int naviBarHeight;

    AssistiveHelper mAssistiveHelper;

    public NaviBarView(Context context) {
        super(context);
    }

    public NaviBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NaviBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = (int) (getMeasuredWidth()*PERCENT);
        /*int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);*/

        setMeasuredDimension(width,height);
    }

    public void setAssistiveHelper(AssistiveHelper assistiveHelper){
        mAssistiveHelper = assistiveHelper;
        Pair<Integer,Integer> pair = mAssistiveHelper.getContentSize();
        //android.R.id.content的宽度
        parentWidth = pair.first;
        //android.R.id.content的高度
        parentHeigth = pair.second;

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    /**
     * 当球在坑里面的时候
     * @return
     */
    public ObjectAnimator getShowObjectAnimator(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "translationY",parentHeigth,parentHeigth-getHeight());
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        return objectAnimator;
    }

    public ObjectAnimator getHideObjectAnimator(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "translationY",parentHeigth-getHeight(),parentHeigth);
        objectAnimator.setDuration(200);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        return objectAnimator;
    }
}
