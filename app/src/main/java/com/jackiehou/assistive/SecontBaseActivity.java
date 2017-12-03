package com.jackiehou.assistive;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.jackiehou.assistive.helper.AssistiveHelper;
import com.jackiehou.dragdemo.DragActivity;
import com.jackiehou.dragdemo.manager.FloatWindowManager;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/12/1 14:50
 ************************************************************/

public class SecontBaseActivity extends AppCompatActivity {

    AssistiveHelper mAssistiveHelper;

    private int mTouchSlop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ViewConfiguration configuration = ViewConfiguration.get(this);
        mTouchSlop = configuration.getScaledTouchSlop();
        ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
        mAssistiveHelper = new AssistiveHelper(this,viewGroup);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(mAssistiveHelper != null){
            mAssistiveHelper.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if(mAssistiveHelper != null){
            if(!mAssistiveHelper.dissmissPopView()){
                super.onBackPressed();
            }
        }else {
            super.onBackPressed();
        }

    }


}
