package com.jackiehou.dragdemo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.CheckedTextView;

import com.jackiehou.dragdemo.widget1.CircleLayout1;

import java.util.function.Consumer;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/14 11:35
 ************************************************************/

@SuppressLint({"AppCompatCustomView", "NewApi"})
public class DragCheckedTv extends CheckedTextView implements Consumer<Boolean> {

    public static final String TAG = DragCheckedTv.class.getSimpleName();
    boolean isLongDrag = false;

    public DragCheckedTv(Context context) {
        super(context);
    }

    public DragCheckedTv(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragCheckedTv(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            isLongDrag = false;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isLongDrag){
            return false;
        }
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

    @Override
    public void accept(Boolean b) {

    }
}
