package com.jackiehou.dragdemo.entity;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/16 10:52
 ************************************************************/

public class DragItemEntity {

    String key;

    String iconStr;

    String title;

    public DragItemEntity(String key, String iconStr, String title) {
        this.key = key;
        this.iconStr = iconStr;
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIconStr() {
        return iconStr;
    }

    public void setIconStr(String iconStr) {
        this.iconStr = iconStr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
