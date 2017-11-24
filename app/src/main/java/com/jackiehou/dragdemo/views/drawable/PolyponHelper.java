package com.jackiehou.dragdemo.views.drawable;

import android.graphics.Path;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/22 17:52
 ************************************************************/

public class PolyponHelper {
    //多边形的比例
    private static float PERCENT = 1;

    private static List<Pair<Float,Float>> pairList = new ArrayList<>();

    private static Pair<Float,Float> fristPair = new Pair<Float,Float>(43f,19f);

    static {

        pairList.add(new Pair<Float,Float>(296f,19f));
        pairList.add(new Pair<Float,Float>(314f,34f));
        pairList.add(new Pair<Float,Float>(422f,34f));
        pairList.add(new Pair<Float,Float>(457f,19f));
        pairList.add(new Pair<Float,Float>(707f,19f));
        pairList.add(new Pair<Float,Float>(721f,34f));
        pairList.add(new Pair<Float,Float>(721f,165f));
        pairList.add(new Pair<Float,Float>(704f,187f));
        pairList.add(new Pair<Float,Float>(704f,263f));
        pairList.add(new Pair<Float,Float>(640f,324f));
        pairList.add(new Pair<Float,Float>(104f,324f));
        pairList.add(new Pair<Float,Float>(46f,263f));
        pairList.add(new Pair<Float,Float>(46f,187f));
        pairList.add(new Pair<Float,Float>(31f,165f));
        pairList.add(new Pair<Float,Float>(31f,34f));
        pairList.add(new Pair<Float,Float>(43f,19f));
    }

    public PolyponHelper() {
    }

    public float getPercent() {
        return PERCENT;
    }

    public void setPercent(float percent) {
        if(PERCENT == percent){
            return;
        }
        PERCENT = percent;

    }

    public Path getPath(){
        Path path = new Path();
        path.moveTo(change(fristPair.first*PERCENT),change(fristPair.second*PERCENT));


        for (Pair<Float,Float> p:pairList) {
            path.lineTo(p.first*PERCENT,p.second*PERCENT);
        }
        path.close();
        return path;
    }

    private float change(float x){

        return x*PERCENT;
    }


}
