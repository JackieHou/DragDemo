package com.jackiehou.dragdemo.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.jackiehou.dragdemo.MyApp;
import com.jackiehou.dragdemo.R;


/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/14 11:35
 ************************************************************/

@SuppressLint({"AppCompatCustomView", "NewApi"})
public class PercentImagView extends AppCompatImageView{

    public static final String TAG = PercentImagView.class.getSimpleName();

    private float mPercent = 0.145f;

    private float sceenWidth;

    public PercentImagView(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public PercentImagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sceenWidth = MyApp.getInstance().getWidthPixels();
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CircleLayout, defStyleAttr, defStyleAttr);
        if (attrs != null) {
            try {
                mPercent = a.getFloat(R.styleable.PercentImagView_percent,0.145f);
            } finally {
                a.recycle();
            }

        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = (int) (sceenWidth*mPercent);
        setMeasuredDimension(width,width);
    }
}
