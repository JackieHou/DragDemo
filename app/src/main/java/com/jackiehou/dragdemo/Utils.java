package com.jackiehou.dragdemo;

import android.content.Context;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/16 10:49
 ************************************************************/

public class Utils {

    /**
     * 根据资源名称找资源id
     * @param context
     * @param name
     * @return
     */
    public static int getDrawableRes(Context context,String name){
        return context.getResources().getIdentifier(name, "drawable" ,context.getPackageName());
    }


    /**
     * 根据资源名称找资源id
     * @param context
     * @param name
     * @return
     */
    public static int getResId(Context context,String type ,String name){
        return context.getResources().getIdentifier(name, type ,context.getPackageName());
    }
}
