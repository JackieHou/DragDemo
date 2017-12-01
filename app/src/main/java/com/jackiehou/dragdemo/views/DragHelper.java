package com.jackiehou.dragdemo.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.jackiehou.dragdemo.Utils;
import com.jackiehou.dragdemo.db.OrmHelper;
import com.jackiehou.dragdemo.greendao.DragItemEntity;
import com.jackiehou.dragdemo.entity.DragTaget;

import java.util.List;

import static com.annimon.stream.Collectors.toList;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/16 13:26
 ************************************************************/

public class DragHelper implements GestureDetector.OnGestureListener{

    public static final String TAG = DragLayout.class.getSimpleName();

    public interface OnDragEndCallback{
        public void onDragEnd();
    }

    OnDragEndCallback onDragEndCallback;

    private GestureDetectorCompat mDetector;

    DragLayout dragLayout;

    Rect dragRect;

    LinearLayout mLeftLayout, mRightLayout,mBottomLayout;
    CircleLayout mCircleLayout;

    float lastX, lastY;

    //左侧拖拽Layout的矩形区域
    Rect mLeftRect;

    //右侧拖拽Layout的矩形区域
    Rect mRightRect;

    //圆形拖拽Layout的矩形区域
    Rect mCircleRect;

    //底部拖拽Layout的矩形区域
    Rect mBottomRect;

    List<DragTaget> leftRects;

    List<DragTaget> rightRects;

    List<DragTaget> circleRects;

    List<DragTaget> bottomRects;

    AppCompatImageView dragTagetView;

    AppCompatTextView animView;

    View fromView;

    Rect fromRect;

    DragTaget to;

    View toView;

    double changeCorner;

    AnimatorSet exchangedAnim;

    OrmHelper ormHelper;

    public DragHelper(DragLayout dragLayout) {
        this.dragLayout = dragLayout;
        this.dragLayout.setDragHelper(this);
        //dragLayout.setOnTouchListener((v,e) -> onTouchEvent(e));
        ormHelper = OrmHelper.getHelper();
    }

    public DragHelper(DragLayout dragLayout, LinearLayout mLeftLayout, LinearLayout mRightLayout, CircleLayout mCircleLayout, LinearLayout bottomLayout) {
        this.dragLayout = dragLayout;
        this.mLeftLayout = mLeftLayout;
        this.mRightLayout = mRightLayout;
        this.mCircleLayout = mCircleLayout;
        this.mBottomLayout = bottomLayout;
        this.dragLayout.setDragHelper(this);

        ormHelper = OrmHelper.getHelper();
        mDetector =  new GestureDetectorCompat(dragLayout.getContext(),this);
    }

    public void setOnDragEndCallback(OnDragEndCallback onDragEndCallback) {
        this.onDragEndCallback = onDragEndCallback;
    }

    /**
     * 把拖拽的view的截图添加DragLayout中去
     *
     * @param context
     * @param view
     */
    public void addToDragLayout(Context context, View view) {
        Log.w(TAG,"addToDragLayout view = "+view);
        if (view == null) {
            return;
        }
        //更新可拖拽view的矩形区域
        getAllLayoutRects(context);
        fromView = view;
        //创建可拖拽的ImageView
        createDragView(context);
        fromView.setVisibility(View.INVISIBLE);
        //创建放大的动画
        playScaleAnim();
    }

    /**
     * 创建放大的动画
     */
    private void playScaleAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        float toValue = 1.1f;
        if(fromView instanceof ImageView){
            toValue = 2f;
        }
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(dragTagetView, "scaleX", 1f, toValue);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(dragTagetView, "scaleY", 1f, toValue);

        animatorSet.setDuration(100);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.start();
    }

    /**
     * 创建可拖拽的ImageView
     * @param context
     */
    public void createDragView(Context context){
        Bitmap bitmap = Bitmap.createBitmap(fromView.getWidth(), fromView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        fromView.draw(c);
        if (dragRect == null) {
            dragRect = new Rect();
            dragLayout.getGlobalVisibleRect(dragRect);
        }

        if (dragTagetView == null) {
            dragTagetView = new AppCompatImageView(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dragTagetView.setTranslationZ(1);
            }

            // TODO: 2017/11/16 设置Alpha会导致性能问题
            dragTagetView.setAlpha(0.5f);

        }
        dragTagetView.setImageBitmap(bitmap);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(fromView.getWidth(), fromView.getHeight());
        fromRect = new Rect();
        fromView.getGlobalVisibleRect(fromRect);
        //添加到dragLayout
        dragLayout.addView(dragTagetView, params);
        dragTagetView.setTranslationX(fromRect.left);
        dragTagetView.setTranslationY(fromRect.top - dragRect.top);
    }

    /**
     * 更新可拖拽view的矩形区域
     */
    public void getAllLayoutRects(Context context) {
        Rect leftRect = new Rect();
        mLeftLayout.getGlobalVisibleRect(leftRect);
        if (leftRect.equals(mLeftRect) && changeCorner != mCircleLayout.getChangeCorner()) {
            circleRects =getRectsByData(context,mCircleLayout,Stream.of(ormHelper.getCircleDragItem()));
        } else {
            Rect rightRect = new Rect();
            mRightLayout.getGlobalVisibleRect(rightRect);
            Rect circleRect = new Rect();
            mCircleLayout.getGlobalVisibleRect(circleRect);
            Rect bottomRect = new Rect();
            mBottomLayout.getGlobalVisibleRect(bottomRect);

            mLeftRect = leftRect;
            mRightRect = rightRect;
            mCircleRect = circleRect;
            mBottomRect = bottomRect;

            leftRects =getRectsByData(context,mLeftLayout,Stream.of(ormHelper.getLeftDragItem()));
            rightRects =getRectsByData(context,mRightLayout,Stream.of(ormHelper.getRightDragItem()));
            circleRects =getRectsByData(context,mCircleLayout,Stream.of(ormHelper.getCircleDragItem()));
            bottomRects = getRectsByData(context,mBottomLayout,Stream.of(ormHelper.getBottomDragItem()));
        }
        changeCorner = mCircleLayout.getChangeCorner();
    }

    /**
     * 根据数据获取可拖拽view的矩形区域
     * @param context
     * @param parent
     * @param stream
     * @return
     */
    public List<DragTaget> getRectsByData(Context context, ViewGroup parent, Stream<DragItemEntity> stream) {
        return stream.map(item -> {
                    int id = Utils.getResId(context, "id", item.getKey());
                    View view = parent.findViewById(id);
                    Rect rect = new Rect();
                    view.getGlobalVisibleRect(rect);
                    return new DragTaget(id, rect);
                }).collect(toList());
    }

    public void dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_UP ||ev.getAction() == MotionEvent.ACTION_CANCEL){
            endDrag(ev);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float rawX = ev.getRawX();
        float rawY = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.w(TAG, "onInterceptTouchEvent ACTION_DOWN rawX = " + rawX + ", rawY = " + rawY);
                break;
            case MotionEvent.ACTION_MOVE:
                lastX = rawX;
                lastY = rawY;
                Log.w(TAG, "onInterceptTouchEvent ACTION_MOVE rawX = " + rawX + ", rawY = " + rawY);
                break;
            case MotionEvent.ACTION_UP:
                Log.w(TAG, "onInterceptTouchEvent ACTION_UP rawX = " + rawX + ", rawY = " + rawY);
                //endDrag(ev);
                break;
            default:
                break;
        }
        return fromView != null;
    }

    public boolean onTouchEvent(MotionEvent e) {
        if (fromView == null) {
            return false;
        }
        if(e.getAction() == MotionEvent.ACTION_DOWN){
            lastX = e.getRawX();
            lastY = e.getRawY();
        }
        if(e.getAction() == MotionEvent.ACTION_UP){
            endDrag(e);
        }
        return mDetector.onTouchEvent(e);
    }

    /**
     * 判断x、y坐标是否在这些矩形区域内
     * @param x
     * @param y
     */
    private void checkRect(final int x, final int y) {
        if(fromRect.contains(x,y)){
            updateToView();
            toView = null;
            return;
        }
        if (mLeftRect.contains(x, y)) {
            checkRect(x, y, leftRects, mLeftLayout);
        } else if (mRightRect.contains(x, y)) {
            checkRect(x, y, rightRects, mRightLayout);
        } else if (mCircleRect.contains(x, y)) {
            checkRect(x, y, circleRects, mCircleLayout);
        } else {
            updateToView();
            toView = null;
        }
    }

    /**
     * 判断x、y坐标是否在这些矩形区域内
     * @param x
     * @param y
     * @param rects
     * @param layout
     */
    private void checkRect(final int x, final int y, List<DragTaget> rects, ViewGroup layout) {
        for (DragTaget taget : rects) {
            if (taget.rect.contains(x, y)) {
                View view = layout.findViewById(taget.id);
                if (toView == null || toView != view) {
                    playMoveAnim(view,taget.rect);
                    toView = view;
                    toView.setVisibility(View.INVISIBLE);
                }
                return;
            }
        }
        updateToView();
        toView = null;
    }

    /**
     * 播放交换动画
     * @param view
     * @param rect
     */
    private void playMoveAnim(View view,Rect rect){
        if(toView != view){
            updateToView();
            createAnimView(view,rect);
            if(exchangedAnim == null){
                exchangedAnim = new AnimatorSet();
            }
            ObjectAnimator translationX= ObjectAnimator.ofFloat(animView, "translationX", fromRect.left);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(animView, "translationY", fromRect.top - dragRect.top);

            exchangedAnim.setDuration(150);
            exchangedAnim.setInterpolator(new AccelerateInterpolator());
            exchangedAnim.play(translationX).with(translationY);
            exchangedAnim.start();
        }
    }

    /**
     * 创建拖拽动画的目标view
     * @param view
     * @param rect
     */
    private void createAnimView(View view,Rect rect){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(fromView.getWidth(), fromView.getHeight());
        DragItemEntity item = (DragItemEntity) view.getTag();
        animView = new AppCompatTextView(fromView.getContext());
        if(fromView instanceof DragButton){
            animView.setGravity(Gravity.CENTER);
            animView.setText(item.getTitle());
            animView.setBackground(fromView.getBackground());
        }else {
            animView.setBackgroundResource(Utils.getResId(fromView.getContext(),"drawable",item.getIconName()));
        }

        dragLayout.addView(animView,params);
        animView.setTranslationX(rect.left);
        animView.setTranslationY(rect.top - dragRect.top);
    }

    /**
     * 取消交换的动画
     */
    private void cleanExchangeAnim(){
        if(exchangedAnim != null && exchangedAnim.isRunning()){
            exchangedAnim.cancel();
        }
        if(animView != null){
            dragLayout.removeView(animView);
            animView = null;
        }
    }


    /**
     * 重置toView的状态
     */
    private void updateToView() {
        if (toView != null) {
            toView.setVisibility(View.VISIBLE);
        }
        cleanExchangeAnim();
    }

    /*private void updateFromView(DragItemEntity item) {
        if (fromView instanceof CheckedTextView) {
            fromView.setBackgroundResource(Utils.getResId(fromView.getContext(), "drawable", item.getIconName()));
        } else {
            fromView.setText(item.getTitle());
        }
    }*/

    private void updateView(DragItemEntity item, View view) {
        if (view instanceof TextView) {
            ((TextView)view).setText(item.getTitle());

        } else {
            view.setBackgroundResource(Utils.getResId(view.getContext(), "drawable", item.getIconName()));
        }
    }

    /**
     * up事件停止拖拽
     * 更新数据
     * @param e
     */
    private void endDrag(MotionEvent e) {
        Log.w(TAG, "endDrag fromView = " + fromView);
        if(fromView == null){
            return;
        }
        DragItemEntity fromItem = (DragItemEntity) fromView.getTag();
        //清除拖拽view的状态
        cleanDragViewStatus();
        if (toView == null) {
            updateView(fromItem,fromView);
        } else {
            DragItemEntity toItem = (DragItemEntity) toView.getTag();
            updateToView();
            //交换内存数据和显示
            fromView.setTag(toItem);
            toView.setTag(fromItem);
            updateView(toItem, fromView);
            updateView(fromItem, toView);
            //交换数据库数据
            ormHelper.changeDragItem(fromItem,toItem);
            if(onDragEndCallback != null){
                onDragEndCallback.onDragEnd();
            }
        }
        fromView.setVisibility(View.VISIBLE);
        fromView = null;
    }

    /**
     * 清除拖拽view的状态
     */
    private void cleanDragViewStatus(){
        dragLayout.removeView(dragTagetView);
        dragTagetView.setImageBitmap(null);
        dragTagetView.setTranslationX(0f);
        dragTagetView.setTranslationY(0f);
        dragTagetView.setScaleX(1f);
        dragTagetView.setScaleY(1f);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e, float distanceX, float distanceY) {
        float rawX = e.getRawX();
        float rawY = e.getRawY();

        if (fromView == null) {
            return false;
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = rawX;
                lastY = rawY;
                Log.w(TAG, "onTouchEvent ACTION_DOWN rawX = " + rawX + ", rawY = " + rawY);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.w(TAG, "onTouchEvent ACTION_MOVE rawX = " + rawX + ", rawY = " + rawY);
                float dx = rawX - lastX;
                float dy = rawY - lastY;
                dragTagetView.setTranslationX(dragTagetView.getTranslationX() + dx);
                dragTagetView.setTranslationY(dragTagetView.getTranslationY() + dy);
                lastX = rawX;
                lastY = rawY;
                checkRect((int) rawX, (int) rawY);
                break;
            case MotionEvent.ACTION_UP:
                Log.w(TAG, "onTouchEvent ACTION_UP rawX = " + rawX + ", rawY = " + rawY);
                endDrag(e);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }
}
