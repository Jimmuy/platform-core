package com.jimmy.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.reflect.TypeToken;
import com.jimmy.datamodel.GsonConverter;
import com.jimmy.dataservice.base.CacheStrategy;
import com.jimmy.dataservice.request.ApiRequest;
import com.jimmy.dataservice.response.ApiResponse;
import com.jimmy.dataservice.response.BasicApiResponse;
import com.jimmy.utils.PreferenceUtils;

import java.util.Map;

/**
 * API request cache keyed by a ApiRequest with a ApiResponse as data.
 * Use SQLite as disk cache keyed by request url.
 *
 * Created by chen on 15/3/17.
 */
public class RequestCache implements Cache<ApiRequest, ApiResponse> {

    private Context context;
    private static RequestCache requestCache;
    private RequestCacheDBHelper requestCacheDBHelper;

    public static RequestCache getInstance(Context context) {
        if (requestCache == null) {
            requestCache = new RequestCache(context);
        }
        return requestCache;
    }

    private RequestCache(Context context) {
        this.context = context;
        requestCacheDBHelper = new RequestCacheDBHelper(context);
        requestCacheDBHelper.getWritableDatabase();
    }

    @Override
    public synchronized boolean put(ApiRequest key, ApiResponse value) {
        Cursor cursor = requestCacheDBHelper.getRequestCache(key);
        if (cursor != null && cursor.getCount() > 0) {
            requestCacheDBHelper.updateRequestCache(key, value);
        } else {
            requestCacheDBHelper.addRequestCache(key, value);
        }
        return true;
    }

    @Override
    public synchronized ApiResponse get(ApiRequest key) {
        Cursor cursor = requestCacheDBHelper.getRequestCache(key);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (requestCacheDBHelper.checkCacheAvailableStatus(cursor)) {
                    Map<String, String> headers = GsonConverter.decode(cursor.getString(0),
                            new TypeToken<Map<String, String>>(){}.getType());
                    byte[] result = cursor.getBlob(1);
                    BasicApiResponse apiResponse = new BasicApiResponse(200, headers, result, null, true);
                    return apiResponse;
                } else {
                    requestCacheDBHelper.deleteRequestCache(key);
                }
            }
            return null;
        }finally{
            if(cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public synchronized void remove(ApiRequest key) {
        requestCacheDBHelper.deleteRequestCache(key);
    }

    @Override
    public synchronized void clear() {
        requestCacheDBHelper.clearRequestCache();
    }

    private class RequestCacheDBHelper extends SQLiteOpenHelper {
        private static final String REQUEST_CACHE_DATABASE_NAME = "REQUEST_CACHE_DB";
        private static final String REQUEST_CACHE_TABLE = "request_cache";
        private static final String KEY_URL = "url";
        private static final String KEY_HEADER = "header";
        private static final String KEY_RESPONSE = "response";
        private static final String KEY_STRATEGY = "strategy";
        private static final String KEY_TIMESTAMP = "timestamp";


        private static final int DATABASE_VERSION = 2;

        private static final String CACHE_TABLE_CREATE = "CREATE TABLE " + REQUEST_CACHE_TABLE + "("
                + KEY_URL + " STRING PRIMARY KEY," + KEY_HEADER + " STRING," + KEY_RESPONSE + " VARBINARY,"
                + KEY_STRATEGY + " INTEGER," + KEY_TIMESTAMP + " LONG" + ")";

        public RequestCacheDBHelper(Context context) {
            super(context, REQUEST_CACHE_DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CACHE_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + REQUEST_CACHE_TABLE);
            PreferenceUtils.clearPreference(context, null);

            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + REQUEST_CACHE_TABLE);
            PreferenceUtils.clearPreference(context, null);

            onCreate(db);
        }

        public void addRequestCache(ApiRequest apiRequest, ApiResponse apiResponse) {
            SQLiteDatabase db =  this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_URL, apiRequest.getCacheKey());
            values.put(KEY_HEADER, GsonConverter.toJson(apiResponse.getHeaders()));
            values.put(KEY_RESPONSE, apiResponse.getResult());
            values.put(KEY_STRATEGY, apiRequest.getCacheStrategy());
            values.put(KEY_TIMESTAMP, System.currentTimeMillis());
            db.insert(REQUEST_CACHE_TABLE, null, values);
        }

        public Cursor getRequestCache(ApiRequest apiRequest) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(
                    REQUEST_CACHE_TABLE,
                    new String[]{KEY_HEADER, KEY_RESPONSE, KEY_STRATEGY, KEY_TIMESTAMP},
                    KEY_URL + "= ?",
                    new String[]{apiRequest.getCacheKey()},
                    null,
                    null,
                    null
            );
            return cursor;
        }

        public void updateRequestCache(ApiRequest apiRequest, ApiResponse apiResponse) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_HEADER, GsonConverter.toJson(apiResponse.getHeaders()));
            values.put(KEY_RESPONSE, apiResponse.getResult());
            values.put(KEY_STRATEGY, apiRequest.getCacheStrategy());
            values.put(KEY_TIMESTAMP, System.currentTimeMillis());
            db.update(REQUEST_CACHE_TABLE, values, KEY_URL + " = ?", new String[]{ apiRequest.getCacheKey()});
        }

        public void deleteRequestCache(ApiRequest apiRequest) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(REQUEST_CACHE_TABLE,
                    KEY_URL + "= ?",
                    new String[]{apiRequest.getCacheKey()}
                    );
        }

        public void clearRequestCache() {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + REQUEST_CACHE_TABLE);
        }

        public boolean checkCacheAvailableStatus(Cursor cursor) {
            long availableTime;
            switch (cursor.getInt(2)) {
                case CacheStrategy.NORMAL:
                    availableTime = 5 * 60 * 1000;
                    break;
                case CacheStrategy.HOURLY:
                    availableTime = 60 * 60 * 1000;
                    break;
                case CacheStrategy.DAILY:
                    availableTime = 24 * 60 * 60 * 1000;
                    break;
                case CacheStrategy.PERSIST:
                case CacheStrategy.CACHE_PRECEDENCE:
                    availableTime = Long.MAX_VALUE;
                    break;
                default:
                    availableTime = 5 * 60 * 1000;
                    break;
            }

            return System.currentTimeMillis() - cursor.getLong(3) < availableTime ? true : false;
        }

    }
}
