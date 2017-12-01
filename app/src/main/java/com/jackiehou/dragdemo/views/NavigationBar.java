package com.jackiehou.dragdemo.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.jackiehou.dragdemo.R;
import com.jackiehou.dragdemo.manager.FloatWindowManager;

/************************************************************
 * Created by houjie
 * Description:     // 底部的 我的、商城 、悬浮球/悬浮球的坑 的导航栏
 * Date: 2017/11/23 14:21
 ************************************************************/

public class NavigationBar {

    public static final String TAG = NavigationBar.class.getSimpleName();

    public static final int DELAYED_TIME = 3200;

    //长宽的比例
    public static final float PERCENT = 0.196f;

    private WindowManager windowManager;

    private View mNaviBarView;

    private ImageView holeImg;

    private View animTarget;

    private final int mTouchSlop;

    private final int widthPixels;
    private final int heightPixels;
    private final int sceenHeight;

    private boolean mIsShowing = false;

    private Runnable hideNaviBarRunnable;

    public NavigationBar(WindowManager windowManager) {
        this.windowManager = windowManager;
        mTouchSlop = FloatWindowManager.getInstance().getTouchSlop() / 2;
        widthPixels = FloatWindowManager.getInstance().getWidthPixels();
        sceenHeight = FloatWindowManager.getInstance().getHeightPixels();
        heightPixels = (int) (widthPixels * PERCENT);
    }

    /**
     * 创建导航栏，并把它添加到window上
     *
     * @param context
     */
    private void createNaviBarView(Context context) {
        mNaviBarView = View.inflate(context, R.layout.navi_bar_view1, null);
        animTarget = mNaviBarView.findViewById(R.id.nav_bar_prl);
        holeImg = (ImageView) mNaviBarView.findViewById(R.id.nav_hole);

        holeImg.setVisibility(isFloatInNavi()?View.VISIBLE:View.GONE);
        hideNaviBarRunnable = () -> {
            hideNaviBar(true);
        };
        // 创建布局的参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.gravity = Gravity.BOTTOM;
        params.width = widthPixels;
        params.height = heightPixels;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.type = WindowManager.LayoutParams.TYPE_TOAST;

        // 半透明 如果不加这个会有阴影(透明)的地方会变成黑的（因为它不会通知GPU合成Z轴图层的纹理）
        params.format = PixelFormat.TRANSLUCENT;

        params.windowAnimations = 0;

        windowManager.addView(mNaviBarView, params);

    }

    /**
     * 显示导航栏
     *
     * @param context
     * @param playAnim 是否播放动画
     */
    public void showNaviBar(Context context, boolean playAnim,boolean isAddCallback) {
        removeHideBarCallback();
        if (!isShowing()) {
            mIsShowing = true;
            if (mNaviBarView == null) {
                createNaviBarView(context);
            }
            mNaviBarView.setVisibility(View.VISIBLE);
            if (playAnim) {
                animTarget.setTranslationY(heightPixels);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animTarget, "translationY", heightPixels, 0);
                objectAnimator.setDuration(1000);
                objectAnimator.setInterpolator(new AccelerateInterpolator());
                objectAnimator.addListener(new NaviAnimatorListener());
                objectAnimator.start();
            } else {
                if(isFloatInNavi()){
                    holeImg.setVisibility(View.GONE);
                    FloatWindowManager.getInstance().showFloatTouchView();
                }
                animTarget.setTranslationY(0);
            }
        }
        if(isAddCallback){
            addHideBarCallback();
        }
    }

    /**
     * 隐藏导航栏
     *
     * @param playAnim 是否播放动画
     */
    public void hideNaviBar(boolean playAnim) {
        if (isShowing()) {
            if(isFloatInNavi()){
                holeImg.setVisibility(View.VISIBLE);
                FloatWindowManager.getInstance().hideFloatTouchView();
            }
            animTarget.setTranslationY(0);
            if (playAnim) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animTarget, "translationY", 0, heightPixels);
                objectAnimator.setDuration(200);
                objectAnimator.setInterpolator(new AccelerateInterpolator());
                objectAnimator.addListener(new NaviAnimatorListener());
                objectAnimator.start();
            } else {
                mNaviBarView.setVisibility(View.GONE);
            }
            removeHideBarCallback();
        }
        mIsShowing = false;
    }

    /**
     * 添加N毫秒之后隐藏导航栏的ACTION
     */
    public void addHideBarCallback() {
        if (mNaviBarView != null && hideNaviBarRunnable != null) {
            mNaviBarView.postDelayed(hideNaviBarRunnable, DELAYED_TIME);
        }
    }

    /**
     * 移除隐藏导航栏的Callback
     */
    public void removeHideBarCallback() {
        if (mNaviBarView != null) {
            mNaviBarView.removeCallbacks(hideNaviBarRunnable);
        }
    }

    /**
     * 把导航栏从window里面移除
     */
    public void removeNaviBarView() {
        try {
            if (mNaviBarView != null) {
                windowManager.removeView(mNaviBarView);
                mIsShowing = false;
                mNaviBarView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 是否显示了导航栏
     *
     * @return
     */
    public boolean isShowing() {
        return mIsShowing && mNaviBarView != null;
    }

    public boolean isFloatInNavi(){
        return FloatWindowManager.getInstance().isFloatInNavi();
    }

    /**
     * 动画的监听
     */
    class NaviAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if(animTarget != null){
                animTarget.setTranslationY(0);
            }
            if(mNaviBarView != null){
                mNaviBarView.setVisibility(mIsShowing ? View.VISIBLE :View.GONE);
            }
            if(mIsShowing && isFloatInNavi()){
                holeImg.setVisibility(View.GONE);
                FloatWindowManager.getInstance().showFloatTouchView();

            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            if(animTarget != null){
                animTarget.setTranslationY(0);
            }
            if(mNaviBarView != null){
                mNaviBarView.setVisibility(mIsShowing ? View.VISIBLE :View.GONE);
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
