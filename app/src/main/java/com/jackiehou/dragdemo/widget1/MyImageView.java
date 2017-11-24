package com.jackiehou.dragdemo.widget1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/14 11:35
 ************************************************************/

@SuppressLint("AppCompatCustomView")
public class MyImageView extends ImageView {

    public static final String TAG = MyImageView.class.getSimpleName();
    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(getDragging()){
            super.onTouchEvent(event);
            return false;
        }
        return super.onTouchEvent(event);
    }

    public boolean getDragging(){
        if(getParent() != null && getParent() instanceof CircleLayout1){
            CircleLayout1 parent = (CircleLayout1) getParent();
            Log.i(TAG,"getDragging = "+parent.getDragging());
            return parent.getDragging();
        }
        return false;
    }
}
