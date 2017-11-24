package com.jackiehou.dragdemo.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.PopupWindow;

import com.jackiehou.dragdemo.ActivityRecorder;
import com.jackiehou.dragdemo.R;
import com.jackiehou.dragdemo.manager.FloatWindowManager;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/20 17:33
 ************************************************************/

public class FloatPopView extends PopupWindow{

    public static final String TAG = FloatPopView.class.getSimpleName();

    private final int widthPixels;
    private final int heightPixels;

    private Rect fromRect;

    public static final float PERCENT = 0.74f;

    CircleLayoutPlanB circleLayout;


    public FloatPopView(Context context) {
        super(context,null,0);
        widthPixels = FloatWindowManager.getInstance().getWidthPixels();
        heightPixels = FloatWindowManager.getInstance().getHeightPixels();
        initPopupWindow(context);
    }

    private void initPopupWindow(Context context) {
        //使用view来引入布局
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.float_content_view, null);

        circleLayout = (CircleLayoutPlanB) contentView.findViewById(R.id.circle_layout);

        contentView.setOnClickListener(view ->{
            dismiss();
        });

        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth((int) (widthPixels));
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight((int) (heightPixels));
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //this.setBackgroundDrawable(null);

    }

    public void showView(Rect rect){
        fromRect = rect;
        Activity activity = (Activity) ActivityRecorder.getInstance().getContext();
        if(activity != null){
            super.showAtLocation(activity.getWindow().getDecorView(), Gravity.TOP, 0, 0);

            Log.w(TAG,"showView rect="+rect);

            float fromScale = rect.width()/(widthPixels*PERCENT);
            float fromX = rect.left-(widthPixels-widthPixels*PERCENT)/2;
            float fromY = rect.top -(heightPixels-widthPixels*PERCENT)/2;

            circleLayout.setPivotX(0);
            circleLayout.setPivotY(0);

            circleLayout.setTranslationX(fromX);
            circleLayout.setTranslationY(fromY);

            circleLayout.setScaleX(fromScale);
            circleLayout.setScaleY(fromScale);

            AnimatorSet animatorSet = new AnimatorSet();

            ObjectAnimator animScaleX =ObjectAnimator.ofFloat(circleLayout,"scaleX",1.0f);
            ObjectAnimator animScaleY =ObjectAnimator.ofFloat(circleLayout,"scaleY",1.0f);

            ObjectAnimator translationX= ObjectAnimator.ofFloat(circleLayout, "translationX", 0f);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(circleLayout, "translationY", 0f);

            animatorSet.setDuration(200);
            animatorSet.setInterpolator(new AccelerateInterpolator());
            animatorSet.play(translationX).with(translationY).with(animScaleX).with(animScaleY);
            animatorSet.start();



        }else {
            // TODO: 2017/11/21 通过bugly 上传日志

        }


    }

    @Override
    public void dismiss() {
        circleLayout.setPivotX(0);
        circleLayout.setPivotY(0);
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animScaleX =ObjectAnimator.ofFloat(circleLayout,"scaleX",fromRect.width()/(widthPixels*PERCENT));
        ObjectAnimator animScaleY =ObjectAnimator.ofFloat(circleLayout,"scaleY",fromRect.width()/(widthPixels*PERCENT));

        float toX = fromRect.left-(widthPixels-widthPixels*PERCENT)/2;
        float toY = fromRect.top -(heightPixels-widthPixels*PERCENT)/2;

        ObjectAnimator translationX= ObjectAnimator.ofFloat(circleLayout, "translationX", toX);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(circleLayout, "translationY", toY);

        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.play(translationX).with(translationY).with(animScaleX).with(animScaleY);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                FloatPopView.super.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                FloatPopView.super.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        //animatorSet.play(animScaleX).with(animScaleY);
        animatorSet.start();

    }
}
