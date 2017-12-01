package com.jackiehou.assistive.helper;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.jackiehou.assistive.widget.AssistiveView;
import com.jackiehou.assistive.widget.NaviBarView;
import com.jackiehou.dragdemo.MyApp;
import com.jackiehou.dragdemo.R;
import com.jackiehou.dragdemo.entity.FloatCoords;

import io.paperdb.Paper;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/12/1 14:53
 ************************************************************/

public class AssistiveHelper {

    public static final String TAG = "AssistiveHelper";

    public static final String KEY_COORDS = "KEY_COORDS";

    public static final float FLOAT_PERCENT = 0.145f;

    int screenWidth;

    private View mFloatView;

    private NaviBarView mNaviBarView;

    private int mTouchSlop;


    int contentWidth;
    int contentHeigth;
    int naviHeight;

    private int holeX;
    private int holeY;


    public void init(final Context context,final ViewGroup contentView){
        screenWidth = MyApp.getInstance().getWidthPixels();
        naviHeight = (int)(screenWidth*NaviBarView.PERCENT);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        contentView.post(() ->initView(context,contentView));
    }

    public void initView(Context context,ViewGroup contentView){
        initSize(contentView);
        createNavBar(context,contentView);
        createFloatView(context,contentView);
    }



    public void initSize(ViewGroup contentView){
        contentWidth = contentView.getMeasuredWidth();
        contentHeigth = contentView.getMeasuredHeight();
        holeX = (int) (contentWidth*(1-FLOAT_PERCENT)/2f);
        holeY = (int) (contentHeigth-contentWidth*FLOAT_PERCENT);
    }

    public void showNavBarAndAstBall(Context context,ViewGroup contentView){
        boolean isInNav = false;
        FloatCoords floatCoords = Paper.book().read(KEY_COORDS);
        if(floatCoords == null){
            floatCoords = new FloatCoords(holeX,holeY);
            isInNav = true;
        }
        if(mNaviBarView == null){
            createNavBar(context,contentView);
        }
        if(mFloatView == null){
            createFloatView(context,contentView);
        }
        mFloatView.setTranslationX(floatCoords.getX());
        mFloatView.setTranslationY(floatCoords.getY());
    }


    public Pair<Integer,Integer> getContentSize(){
        return new Pair<>(contentWidth,contentHeigth);
    }

    public Pair<Integer,Integer> getNavHolePos(){
        return new Pair<>(holeX,holeY);
    }

    public int getTouchSlop(){
        return mTouchSlop;
    }

    public void onStart(){

    }

    public void onResume(){

    }

    public void onPause(){

    }

    public void onStop(){

    }

    private void createFloatView(Context context,ViewGroup contentView){
        mFloatView = new AssistiveView(context,this);
        mFloatView.setBackgroundResource(R.mipmap.ball_tool);
        int width = (int) (FLOAT_PERCENT*screenWidth);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,width);
        contentView.addView(mFloatView,params);
        //FloatCoords floatCoords = getFloatCoords();

    }

    private void createNavBar(Context context, ViewGroup contentView) {
        mNaviBarView = (NaviBarView) LayoutInflater.from(context).inflate( R.layout.navi_bar_view,contentView,false);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(contentWidth,naviHeight);
        contentView.addView(mNaviBarView,params);
        mNaviBarView.setTranslationX(0);
        mNaviBarView.setTranslationY(contentHeigth-naviHeight);

    }

    public FloatCoords getFloatCoords() {
        return Paper.book().read(KEY_COORDS,new FloatCoords(holeX,holeY));
    }



    public boolean isFloatInNavi(){
        if(contentWidth == 0 || contentHeigth == 0 || holeX == 0 || holeY == 0){
            return true;
        }
        if(mFloatView != null){
            float tranX = mFloatView.getTranslationX();
            float tranY = mFloatView.getTranslationY();
            if(Math.abs(holeX-tranX) < 2f && tranY >= holeY){
                return true;
            }
            return false;
        }
        return true;
    }

}
