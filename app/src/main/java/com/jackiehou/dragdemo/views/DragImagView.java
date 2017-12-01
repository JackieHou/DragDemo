package com.jackiehou.dragdemo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.annimon.stream.function.Consumer;
import com.jackiehou.dragdemo.MyApp;


/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/14 11:35
 ************************************************************/

@SuppressLint({"AppCompatCustomView", "NewApi"})
public class DragImagView extends AppCompatImageButton implements Consumer<Boolean> {

    public static final String TAG = DragImagView.class.getSimpleName();

    View.OnLongClickListener onLongClickListener;

    //public static final float PERCENT = 0.067f;
    public static final float PERCENT = 0.1f;
    boolean isLongDrag = false;


    private int width;

    private float startX;
    private float startY;

    private int longTime = 1000*3;

    int mSlop;

    Runnable longClickAction;

    private long startTime;

    public DragImagView(Context context) {
        super(context);
        init(context);
    }


    public DragImagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DragImagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        width = (int) (MyApp.getInstance().getWidthPixels()*PERCENT);
        longTime = ViewConfiguration.getLongPressTimeout();
        mSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Log.w(TAG,"init WIDTH = "+width);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width,width);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            startTime = SystemClock.elapsedRealtime();
            isLongDrag = false;
            startX = x;
            startY = y;
            addLongClickAction();
        }if(event.getAction() == MotionEvent.ACTION_MOVE){
            //if(Math.abs(x-startX) > mSlop || Math.abs(y-startY) > mSlop){
                removeLongClickAction();
            //}
        }if(event.getAction() ==MotionEvent.ACTION_UP){
            removeLongClickAction();
        }
        return super.dispatchTouchEvent(event);
    }

    public void addLongClickAction(){
        if(longClickAction == null){
            longClickAction = () ->{
                if(onLongClickListener != null){
                    onLongClickListener.onLongClick(DragImagView.this);
                }
            };
        }
        postDelayed(longClickAction,longTime);
    }

    public void removeLongClickAction(){
        if(longClickAction != null){
            removeCallbacks(longClickAction);
            longClickAction = null;
        }
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




    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        super.setOnLongClickListener(l);
        //onLongClickListener = l;
    }
}
