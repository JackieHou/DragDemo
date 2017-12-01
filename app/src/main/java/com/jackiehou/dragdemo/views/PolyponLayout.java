package com.jackiehou.dragdemo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by le on 2017/11/22.
 */

public class PolyponLayout extends RelativeLayout {

    private float persent = 1;

    private Path path;


    public PolyponLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //开启硬件加速canvas.clipPath方法不管用
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //防止onDraw方法不执行
        setWillNotDraw(false);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Log.w("PolyponLayout", "onDraw isHardwareAccelerated = " + canvas.isHardwareAccelerated());

        if (path == null) {
            path = new Path();

            path.moveTo(change(43), change(19));
            path.lineTo(change(296), change(19));
            path.lineTo(change(314), change(34));
            path.lineTo(change(422), change(34));
            path.lineTo(change(457), change(19));
            path.lineTo(change(707), change(19));
            path.lineTo(change(721), change(34));
            path.lineTo(change(721), change(165));
            path.lineTo(change(704), change(187));
            path.lineTo(change(704), change(263));
            path.lineTo(change(640), change(324));
            path.lineTo(change(104), change(324));
            path.lineTo(change(46), change(263));
            path.lineTo(change(46), change(187));
            path.lineTo(change(31), change(165));
            path.lineTo(change(31), change(34));
            path.lineTo(change(43), change(19));


            //path.addArc();
            path.close();
        }


        canvas.clipPath(path);



    }



    private float change(float x) {
        return x * persent;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        persent = getMeasuredWidth() / 750f;
        Log.w("PolyponLayout", "onMeasure persent =" + persent);
    }

}
