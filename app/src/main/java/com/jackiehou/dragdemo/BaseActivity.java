package com.jackiehou.dragdemo;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.jackiehou.dragdemo.manager.FloatWindowManager;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/20 15:21
 ************************************************************/

public class BaseActivity extends AppCompatActivity {

    private int mTouchSlop = FloatWindowManager.getInstance().getTouchSlop();

    @Override
    protected void onStart() {
        super.onStart();
        //ActivityRecorder.getInstance().recordAtyOnStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //ActivityRecorder.getInstance().recordAtyOnStop(this);
    }


    float lastY;
    int orientation = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - lastY;
                if(Math.abs(dy) > mTouchSlop-1){
                    if(dy> 0){
                        orientation = -1;
                    }else {
                        orientation = 1;
                    }
                }else {
                    orientation = 0;
                }
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                updateTabBar();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void updateTabBar() {
        if(orientation == 1){
            FloatWindowManager.getInstance().showNaviBar(false);
        }else{
            FloatWindowManager.getInstance().hideNaviBar(false);
        }
    }
}
