package com.jackiehou.dragdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.WindowManager;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/21 14:37
 ************************************************************/


public class MyApp extends Application implements Application.ActivityLifecycleCallbacks
{

    private WindowManager windowManager;

    static MyApp myApp;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;

        ActivityRecorder.getInstance().init();

        registerActivityLifecycleCallbacks(this);
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
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
