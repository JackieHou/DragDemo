package com.jackiehou.assistive.helper;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.jackiehou.assistive.widget.AssistiveView;
import com.jackiehou.assistive.widget.NaviBarView;
import com.jackiehou.dragdemo.ActivityRecorder;
import com.jackiehou.dragdemo.MyApp;
import com.jackiehou.dragdemo.R;
import com.jackiehou.dragdemo.entity.FloatCoords;
import com.jackiehou.dragdemo.views.FloatPopView;

import java.util.Objects;

import io.paperdb.Paper;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/12/1 14:53
 ************************************************************/

public class AssistiveHelper implements GestureDetector.OnGestureListener {

    public static final String TAG = "AssistiveHelper";
    //保存小球位置的key
    public static final String KEY_COORDS = "KEY_COORDS";
    //小球尺寸相对屏幕宽度的百分比例
    public static final float FLOAT_PERCENT = 0.145f;
    //导航栏自动消失的时间
    public static final int DELAYED_TIME = 3200;

    //屏幕宽度
    int screenWidth;
    int screenHeith;

    //Context mContext;

    //悬浮小球
    private AssistiveView mFloatView;

    //底部导航栏
    private NaviBarView mNaviBarView;

    //转盘的popview
    FloatPopView floatPopView;

    //误触距离
    private int mTouchSlop;

    //ID为android.R.id.content的view的尺寸
    int contentWidth;
    int contentHeigth;

    //ID为android.R.id.content的view距离顶部的距离
    int contentMaginTop;

    //底部导航栏的高度
    int naviHeight;

    //底部导航栏坑的坐标
    private int holeX;
    private int holeY;

    //最后一次的方向
    float lastDistanceY;

    //底部导航栏是否显示
    private boolean mIsShowing = false;

    //是否改变滑动方向
    boolean isChangeDirection = false;

    //是否touch悬浮小球
    boolean mIsTouchBall = false;

    //手势
    GestureDetectorCompat mDetector;

    //隐藏小球的函数表达式
    private Runnable hideNaviBarRunnable = () -> hideNavBar(true);

    public AssistiveHelper(final Context context, final ViewGroup contentView){
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        screenWidth = MyApp.getInstance().getWidthPixels();
        screenHeith = MyApp.getInstance().getHeightPixels();
        naviHeight = (int) (screenWidth * NaviBarView.PERCENT);
        mDetector = new GestureDetectorCompat(context, this);

        contentView.post(() -> initView(context, contentView));
    }

    /**
     * 创建小球和底部导航栏
     *
     * @param context
     * @param contentView
     */
    public void initView(Context context, ViewGroup contentView) {
        Log.w(TAG,"initView");
        //mContext = context;
        initSize(contentView);
        createNavBar(context, contentView);
        createFloatView(context, contentView);
        showNavBarAndAstBall();
    }

    /**
     * 获取android.R.id.content的view的尺寸和导航栏坑的尺寸
     *
     * @param contentView
     */
    public void initSize(ViewGroup contentView) {
        contentWidth = contentView.getMeasuredWidth();
        contentHeigth = contentView.getMeasuredHeight();
        holeX = (int) (contentWidth * (1 - FLOAT_PERCENT) / 2f);
        holeY = (int) (contentHeigth - contentWidth * FLOAT_PERCENT);
        Rect rect = new Rect();
        contentView.getGlobalVisibleRect(rect);
        contentMaginTop = rect.top;
    }

    /**
     * 显示导航栏和小球
     */
    public void showNavBarAndAstBall() {
        if (mFloatView == null || mNaviBarView == null) {
            return;
        }
        showNavBar(false, true);
        if (!isFloatInNavi()) {
            showAssiBall();
        }
    }

    /**
     * 显示小球
     */
    public void showAssiBall() {
        if (mFloatView != null) {
            FloatCoords floatCoords = getFloatCoords();
            mFloatView.setTranslationX(floatCoords.getX());
            mFloatView.setTranslationY(floatCoords.getY());
            mFloatView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏小球
     */
    public void hideAssiBall() {
        if (mFloatView != null) {
            mFloatView.setVisibility(View.GONE);
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

    /**
     * 显示导航栏
     *
     * @param playAnim
     */
    public void showNavBar(boolean playAnim, boolean isAddCallback) {
        if (mNaviBarView == null) {
            return;
        }
        removeHideBarCallback();
        if (!mIsShowing) {
            mIsShowing = true;
            if (playAnim) {
                mNaviBarView.setTranslationY(contentHeigth);
                AnimatorSet animatorSet = new AnimatorSet();
                AnimatorSet.Builder builder = animatorSet.play(mNaviBarView.getShowObjectAnimator());
                if (isFloatInNavi() && mFloatView != null) {
                    showAssiBall();
                    builder.with(mFloatView.getShowObjectAnimator());
                }
                animatorSet.start();
            } else {
                mNaviBarView.setTranslationY(contentHeigth - naviHeight);
                if (isFloatInNavi()) {
                    showAssiBall();
                }
            }
        }
        if (isAddCallback) {
            addHideBarCallback();
        }

    }

    /**
     * 隐藏导航栏
     *
     * @param playAnim
     */
    public void hideNavBar(boolean playAnim) {
        if (mNaviBarView == null) {
            return;
        }
        if (isShowing()) {
            if (playAnim) {
                AnimatorSet animatorSet = new AnimatorSet();
                AnimatorSet.Builder builder = animatorSet.play(mNaviBarView.getHideObjectAnimator());
                if (isFloatInNavi() && mFloatView != null) {
                    builder.with(mFloatView.getHideObjectAnimator());
                }
                animatorSet.start();
            } else {
                mNaviBarView.setTranslationX(0);
                mNaviBarView.setTranslationY(contentHeigth - naviHeight);
                if (isFloatInNavi()) {
                    hideAssiBall();
                }
            }
            removeHideBarCallback();
            mIsShowing = false;
        }

    }

    /**
     * 添加N毫秒之后隐藏导航栏的ACTION
     */
    public void addHideBarCallback() {
        if (hideNaviBarRunnable != null && isShowing()) {
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

    public boolean dissmissPopView(){
        if(floatPopView == null || !floatPopView.isShowing()){
            return false;
        }else {
            floatPopView.dismiss();
            return true;
        }
    }

    /**
     * 获取android.R.id.content的view的尺寸
     *
     * @return
     */
    public Pair<Integer, Integer> getContentSize() {
        return new Pair<>(contentWidth, contentHeigth);
    }

    /**
     * 获取导航栏坑的位置
     *
     * @return
     */
    public Pair<Integer, Integer> getNavHolePos() {
        return new Pair<>(holeX, holeY);
    }

    /**
     * 获取误触的距离
     *
     * @return
     */
    public int getTouchSlop() {
        return mTouchSlop;
    }


    /**
     * 创建悬浮小球
     *
     * @param context
     * @param contentView
     */
    private void createFloatView(Context context, ViewGroup contentView) {
        mFloatView = new AssistiveView(context, this);
        mFloatView.setBackgroundResource(R.mipmap.ball_tool);
        int width = (int) (FLOAT_PERCENT * screenWidth);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, width);
        contentView.addView(mFloatView, params);
        FloatCoords floatCoords = getFloatCoords();
        mFloatView.setTranslationX(floatCoords.getX());
        mFloatView.setTranslationY(floatCoords.getY());
        mFloatView.setOnClickListener(view -> showFloatPopView());

    }

    /**
     * 创建底部导航栏
     *
     * @param context
     * @param contentView
     */
    private void createNavBar(Context context, ViewGroup contentView) {
        mNaviBarView = (NaviBarView) LayoutInflater.from(context).inflate(R.layout.navi_bar_view, contentView, false);
        mNaviBarView.setAssistiveHelper(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(contentWidth, naviHeight);
        contentView.addView(mNaviBarView, params);
        mNaviBarView.setTranslationX(0);
        mNaviBarView.setTranslationY(contentHeigth - naviHeight);

    }

    /**
     * 显示点击悬浮小球显示popView
     *
     */
    public void showFloatPopView() {
        if(mFloatView == null){
            return;
        }
        Rect rect = new Rect();
        mFloatView.getGlobalVisibleRect(rect);
        //Rect targetRect = new Rect(rect.left,rect.top-contentMaginTop,rect.right,rect.bottom-contentMaginTop);
        floatPopView = new FloatPopView(mFloatView.getContext(),screenWidth,screenHeith);
        floatPopView.setOnDismissListener(() -> {
            if(!isFloatInNavi()){
                showAssiBall();
            }
        });
        if(!isFloatInNavi()){
            hideAssiBall();
        }
        floatPopView.showView(mFloatView,rect);
    }

    /**
     * 获取小球的位置，如果本地没有保存则默认把坑的位置存到本地
     *
     * @return
     */
    public FloatCoords getFloatCoords() {
        FloatCoords floatCoords = Paper.book().read(KEY_COORDS);
        if (floatCoords == null) {
            floatCoords = new FloatCoords(holeX, holeY);
            Paper.book().write(KEY_COORDS, floatCoords);
        }
        return floatCoords;
    }

    /**
     * 判断小球是否在导航栏的坑里面
     *
     * @return
     */
    public boolean isFloatInNavi() {
        FloatCoords coords = getFloatCoords();
        Log.w(TAG, "isFloatInNavi holeX = " + holeX + ",holeY = " + holeY);
        Log.w(TAG, "isFloatInNavi floatCoords.getX() = " + coords.getX() + ",floatCoords.getY() = " + coords.getY());
        if (Math.abs(holeX - coords.getX()) < 2 && coords.getY() >= holeY) {
            return true;
        }

        return false;
    }


    public void onTouchEvent(MotionEvent e){
        if(isTouchBall(e)){
            return;
        }
        if(e.getAction() == MotionEvent.ACTION_DOWN){
            isChangeDirection = false;
        }
        mDetector.onTouchEvent(e);
        if(e.getAction() == MotionEvent.ACTION_UP){
            Log.w(TAG, "onTouchEvent MotionEvent.ACTION_UP");
            addHideBarCallback();
        }
    }

    public boolean isTouchBall(MotionEvent e){
        if(e.getAction() == MotionEvent.ACTION_DOWN){
            mIsTouchBall = false;
            if(mFloatView != null){
                Rect rect = new Rect();
                mFloatView.getGlobalVisibleRect(rect);
                mIsTouchBall = rect.contains((int)e.getRawX(),(int)e.getRawY());
            }
        }
        return mIsTouchBall;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
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
        Log.w(TAG,"onScroll distanceX="+distanceX+", distanceY = "+distanceY);
        if(isChangeDirection){
            return false;
        }
        if((distanceY < 0 && lastDistanceY > 0) ||
                (distanceY > 0 && lastDistanceY < 0)){
            lastDistanceY = distanceY;
            hideNavBar(true);
            isChangeDirection = true;
            return false;
        }
        if(distanceY > 0){
            showNavBar(true,false);
        }else if(distanceY < 0){
            hideNavBar(true);
        }
        lastDistanceY = distanceY;
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.w(TAG,"onFling velocityX="+velocityX+", velocityY = "+velocityY);
        if(velocityY > 0){
            hideNavBar(true);
        }else if(velocityY < 0){
            showNavBar(true,false);
        }
        return false;
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
            if (!mIsShowing && isFloatInNavi()) {
                hideAssiBall();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            if (!mIsShowing && isFloatInNavi()) {
                hideAssiBall();
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
