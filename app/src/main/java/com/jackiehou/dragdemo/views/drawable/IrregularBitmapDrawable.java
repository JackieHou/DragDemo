package com.jackiehou.dragdemo.views.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.io.InputStream;

/************************************************************
 * Created by houjie
 * Description:     // 模块描述
 * Date: 2017/11/22 17:39
 ************************************************************/

public class IrregularBitmapDrawable extends BitmapDrawable {


    public IrregularBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    public IrregularBitmapDrawable(Resources res, String filepath) {
        super(res, filepath);
    }

    public IrregularBitmapDrawable(Resources res, InputStream is) {
        super(res, is);
    }
}
