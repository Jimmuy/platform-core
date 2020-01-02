package com.jimmy.log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jimmy.app.CoreApplication;


/**
 * Created by lorin on 16/2/3.
 * 崩溃日志DBHelper
 */
public class LogDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "log.db";
    private static final int DATABASE_VERSION = 5;

    //Crash表
    public static final String TABLE_CRASH_LIST = "crash_list";
    //Mark表
    public static final String TABLE_MARK_LIST = "mark_list";

    private volatile static LogDBHelper dbHelper = null;

    public static LogDBHelper getInstance() {
        if (null == dbHelper) {
            synchronized (LogDBHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new LogDBHelper(CoreApplication.get());
                }
            }
        }

        return dbHelper;
    }

    public LogDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CRASH_LIST +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, has_sent INTEGER, uuid VARCHAR, date VARCHAR, app VARCHAR, apps VARCHAR, dev VARCHAR, sys VARCHAR, says VARCHAR, cause VARCHAR, exp VARCHAR, stack VARCHAR, extra VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MARK_LIST +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, content VARCHAR, has_sent INTEGER)");
    }

    //如果DATABASE_VERSION值被增加,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //若需要升级版本，则在此处使用方法调用sql语句
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CRASH_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARK_LIST);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //若需要降级版本，则在此处使用方法调用sql语句
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CRASH_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARK_LIST);

        onCreate(db);
    }
}