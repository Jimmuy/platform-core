package com.jimmy.log.analysis;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jimmy.log.LogDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lorin on 16/3/16.
 */
public class AnalysisDataClient {

    private LogDBHelper helper;
    private SQLiteDatabase db;

    //最大存储条目
    private int markPointListMaxNumber = 1000;
    public final static int ANALYSIS_ITEM_COMMON = 0;
    public final static int ANALYSIS_ITEM_UPLOADING = 1;
    public final static int ANALYSIS_ITEM_UPLOADING_COMPLETE = 2;


    private String markTable;

    public void setMarkListMaxLimit(int MarkListMax) {
        markPointListMaxNumber = MarkListMax;
    }

    public AnalysisDataClient() {

    }

    public AnalysisDataClient(LogDBHelper dbHelper, int max) {
        this.helper = dbHelper;
        this.db = helper.getWritableDatabase();
        this.markPointListMaxNumber = max;
        this.markTable = helper.TABLE_MARK_LIST;
    }

    /**
     * 插入新数据
     *
     * @param content
     */
    public void addMarkMessage(String content) {

        db.beginTransaction();  //开始事务
        try {
            checkOverCount();
            db.execSQL("INSERT INTO " + markTable + " VALUES(null, ?, ?)", new Object[]{content, 0});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }

    }

    /**
     * 设置了最大保存MarkListMaxNumber，超过则删除最后条目
     */
    private void checkOverCount() {
        ArrayList<AnalysisInfo> queryList = null;
        queryList = queryAllUnsentMessages();//查询所有Activate的数据
        if (null != queryList && queryList.size() >= markPointListMaxNumber) {

            deleteMarkMessageByid(queryList.get((queryList.size() - 1)).get_id());
        }

    }

    public void deleteMarkMessagesByid(List<AnalysisInfo> idList) {


        db.beginTransaction();  //开始事务
        try {

            for (int i = 0; i < idList.size(); i++) {
                deleteMarkMessageByid(idList.get(i).get_id());
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }


    }

    public void deleteMarkMessageByid(int id) {

        db.execSQL("delete from " + markTable + " where _id=?",
                new Object[]{id});

    }

    public void clearMarkTable() {
        db.execSQL("delete from " + markTable);
    }

    /**
     * 变更条目状态，设置has_sent的的值，使用规则应如下：
     * 状态0新加入或者上传失败后
     * 状态1上传过程中
     * 状态2传完成，但一般会执行删除条目操作，所以常态下不会出现
     * @param idList
     * @param status
     */
    public void motifyMessageStatus(List<AnalysisInfo> idList, int status) {
        ContentValues values = new ContentValues();
        values.put("has_sent", status);

        db.beginTransaction();  //开始事务
        try {

            for (int i = 0; i < idList.size(); i++) {
                db.update(markTable, values, "_id=?", new String[]{"" + idList.get(i).get_id()});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    public ArrayList<AnalysisInfo> queryAllUnsentMessages() {
        return queryEventsByType(queryAllUnsentCursor());
    }

    public ArrayList<AnalysisInfo> queryAllMessages() {
        return queryEventsByType(queryAllCursor());
    }

    /**
     * 根据游标信息返回组装返回数据列表
     *
     * @return List<infoObject>
     */
    public ArrayList<AnalysisInfo> queryEventsByType(Cursor cursor) {
        ArrayList<AnalysisInfo> infoObjects = new ArrayList<AnalysisInfo>();
        while (cursor.moveToNext()) {
            AnalysisInfo analysisInfo = new AnalysisInfo();

            analysisInfo.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
            analysisInfo.setContent(cursor.getString(cursor.getColumnIndex("content")));
            analysisInfo.setHasSent(cursor.getInt(cursor.getColumnIndex("has_sent")));

            infoObjects.add(analysisInfo);
        }
        cursor.close();
        return infoObjects;
    }

    /**
     * 获取mark_list表中所有存在的数据游标
     *
     * @return Cursor
     */
    private Cursor queryAllCursor() {
        Cursor c = db.rawQuery("SELECT * FROM " + markTable + " order by _id desc", null);
        return c;
    }

    /**
     * 获取查询所有未发送埋点数据游标
     *
     * @return Cursor
     */
    private Cursor queryAllUnsentCursor() {
        Cursor c = db.rawQuery("SELECT * FROM " + markTable + " where has_sent = ? order by _id desc", new String[]{"0"});
        return c;
    }

    public AnalysisInfo queryLastMarkMessage() {

        Cursor cursor = queryInfosByDESCCursor(1);
        AnalysisInfo MarkPointInfo = null;

        if (cursor.getCount() > 0) {
            MarkPointInfo = queryEventsByType(cursor).get(0);
        }
        cursor.close();
        return MarkPointInfo;

    }

    /**
     * 倒叙返回指定数量的埋点数据游标
     *
     * @return Cursor
     */
    private Cursor queryInfosByDESCCursor(int count) {
        Cursor c = db.rawQuery("SELECT * FROM " + markTable + " order by _id desc limit " + count, null);
        return c;
    }

}
