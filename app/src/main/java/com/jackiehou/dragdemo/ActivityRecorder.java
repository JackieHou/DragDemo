package com.jackiehou.dragdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jackiehou.dragdemo.manager.FloatWindowManager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/************************************************************
 * Created by houjie
 * Description:     // 记录Activity的start stop等状态
 * Date: 2017/11/20 13:45
 ************************************************************/

public class ActivityRecorder {

    public static ActivityRecorder activityRecorder;

    Set<Integer> set= new HashSet<Integer>();

    FloatWindowManager floatManager;

    //保存当前start状态的activity的context
    private Context mContext;

    public Context getContext() {
        return mContext;
    }

    private ActivityRecorder(){
    }

    public static ActivityRecorder getInstance(){
        if(activityRecorder == null){
            activityRecorder = new ActivityRecorder();
        }
        return activityRecorder;
    }

    public void init(){
        release();
        floatManager = FloatWindowManager.getInstance();
        floatManager.initView(MyApp.getInstance());
    }

    /**
     * 记录activity的onStart状态
     * @param context
     */
    public void recordOnAtyStarted(Context context){
        mContext = context;
        if(context instanceof MainActivity){
            onHomeAtyStart(context);
        }else {
            onOtherAtyStart(context);
        }
    }

    /**
     * 记录activity的onStop状态
     * @param context
     */
    public void recordOnAtyStoped(Context context){
        if(context instanceof MainActivity){
            return;
        }
        set.remove(context.hashCode());
        if(set.size() == 0){
            floatManager.removeNaviAndFloatView();
        }
    }

    /**
     * 二级界面进入到onStart方法
     */
    private void onOtherAtyStart(Context context) {
        set.add(context.hashCode());
        floatManager.showNaviAndFloatView(false);

    }

    /**
     * 进入到home界面
     */
    private void onHomeAtyStart(Context context) {
        //返回首页清除所以SET数据
        set.clear();
        floatManager.removeNaviAndFloatView();
    }

    public void startActivity(Intent intent){
        Objects.requireNonNull(mContext);
        Objects.requireNonNull(intent);
        ComponentName component = intent.getComponent();
        if(component != null && ((Activity)mContext).getClass().getName().equals(component.getClassName())){
            Toast.makeText(mContext,"您已在次界面！",Toast.LENGTH_LONG).show();
            return;
        }
        mContext.startActivity(intent);
    }

    public void release(){
        set.clear();
        mContext = null;
    }

}
