package com.qcec.log.crash;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qcec.log.LogDBHelper;
import com.qcec.log.analysis.AnalysisInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lorin on 16/2/4.
 * 管理所有重要保存数据的数据库需要在Application中的onCreate中初始化和在onTerminate中关闭
 */
public class CrashDataClient {

    private SQLiteDatabase db;

    private String crashTable;

    private int maxCrashNumber = 1000;

    public final static int CRASH_ITEM_COMMON = 0;
    public final static int CRASH_ITEM_UPLOADING_COMPLETE = 1;


    public CrashDataClient(LogDBHelper dbHelper) {
        this.db = dbHelper.getWritableDatabase();
        this.crashTable = LogDBHelper.TABLE_CRASH_LIST;
    }

    public void setMaxCrashNumber(int number) {
        maxCrashNumber = number;
    }

    /**
     * add InfoObject
     * 插入触发数据，设置了最大保存activateEventsMaxNumber，超过则删除最后条目
     *
     * @param infoObject
     */
    public void addCrashInfo(CrashInfoModel infoObject) {
        db.beginTransaction();  //开始事务
        try {
            checkOverCount();

            db.execSQL("INSERT INTO " + crashTable + " VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{infoObject.getHasSent(), infoObject.getUuid(), infoObject.getDate(),
                    infoObject.getApp(), infoObject.getAppv(), infoObject.getDev(), infoObject.getSys(), infoObject.getSysv(), infoObject.getCause(), infoObject.getExp(), infoObject.getStack(), infoObject.getExtra()
            });
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    private void checkOverCount() {
        List<CrashInfoModel> queryList = null;
        queryList = queryAllCrashInfos();
        if (null != queryList && queryList.size() >= maxCrashNumber) {
            deleteCrashInfoByid(queryList.get((queryList.size() - 1)).get_id());
        }

    }

    public void deleteCrashInfoByid(int id) {
        db.execSQL("delete from " + crashTable + " where _id=?", new Object[]{id});
    }

    public List<CrashInfoModel> queryAllCrashInfos() {
        Cursor cursor = db.rawQuery("SELECT * FROM " + crashTable + " order by _id desc", null);
        try {
            return getCrashInfosByCursor(cursor);
        } finally {
            cursor.close();
        }
    }


    public CrashInfoModel queryLatestCrashInfo() {
        Cursor cursor = db.rawQuery("SELECT * FROM " + crashTable + " order by _id desc limit 1", null);
        try {
            if (cursor.getCount() > 0) {
                return getCrashInfosByCursor(cursor).get(0);
            }
        } finally {
            cursor.close();
        }

        return null;

    }

    /**
     * 清空表
     */

    public void clearCrashInfos() {
        db.execSQL("delete from " + crashTable);
    }

    private List<CrashInfoModel> getCrashInfosByCursor(Cursor cursor) {
        ArrayList<CrashInfoModel> list = new ArrayList<CrashInfoModel>();
        while (cursor.moveToNext()) {
            CrashInfoModel crashInfo = new CrashInfoModel();

            crashInfo.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
            crashInfo.setHasSent(cursor.getInt(cursor.getColumnIndex("has_sent")));
            crashInfo.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
            crashInfo.setDate(cursor.getString(cursor.getColumnIndex("date")));
            crashInfo.setApp(cursor.getString(cursor.getColumnIndex("app")));
            crashInfo.setAppv(cursor.getString(cursor.getColumnIndex("apps")));
            crashInfo.setDev(cursor.getString(cursor.getColumnIndex("dev")));
            crashInfo.setSys(cursor.getString(cursor.getColumnIndex("sys")));
            crashInfo.setSysv(cursor.getString(cursor.getColumnIndex("says")));
            crashInfo.setCause(cursor.getString(cursor.getColumnIndex("cause")));
            crashInfo.setExp(cursor.getString(cursor.getColumnIndex("exp")));
            crashInfo.setStack(cursor.getString(cursor.getColumnIndex("stack")));
            crashInfo.setExtra(cursor.getString(cursor.getColumnIndex("extra")));


            list.add(crashInfo);
        }

        return list;
    }

    /**
     * 变更条目状态，设置has_sent的的值，使用规则应如下：
     * 状态0新加入或者上传失败后
     * 状态1加入上传完成后
     * @param idList
     * @param status
     */
    public void motifyMessageStatus(List<CrashInfoModel> idList,int status)
    {
        ContentValues values = new ContentValues();
        values.put("has_sent", status);

        db.beginTransaction();  //开始事务
        try {

            for (int i = 0; i < idList.size(); i++) {
                db.update(crashTable, values, "_id=?", new String[]{""+idList.get(i).get_id()});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * 获取查询所有未发送埋点数据游标
     *
     * @return Cursor
     */
    private Cursor queryCursorBySendingStatus(String status) {
        Cursor c = db.rawQuery("SELECT * FROM " + crashTable + " where has_sent = ? order by _id desc", new String[]{status});
        return c;
    }

    public List<CrashInfoModel> queryInfosBySendingStatus(String status) {
        return getCrashInfosByCursor(queryCursorBySendingStatus(status));
    }



}
