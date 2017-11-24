package com.jackiehou.dragdemo.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import com.jackiehou.dragdemo.R;
import com.jackiehou.dragdemo.manager.FloatWindowManager;

/************************************************************
 * Created by houjie
 * Description:     // 悬浮的小球
 * Date: 2017/11/20 13:26
 ************************************************************/

public class FloatTouchView implements View.OnTouchListener {

    public static final String TAG = "FloatTouchView";

    private WindowManager windowManager;

    private final int mTouchSlop;

    private final int widthPixels;
    private final int heightPixels;

    private float lastX;
    private float lastY;

    private View mFloatView;
    private boolean ismoving;


    public FloatTouchView(Context context) {
        this((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
    }

    public FloatTouchView(WindowManager windowManager){
        this.windowManager = windowManager;
        mTouchSlop = FloatWindowManager.getInstance().getTouchSlop()/2;
        widthPixels = FloatWindowManager.getInstance().getWidthPixels();
        heightPixels = FloatWindowManager.getInstance().getHeightPixels();
    }

    /**
     * 创建悬浮小球View，并添加到windowManager
     * @param context
     */
    private void createTouchView(final Context context) {
        mFloatView = View.inflate(context, R.layout.float_touch_view, null);

        mFloatView.setOnTouchListener(this);
        mFloatView.setOnClickListener(view -> {
            //显示转盘\的popView
            FloatWindowManager.getInstance().showFloatPopView(getFloatViewRect());
        });

        // 创建布局的参数
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.gravity = Gravity.LEFT + Gravity.TOP;

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        // 不可获得焦点 不可触摸 屏幕常亮
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// |
        // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|
        // WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        // 半透明
        params.format = PixelFormat.TRANSLUCENT;

        //设置Toast类型 小米使用TYPE_TOAST类型就可以触摸 模拟器使用TYPE_PRIORITY_PHONE类型就可以触摸
        params.type = WindowManager.LayoutParams.TYPE_TOAST;

        // 显示自定义Toast
        windowManager.addView(mFloatView, params);

        //ImageView
    }

    /**
     * 显示悬浮小球
     * @param context
     */
    public void showFloatView(Context context) {
        if (mFloatView == null) {
            createTouchView(context);
        } else if (mFloatView.getVisibility() != View.VISIBLE) {
            mFloatView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏悬浮小球
     */
    public void hideFloatView() {
        if (mFloatView != null) {
            mFloatView.setVisibility(View.GONE);
        }
    }

    /**
     * 把悬浮小球从windowManager里面移除
     */
    public void removeFloatView() {
        try {
            if (mFloatView != null) {
                windowManager.removeView(mFloatView);
                mFloatView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent e) {
        float x = e.getRawX();
        float y = e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ismoving = false;
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (ismoving || Math.abs(x - lastX) > mTouchSlop || Math.abs(y - lastY) > mTouchSlop) {
                    ismoving = true;
                }
                updateViewPosition(x, y);
                break;
            case MotionEvent.ACTION_UP:
                endDrag(e);
                break;
            default:
                break;
        }

        return ismoving;
    }

    /**
     * up事件，然后play动画
     * @param e
     */
    private void endDrag(MotionEvent e) {
        if (mFloatView != null) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) mFloatView.getLayoutParams();
            if(heightPixels - params.y <= 50){
                // TODO: 2017/11/22 如果拖动到导航栏 则小球回到坑里面
                return;
            }
            lastX = params.x;
            lastY = params.y;
            int minX = Math.min(params.x, widthPixels - params.x);
            int minY = Math.min(params.y, heightPixels - params.y);
            ObjectAnimator objectAnimator;
            if (minX <= minY) {
                int toX = (minX == params.x) ? 0 : widthPixels;
                objectAnimator = ObjectAnimator.ofFloat(this, "translationX",params.x,toX);
            } else {
                int toY = (minY == params.y) ? 0 : heightPixels;
                objectAnimator = ObjectAnimator.ofFloat(this, "translationY",params.x,toY);
            }
            objectAnimator.setDuration(200);
            objectAnimator.setInterpolator(new AccelerateInterpolator());
            objectAnimator.start();
        }

    }

    /**
     * 小球根据x轴坐标进行位移
     * @param x
     */
    private void setTranslationX(float x) {
        updateViewPosition(x, lastY);
    }

    /**
     * 小球根据y轴坐标进行位移
     * @param y
     */
    private void setTranslationY(float y) {
        updateViewPosition(lastX, y);
    }

    /**
     * 更新小球xy轴坐标
     * @param x
     * @param y
     */
    private void updateViewPosition(float x, float y) {
        if (mFloatView != null && ismoving) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) mFloatView.getLayoutParams();

            float moveX = (x - lastX);
            float moveY = (y - lastY);

            params.x += moveX;
            params.y += moveY;
            // 处理手机屏幕移动出界问题
            if (params.x < 0) {
                params.x = 0;
            }
            if (params.y < 0) {
                params.y = 0;
            }
            // TODO: 2017/11/20 边界需要重新判断下
            if (params.x + mFloatView.getWidth() > widthPixels) {
                params.x = widthPixels - mFloatView.getWidth();
            }
            if (params.y + mFloatView.getHeight() > heightPixels - 30) {
                params.y = heightPixels - 30 - mFloatView.getHeight();
            }
            Log.i(TAG, "updateViewPosition moveX =" + moveX + ",moveY=" + moveY + ",params.x=" + params.x + ",params.y=" + params.y);
            windowManager.updateViewLayout(mFloatView, params);
            lastX = x;
            lastY = y;
        }

    }

    /**
     * 获取小球的位置矩形（相对屏幕的）
     * @return
     */
    public Rect getFloatViewRect() {
        if (mFloatView == null) {
            return null;
        }
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) mFloatView.getLayoutParams();
        Rect rect = new Rect();
        mFloatView.getGlobalVisibleRect(rect);
        Rect toRect = new Rect(rect.left + params.x, rect.top + params.y, rect.right + params.x, rect.bottom + params.y);
        Log.w(TAG, "getFloatViewRect = torect = " + toRect);
        return toRect;
    }

}
