package com.jackiehou.dragdemo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Supplier;


/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/14 11:35
 ************************************************************/

@SuppressLint({"AppCompatCustomView", "NewApi"})
public class DragButton extends Button implements Consumer<Boolean> {

    boolean isLongDrag = false;

    public static final String TAG = DragButton.class.getSimpleName();
    public DragButton(Context context) {
        super(context);
    }

    public DragButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragButton(Context context, AttributeSet attrs, int defStyleAttr) {
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
            super.onTouchEvent(event);
            Log.w(TAG,"onTouchEvent = isLongDrag = "+isLongDrag);
            return false;
        }
        if(getDragging()){
            super.onTouchEvent(event);
            return false;
        }
        Log.w(TAG,"onTouchEvent event = "+event);
        return super.onTouchEvent(event);
    }

    public boolean getDragging(){
        if(getParent() != null && getParent() instanceof CircleLayout){
            CircleLayout parent = (CircleLayout) getParent();
            Log.i(TAG,"getDragging = "+parent.getDragging());
            return parent.getDragging();
        }
        return false;
    }

    @Override
    public void accept(Boolean b) {
        isLongDrag = b;
    }
}
