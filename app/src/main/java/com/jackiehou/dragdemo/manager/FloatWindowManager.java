package com.jackiehou.dragdemo.manager;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.util.Pair;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.jackiehou.dragdemo.ActivityRecorder;
import com.jackiehou.dragdemo.MyApp;
import com.jackiehou.dragdemo.entity.FloatCoords;
import com.jackiehou.dragdemo.views.FloatPopView;
import com.jackiehou.dragdemo.views.FloatTouchView;
import com.jackiehou.dragdemo.views.NavigationBar;

import java.util.Objects;

import io.paperdb.Paper;

/************************************************************
 * Created by houjie
 * Description:     // 二级界面悬浮的导航栏、小球、和点击小球展开圆盘panel的管理单例类
 * Date: 2017/11/21 14:11
 ************************************************************/

public class FloatWindowManager {

    public static final String TAG = FloatWindowManager.class.getSimpleName();

    public static final String KEY_COORDS = "KEY_COORDS";

    public static final float FLOAT_PERCENT = 0.145f;

    private static FloatWindowManager mFloatManager;

    private WindowManager windowManager;

    //悬浮的导航栏
    private NavigationBar navigationBar;

    //二级界面拖动的小球
    private FloatTouchView mFloatTouchView;

    private int widthPixels;
    private int heightPixels;
    private int mTouchSlop;

    private int holeX;
    private int holeY;


    private FloatWindowManager() {

    }

    public static FloatWindowManager getInstance() {
        if (mFloatManager == null) {
            mFloatManager = new FloatWindowManager();
        }
        return mFloatManager;
    }

    public void initView(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initSize(context);

        mFloatTouchView = new FloatTouchView(windowManager);
        navigationBar = new NavigationBar(windowManager);

        initData();
    }

    private void initData() {
        holeX = (int) (widthPixels*(1-FLOAT_PERCENT)/2f);
        holeY = (int) (heightPixels-widthPixels*FLOAT_PERCENT);
        FloatCoords coords = Paper.book().read(KEY_COORDS);
        if (coords == null) {
            Paper.book().write(KEY_COORDS, new FloatCoords(holeX, holeY));
        }
    }

    private void initSize(Context context) {
        // 获取手机屏幕尺寸
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        widthPixels = outMetrics.widthPixels;
        heightPixels = outMetrics.heightPixels;

        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    /**
     * 获取上下文，ActivityRecorder中记录的onstart的Context，如果为空则使用MyApp的Context
     *
     * @return
     */
    public Context getContext() {
        Context context = ActivityRecorder.getInstance().getContext();
        if (context == null) {
            context = MyApp.getInstance();
        }
        return context;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public int getWidthPixels() {
        return widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public int getHeightPixels() {
        return heightPixels;
    }

    /**
     * 获取误触的长度
     *
     * @return
     */
    public int getTouchSlop() {
        return mTouchSlop;
    }

    /**
     * 进入二级页面需要显示 导航栏和悬浮的小球
     */
    public void showNaviAndFloatView(boolean playNavAnim) {
        showNaviBar(playNavAnim);
        //如果不在坑里面要显示小球
        if (mFloatTouchView != null && !isFloatInNavi()) {
            mFloatTouchView.showFloatView(getContext());
        }
    }

    /**
     * 删除掉导航栏和悬浮的小球
     */
    public void removeNaviAndFloatView() {
        if (navigationBar != null) {
            navigationBar.removeNaviBarView();
        }
        if (mFloatTouchView != null) {
            mFloatTouchView.removeFloatView();

        }
    }

    /**
     * 进入首页、登录、注册等页面不需要显示 导航栏和悬浮的小球
     */
    public void hideNaviAndFloatView(boolean playNavAnim) {
        if (navigationBar != null) {
            navigationBar.hideNaviBar(playNavAnim);
        }
        if (mFloatTouchView != null) {
            mFloatTouchView.hideFloatView();
        }
    }

    /**
     * 显示悬浮小球
     */
    public void showFloatTouchView() {
        if (mFloatTouchView == null) {
            mFloatTouchView = new FloatTouchView(getContext());
        }
        mFloatTouchView.showFloatView(getContext());
    }

    /**
     * 隐藏悬浮小球
     */
    public void hideFloatTouchView() {
        if (mFloatTouchView != null) {
            mFloatTouchView.hideFloatView();
        }
    }

    /**
     * 删除悬浮小球
     */
    public void removeFloatTouchView() {
        if (mFloatTouchView != null) {
            mFloatTouchView.removeFloatView();
        }
    }

    /**
     * 小球放到导航栏里面
     */
    public void floatPutInTheNavi(){

    }


    /**
     * 显示导航栏
     *
     * @param playNavAnim 是否播放动画
     */
    public void showNaviBar(boolean playNavAnim) {
        if (navigationBar != null) {
            navigationBar.showNaviBar(MyApp.getInstance(), playNavAnim, true);
        }
    }

    public void showNaviBar(boolean playNavAnim,Context context) {
        if (navigationBar != null) {
            navigationBar.showNaviBar(context, false, false);
        }
    }

    /**
     * 添加N毫秒之后隐藏导航栏的ACTION
     */
    public void addHideBarCallback() {
        if (navigationBar != null && navigationBar.isShowing()) {
            navigationBar.addHideBarCallback();
        }
    }

    /**
     * 隐藏导航栏
     *
     * @param playNavAnim 是否播放动画
     */
    public void hideNaviBar(boolean playNavAnim) {
        if (navigationBar != null) {
            navigationBar.hideNaviBar(playNavAnim);
        }
    }

    /**
     * 显示点击悬浮小球之后的popView
     *
     * @param rect
     */
    public void showFloatPopView(Rect rect) {
        Context context = ActivityRecorder.getInstance().getContext();
        Objects.requireNonNull(context);
        FloatPopView floatPopView = new FloatPopView(context);
        floatPopView.setOnDismissListener(() -> {
            showFloatTouchView();
        });
        hideFloatTouchView();
        floatPopView.showView(rect);
    }

    public boolean isFloatInNavi1(){
        if(mFloatTouchView == null){
            return false;
        }
        if(mFloatTouchView.getFloatCoords() == null){
            return false;
        }
        Pair<Integer ,Integer> pair = mFloatTouchView.getFloatCoords();
        /*if(Math.abs(pair.first-holeX) <= 2 && Math.abs(pair.second-holeY) <= 2){
            return true;
        }*/
        if(pair.first == holeX && pair.second == holeY){
            return true;
        }
        return false;
    }

    public Pair<Integer,Integer> getHoleCoords(){
        return new Pair<Integer,Integer>(holeX,holeY);
    }

    public boolean isFloatInNavi(){

        FloatCoords floatCoords = Paper.book().read(KEY_COORDS);
        /*if(Math.abs(pair.first-holeX) <= 2 && Math.abs(pair.second-holeY) <= 2){
            return true;
        }*/
        Log.w(TAG,"isFloatInNavi holeX = "+holeX+",holeY = "+holeY);
        if(floatCoords == null){
            return false;
        }
        Log.w(TAG,"isFloatInNavi floatCoords.getX() = "+floatCoords.getX()+",floatCoords.getY() = "+floatCoords.getY());
        if(floatCoords.getX() == holeX && floatCoords.getY() == holeY){
            return true;
        }
        return false;
    }

    public FloatCoords getFloatCoords() {
        return Paper.book().read(KEY_COORDS,new FloatCoords(holeX,holeY));
    }



}
