package com.jackiehou.dragdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jackiehou.dragdemo.views.FloatTouchView;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/20 14:24
 ************************************************************/

// TODO: 有风险，如果该服务被系统回收，没有执行onDestroy，可能会出问题
public class FloatTouchService extends Service {

    public static final String SHOW_FLOAT_VIEW_ACTION = "com.zhicai.chezhen.service.SHOW_FLOAT_VIEW";
    public static final String REMOVE_FLOAT_VIEW_ACTION = "com.zhicai.chezhen.service.REMOVE_FLOAT_VIEW";

    FloatTouchView mFloatTouchView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatTouchView = new FloatTouchView(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if(SHOW_FLOAT_VIEW_ACTION.equals(action)){
            mFloatTouchView.showFloatView(this);
        }else if(REMOVE_FLOAT_VIEW_ACTION.equals(action)){
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mFloatTouchView != null){
            mFloatTouchView.removeFloatView();
        }
    }
}
