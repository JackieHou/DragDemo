package com.jackiehou.assistive;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.jackiehou.assistive.helper.AssistiveHelper;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/12/1 14:50
 ************************************************************/

public class SecontBaseActivity extends AppCompatActivity {

    AssistiveHelper mAssistiveHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
        mAssistiveHelper = new AssistiveHelper();
        mAssistiveHelper.init(this,viewGroup);
    }



}
