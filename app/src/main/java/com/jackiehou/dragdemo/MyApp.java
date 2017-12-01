package com.jackiehou.dragdemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.jackiehou.dragdemo.db.OrmHelper;

import io.paperdb.Paper;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/21 14:37
 ************************************************************/


public class MyApp extends Application implements Application.ActivityLifecycleCallbacks
{


    static MyApp myApp;

    private int widthPixels;
    private int heightPixels;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        initSize();
        Paper.init(this);

        OrmHelper.getHelper(this).init();

        ActivityRecorder.getInstance().init();



        registerActivityLifecycleCallbacks(this);
    }

    private void initSize() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // 获取手机屏幕尺寸
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        widthPixels = outMetrics.widthPixels;
        heightPixels = outMetrics.heightPixels;
    }

    public int getWidthPixels() {
        return widthPixels;
    }

    public int getHeightPixels() {
        return heightPixels;
    }


    public static MyApp getMyApp() {
        return myApp;
    }

    public static void setMyApp(MyApp myApp) {
        MyApp.myApp = myApp;
    }

    public static MyApp getInstance(){
        return myApp;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        ActivityRecorder.getInstance().recordOnAtyStarted(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        ActivityRecorder.getInstance().recordOnAtyStoped(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
