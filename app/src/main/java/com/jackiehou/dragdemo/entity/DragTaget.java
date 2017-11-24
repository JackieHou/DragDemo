package com.jackiehou.dragdemo.entity;

import android.graphics.Rect;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/16 13:53
 ************************************************************/

public class DragTaget {

    public DragTaget(int id, Rect rect) {
        this.id = id;
        this.rect = rect;
    }

    //view 的 id标识
    public int id;

    //当前可拖拽的区域矩形
    public Rect rect;
}
