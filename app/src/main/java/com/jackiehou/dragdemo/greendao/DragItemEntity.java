package com.jackiehou.dragdemo.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/16 10:52
 ************************************************************/
@Entity
public class DragItemEntity {

    public static final int CIRCLE_TYPE = 0;

    public static final int LEFT_TYPE = 1;

    public static final int RIGHT_TYPE = 2;

    public static final int BOTTOM_TYPE = 3;

    @Id
    String key;

    String iconName;

    String title;

    int type;

    @Generated(hash = 1012702848)
    public DragItemEntity(String key, String iconName, String title, int type) {
        this.key = key;
        this.iconName = iconName;
        this.title = title;
        this.type = type;
    }

    @Generated(hash = 1656606708)
    public DragItemEntity() {
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIconName() {
        return this.iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }






    

    
}
