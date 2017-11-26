package com.jackiehou.dragdemo.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jackiehou.dragdemo.MyApp;
import com.jackiehou.dragdemo.entity.DaoMaster;
import com.jackiehou.dragdemo.entity.DaoSession;
import com.jackiehou.dragdemo.entity.DragItemEntity;
import com.jackiehou.dragdemo.entity.DragItemEntityDao;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JackieHou on 2017/11/24.
 * sqlite升级
 * https://www.cnblogs.com/liqw/p/4264925.html
 * greendao配置/升级
 * http://www.jianshu.com/p/793f77feeb89
 */

public class OrmHelper extends DaoMaster.OpenHelper{

    public static final String TAG = "OrmHelper";

    public static final boolean ENCRYPTED = false;

    public static final String TABLE_NAME = ENCRYPTED ? "drag-db-encrypted" :"drag-db";

    private static OrmHelper ormHelper;

    private DaoSession daoSession;

    private OrmHelper(Context context) {
        super(context, TABLE_NAME);
    }

    private OrmHelper(Context context,SQLiteDatabase.CursorFactory factory) {
        super(context, TABLE_NAME,factory);
    }

    public static OrmHelper getHelper(Context context) {
        synchronized (OrmHelper.class) {
            if (ormHelper == null) {
                ormHelper = new OrmHelper(context);
            }
        }
        return ormHelper;
    }

    public static OrmHelper getHelper() {
        return getHelper(MyApp.getInstance());
    }

    public void init() {
        //greendao init
        Database db = ENCRYPTED ? getEncryptedWritableDb("super-secret") : getWritableDb();

        //DaoMaster daoMaster = new DaoMaster(db);
        daoSession = new DaoMaster(db).newSession(IdentityScopeType.Session);
        Log.w(TAG,"init");
        saveDefaultData();
    }

    @Override
    public void onCreate(Database db) {

        Log.i(TAG, "Creating tables for schema version " + DaoMaster.SCHEMA_VERSION);
        DaoMaster.createAllTables(db, true);

        //saveDefaultData();
    }

    /**
     * 数据库更新
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    // TODO: 2017/11/26 这里应该配置到xml里面的
    public void saveDefaultData(){
        long count = daoSession.getDragItemEntityDao().count();
        if(count > 0){
            return;
        }
        ArrayList<DragItemEntity> list = new ArrayList<DragItemEntity>();
        list.add(new DragItemEntity("img1","auto_bright_selector","今日油价",DragItemEntity.CIRCLE_TYPE));
        list.add(new DragItemEntity("img2","bluetooth_selector","天气",DragItemEntity.CIRCLE_TYPE));
        list.add(new DragItemEntity("img3","dnd_selector","代驾服务",DragItemEntity.CIRCLE_TYPE));
        list.add(new DragItemEntity("img4","network_selector","故障救援",DragItemEntity.CIRCLE_TYPE));
        list.add(new DragItemEntity("img5","wifi_selector","爱车估值",DragItemEntity.CIRCLE_TYPE));
        list.add(new DragItemEntity("img6","flashlight_selector","加油服务",DragItemEntity.CIRCLE_TYPE));

        list.add(new DragItemEntity("left_btn1","ic_calculator","UBI车险",DragItemEntity.LEFT_TYPE));
        list.add(new DragItemEntity("left_btn2","ic_close","互动",DragItemEntity.LEFT_TYPE));
        list.add(new DragItemEntity("left_btn3","ic_expand_more","违章查询",DragItemEntity.LEFT_TYPE));

        list.add(new DragItemEntity("right_btn1","ic_qs_dnd_on","车险服务",DragItemEntity.RIGHT_TYPE));
        list.add(new DragItemEntity("right_btn2","ic_qs_signal_4g","遁迹",DragItemEntity.RIGHT_TYPE));
        list.add(new DragItemEntity("right_btn3","ic_qs_vpn","FM",DragItemEntity.RIGHT_TYPE));

        list.add(new DragItemEntity("bottom_btn1","clound_selector","行车记录仪",DragItemEntity.BOTTOM_TYPE));
        list.add(new DragItemEntity("bottom_btn2","ic_launcher_background","驾驶评分",DragItemEntity.BOTTOM_TYPE));

        DragItemEntityDao dragItemEntityDao = daoSession.getDragItemEntityDao();
        dragItemEntityDao.insertInTx(list);
    }

    public List<DragItemEntity> getAllDragItem(){
        return daoSession.getDragItemEntityDao().queryBuilder().list();
    }

    public List<DragItemEntity> getCircleDragItem(){
        return daoSession.getDragItemEntityDao()
                .queryBuilder()
                .where(DragItemEntityDao.Properties.Type.eq(DragItemEntity.CIRCLE_TYPE))
                .list();
    }

    public List<DragItemEntity> getLeftDragItem(){
        return daoSession.getDragItemEntityDao()
                .queryBuilder()
                .where(DragItemEntityDao.Properties.Type.eq(DragItemEntity.LEFT_TYPE))
                .list();
    }

    public List<DragItemEntity> getRightDragItem(){
        return daoSession.getDragItemEntityDao()
                .queryBuilder()
                .where(DragItemEntityDao.Properties.Type.eq(DragItemEntity.RIGHT_TYPE))
                .list();
    }

    public List<DragItemEntity> getBottomDragItem(){
        return daoSession.getDragItemEntityDao()
                .queryBuilder()
                .where(DragItemEntityDao.Properties.Type.eq(DragItemEntity.BOTTOM_TYPE))
                .list();
    }


}
