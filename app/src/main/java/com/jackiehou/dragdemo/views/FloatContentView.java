package com.jackiehou.dragdemo.views;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.jackiehou.dragdemo.R;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/21 09:14
 ************************************************************/

public class FloatContentView {

    public static final String TAG = "FloatContentView";

    private static WindowManager windowManager;

    private View mContentView;

    private static int widthPixels;
    private static int heightPixels;

    public FloatContentView(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // 获取手机屏幕尺寸
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        widthPixels = outMetrics.widthPixels;
        heightPixels = outMetrics.heightPixels;
    }

    private void createFloatView(Context context){

        mContentView = View.inflate(context, R.layout.float_content_view, null);
        // 创建布局的参数
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

			/*
             * 设置Toast的位置 以距离顶部距离和左侧距离作为定位标准
			 */
        params.gravity = Gravity.LEFT + Gravity.TOP;

        // 设置Toast显示位置
        /*params.x = mSharedPreferences.getInt("Left", 0);
        params.y = mSharedPreferences.getInt("Top", 0);*/

        // 高度包裹内容
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        // 宽度包裹内容
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// |
        // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|
        // WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        // 半透明
        params.format = PixelFormat.TRANSLUCENT;

			/*
             * 设置Toast类型 小米使用TYPE_TOAST类型就可以触摸 模拟器使用TYPE_PRIORITY_PHONE类型就可以触摸
			 */
        params.type = WindowManager.LayoutParams.TYPE_TOAST;

        // 显示自定义Toast
        windowManager.addView(mContentView, params);
    }

    public void showView(Context context){
        createFloatView(context);
    }

}
