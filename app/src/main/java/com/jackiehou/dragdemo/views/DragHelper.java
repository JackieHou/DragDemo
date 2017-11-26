package com.jackiehou.dragdemo.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.jackiehou.dragdemo.Utils;
import com.jackiehou.dragdemo.entity.DragItemEntity;
import com.jackiehou.dragdemo.entity.DragTaget;
import com.jackiehou.dragdemo.manager.OrmHelper;

import java.util.List;

import static com.annimon.stream.Collectors.toList;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/16 13:26
 ************************************************************/

public class DragHelper implements GestureDetector.OnGestureListener{

    public static final String TAG = DragLayout.class.getSimpleName();

    private GestureDetectorCompat mDetector;

    DragLayout dragLayout;

    Rect dragRect;

    LinearLayout mLeftLayout, mRightLayout;
    CircleLayoutPlanB mCircleLayout;

    float lastX, lastY;

    //左侧拖拽Layout的矩形区域
    Rect mLeftRect;

    //右侧拖拽Layout的矩形区域
    Rect mRightRect;

    //圆形拖拽Layout的矩形区域
    Rect mCircleRect;

    List<DragTaget> leftRects;

    List<DragTaget> rightRects;

    List<DragTaget> circleRects;

    ImageView dragTagetView;

    TextView animView;

    TextView fromView;

    Rect fromRect;

    DragTaget to;

    TextView toView;

    double changeCorner;

    AnimatorSet exchangedAnim;

    public DragHelper(DragLayout dragLayout) {
        this.dragLayout = dragLayout;
        this.dragLayout.setDragHelper(this);
        //dragLayout.setOnTouchListener((v,e) -> onTouchEvent(e));
    }

    public DragHelper(DragLayout dragLayout, LinearLayout mLeftLayout, LinearLayout mRightLayout, CircleLayoutPlanB mCircleLayout) {
        this.dragLayout = dragLayout;
        this.mLeftLayout = mLeftLayout;
        this.mRightLayout = mRightLayout;
        this.mCircleLayout = mCircleLayout;
        this.dragLayout.setDragHelper(this);

        mDetector =  new GestureDetectorCompat(dragLayout.getContext(),this);
    }

    public void setmLeftRect(Rect mLeftRect) {
        this.mLeftRect = mLeftRect;
    }

    public void setmRightRect(Rect mRightRect) {
        this.mRightRect = mRightRect;
    }

    public void setmCircleRect(Rect mCircleRect) {
        this.mCircleRect = mCircleRect;
    }

    public void setLeftRects(List<DragTaget> leftRects) {
        this.leftRects = leftRects;
    }

    public void setRightRects(List<DragTaget> rightRects) {
        this.rightRects = rightRects;
    }

    public void setCircleRects(List<DragTaget> circleRects) {
        this.circleRects = circleRects;
    }


    /**
     * 把拖拽的view的截图添加DragLayout中去
     *
     * @param context
     * @param view
     */
    public void addToDragLayout(Context context, TextView view) {
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

    private void playScaleAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        float toValue = 1.1f;
        if(fromView instanceof CheckedTextView){
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
            dragTagetView = new ImageView(context);
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
            circleRects =getRectsByData(context,mCircleLayout,Stream.of(OrmHelper.getHelper().getCircleDragItem()));
        } else {
            Rect rightRect = new Rect();
            mRightLayout.getGlobalVisibleRect(rightRect);
            Rect circleRect = new Rect();
            mCircleLayout.getGlobalVisibleRect(circleRect);

            mLeftRect = leftRect;
            mRightRect = rightRect;
            mCircleRect = circleRect;

            leftRects =getRectsByData(context,mLeftLayout,Stream.of(OrmHelper.getHelper().getLeftDragItem()));
            rightRects =getRectsByData(context,mRightLayout,Stream.of(OrmHelper.getHelper().getRightDragItem()));
            circleRects =getRectsByData(context,mCircleLayout,Stream.of(OrmHelper.getHelper().getCircleDragItem()));
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
                endDrag(ev);
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

    private void checkRect(final int x, final int y, List<DragTaget> rects, ViewGroup layout) {
        for (DragTaget taget : rects) {
            if (taget.rect.contains(x, y)) {
                TextView view = (TextView) layout.findViewById(taget.id);
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

    private void playMoveAnim(TextView view,Rect rect){
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

    private void createAnimView(TextView view,Rect rect){

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(fromView.getWidth(), fromView.getHeight());
        DragItemEntity item = (DragItemEntity) view.getTag();
        animView = new TextView(fromView.getContext());
        if(fromView instanceof DragButton){
            animView.setGravity(Gravity.CENTER);
            animView.setText(item.getTitle());
            animView.setBackground(fromView.getBackground());
        }else {
            animView.setBackgroundResource( Utils.getResId(fromView.getContext(),"drawable",item.getIconName()));
        }

        dragLayout.addView(animView,params);
        animView.setTranslationX(rect.left);
        animView.setTranslationY(rect.top - dragRect.top);


    }

    private void cleanExchangeAnim(){
        if(exchangedAnim != null && exchangedAnim.isRunning()){
            exchangedAnim.cancel();
        }
        if(animView != null){
            dragLayout.removeView(animView);
            animView = null;
        }
    }



    private void updateToView() {
        if (toView != null) {
            toView.setVisibility(View.VISIBLE);
        }
        cleanExchangeAnim();
    }

    private void updateFromView(DragItemEntity item) {
        if (fromView instanceof CheckedTextView) {
            fromView.setBackgroundResource(Utils.getResId(fromView.getContext(), "drawable", item.getIconName()));
        } else {
            fromView.setText(item.getTitle());
        }
    }

    private void updateView(DragItemEntity item, TextView textView) {
        if (textView instanceof CheckedTextView) {
            textView.setBackgroundResource(Utils.getResId(textView.getContext(), "drawable", item.getIconName()));
        } else {
            textView.setText(item.getTitle());
        }
    }

    private void endDrag(MotionEvent e) {
        if(fromView == null){
            return;
        }
        DragItemEntity fromItem = (DragItemEntity) fromView.getTag();
        //清除拖拽view的状态
        cleanDragViewStatus();
        if (toView == null) {

            updateFromView(fromItem);

        } else {
            updateToView();
            updateFromView((DragItemEntity) toView.getTag());
            fromView.setTag(toView.getTag());
            toView.setTag(fromItem);
            updateView(fromItem, toView);
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
