package com.jackiehou.dragdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jackiehou.dragdemo.MyApp;
import com.jackiehou.dragdemo.greendao.DaoMaster;
import com.jackiehou.dragdemo.greendao.DaoSession;
import com.jackiehou.dragdemo.greendao.DragItemEntity;
import com.jackiehou.dragdemo.greendao.DragItemEntityDao;

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
        //这里的key 还有图片名称都不能修改，res里面的图片名称也不能修改，要不然会找不到图片
        ArrayList<DragItemEntity> list = new ArrayList<DragItemEntity>();
        list.add(new DragItemEntity("img1","svg_oil_value","今日油价",DragItemEntity.CIRCLE_TYPE));
        list.add(new DragItemEntity("img2","svg_weather","天气",DragItemEntity.CIRCLE_TYPE));
        list.add(new DragItemEntity("img3","svg_daijia","代驾服务",DragItemEntity.CIRCLE_TYPE));
        list.add(new DragItemEntity("img4","svg_drage","故障救援",DragItemEntity.CIRCLE_TYPE));
        list.add(new DragItemEntity("img5","svg_value","爱车估值",DragItemEntity.CIRCLE_TYPE));
        list.add(new DragItemEntity("img6","svg_oil","加油服务",DragItemEntity.CIRCLE_TYPE));

        list.add(new DragItemEntity("left_btn1","svg_ubi","UBI车险",DragItemEntity.LEFT_TYPE));
        list.add(new DragItemEntity("left_btn2","svg_interactivity","互动",DragItemEntity.LEFT_TYPE));
        list.add(new DragItemEntity("left_btn3","svg_break_rules","违章查询",DragItemEntity.LEFT_TYPE));

        list.add(new DragItemEntity("right_btn1","svg_car_insurance","车险服务",DragItemEntity.RIGHT_TYPE));
        list.add(new DragItemEntity("right_btn2","svg_trail","遁迹",DragItemEntity.RIGHT_TYPE));
        list.add(new DragItemEntity("right_btn3","svg_fm","FM",DragItemEntity.RIGHT_TYPE));

        list.add(new DragItemEntity("bottom_btn1","svg_driving_recorder","行车记录仪",DragItemEntity.BOTTOM_TYPE));
        list.add(new DragItemEntity("bottom_btn2","svg_driving_score","驾驶评分",DragItemEntity.BOTTOM_TYPE));

        DragItemEntityDao dragItemEntityDao = daoSession.getDragItemEntityDao();
        dragItemEntityDao.insertInTx(list);
    }

    /**
     *做了update 之后需要清除数据，要不然获取的数据一直是以前缓存的数据
     */
    public void cleanCache(){
        daoSession.clear();
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

    /**
     * 交换两个id的数据
     * @param fromItem
     * @param toItem
     */
    public void changeDragItem(DragItemEntity fromItem,DragItemEntity toItem){
        DragItemEntity fromEntity = new DragItemEntity(fromItem.getKey(),toItem.getIconName(),toItem.getTitle(),fromItem.getType());
        DragItemEntity toEntity = new DragItemEntity(toItem.getKey(),fromItem.getIconName(),fromItem.getTitle(),toItem.getType());
        daoSession.getDragItemEntityDao().updateInTx(fromEntity,toEntity);
        daoSession.clear();
    }



}
