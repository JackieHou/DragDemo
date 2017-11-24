package com.jackiehou.dragdemo;

import android.content.Context;

import com.jackiehou.dragdemo.entity.DragItemEntity;

import java.util.ArrayList;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/16 10:49
 ************************************************************/

public class Utils {

    public final static ArrayList<DragItemEntity> CIRCLE_ITEM_LIST = new ArrayList<DragItemEntity>();

    public final static ArrayList<DragItemEntity> LEFT_ITEM_LIST = new ArrayList<DragItemEntity>();

    public final static ArrayList<DragItemEntity> RIGHT_ITEM_LIST = new ArrayList<DragItemEntity>();

    static {
        CIRCLE_ITEM_LIST.add(new DragItemEntity("img1","auto_bright_selector","亮度"));
        CIRCLE_ITEM_LIST.add(new DragItemEntity("img2","bluetooth_selector","蓝牙"));
        CIRCLE_ITEM_LIST.add(new DragItemEntity("img3","dnd_selector","声音"));
        CIRCLE_ITEM_LIST.add(new DragItemEntity("img4","network_selector","网络"));
        CIRCLE_ITEM_LIST.add(new DragItemEntity("img5","wifi_selector","wifi"));
        CIRCLE_ITEM_LIST.add(new DragItemEntity("img6","flashlight_selector","手电筒"));

        LEFT_ITEM_LIST.add(new DragItemEntity("left_btn1","ic_calculator","亮度"));
        LEFT_ITEM_LIST.add(new DragItemEntity("left_btn2","ic_close","关闭"));
        LEFT_ITEM_LIST.add(new DragItemEntity("left_btn3","ic_expand_more","更多"));
        RIGHT_ITEM_LIST.add(new DragItemEntity("right_btn1","ic_qs_dnd_on","勿扰"));
        RIGHT_ITEM_LIST.add(new DragItemEntity("right_btn2","ic_qs_signal_4g","4G"));
        RIGHT_ITEM_LIST.add(new DragItemEntity("right_btn3","ic_qs_vpn","vpn"));
    }

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
