package com.qcec.log.crash;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.qcec.app.CoreApplication;
import com.qcec.core.R;
import com.qcec.log.ActivityInfoStack;
import com.qcec.log.LogDBHelper;
import com.qcec.utils.DeviceUtils;
import com.qcec.utils.SystemUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * 崩溃日志的管理类,用来捕获,保存,处理崩溃日志
 * Created by lorin on 16/3/9.
 */
public class CrashManager {

    private CrashManager() {
    }

    private static long launchTime;
    private static CrashDataClient dataClient = null;
    private static CrashCloseFunction closeFunction;

    private static Thread.UncaughtExceptionHandler defaultExceptionHandler;

    private static final String PRIVATE_KEY = "Wk3QHughJ2XtF/Qd/axwydEedRj";
    private static final String BOUNDARY = "g9sI+RcI3";
    public static String crashUploadUrl;

    /**
     * 初始化方法需应用的Application继承QCApplication
     */

    public static void init() {
        CrashManager.init(null);
    }

    public static void init(String url) {
        CrashManager.init(url, null);
    }


    public static void init(String url, CrashCloseFunction crashCloseFunction) {
        crashUploadUrl = url;
        launchTime = System.currentTimeMillis();
        closeFunction = crashCloseFunction;

        if (null == dataClient) {
            dataClient = new CrashDataClient(LogDBHelper.getInstance());
        }

        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                saveCrashData(ex);
                processCrash(thread, ex);
            }
        });

        List<CrashInfoModel> infos = getCrashInfoByStatus("0");
        if (null != crashUploadUrl && !crashUploadUrl.equals("") && infos.size() > 0) {
            uploadInfoAction(infos);
        }
    }

    /**
     * 保存崩溃时信息,包含了应用的启动时间和崩溃时间一起崩溃时的设备相关信息等
     */
    private static void saveCrashData(Throwable ex) {
        StringBuffer sb = new StringBuffer();

        long crashTime = System.currentTimeMillis();

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        printWriter.close();
        sb.append(stringWriter.toString());

        CrashInfoModel info = new CrashInfoModel();
        Application application = CoreApplication.getInstance();
        info.setHasSent(0);
        info.setUuid(DeviceUtils.getUUID(application));
        info.setDate(DateFormat.format("yyyy-MM-dd kk:mm:ss", crashTime).toString());
        info.setApp(SystemUtils.getPackageName(application));
        info.setAppv(SystemUtils.getVersionName(application));
        info.setDev(DeviceUtils.getPhoneModel());
        info.setSys("Android");
        info.setSysv(DeviceUtils.getAndroidVersion());

        String[] strArr = sb.toString().split("\\\n", -1);
        info.setCause(strArr[0]);

        info.setExp(sb.toString());
        info.setStack(ActivityInfoStack.dump());
        info.setExtra("");

        dataClient.addCrashInfo(info);
    }

    /**
     * 处理应用崩溃
     */
    private static void processCrash(Thread thread, Throwable ex) {
        Context context = CoreApplication.getInstance();
        if (SystemUtils.isDebuggable(context)) {
            defaultExceptionHandler.uncaughtException(thread, ex);
            return;
        }

        showToast(context.getString(R.string.app_crash));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (null == closeFunction) {
            System.exit(0);
        } else {
            closeFunction.exitAction();
        }
    }

    /**
     * 获取最近一条崩溃CrashInfo对象
     *
     * @return CrashInfo对象
     */
    public static CrashInfoModel getLatestCrashInfo() {
        return dataClient.queryLatestCrashInfo();
    }

    /**
     * 获取所有的崩溃信息对象列表
     *
     * @return List<CrashInfo>
     */
    public static List<CrashInfoModel> getAllCrashInfos() {
        return dataClient.queryAllCrashInfos();
    }

    /**
     * 获取对应发送状态
     *
     * @return List<CrashInfo>
     */
    public static List<CrashInfoModel> getCrashInfoByStatus(String status) {
        return dataClient.queryInfosBySendingStatus(status);
    }

    /**
     * 清空所有的崩溃信息
     */
    public static void clearAllCrashInfos() {
        dataClient.clearCrashInfos();
    }

    private static void showToast(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(CoreApplication.getInstance(), message, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
    }

    /**
     * 创建上传需要的标识APIKey
     *
     * @param firstInfo
     * @return
     */
    private static String createAPIKey(CrashInfoModel firstInfo) {

        StringBuffer keyContent = new StringBuffer();
        keyContent.append(firstInfo.getUuid());
        keyContent.append(BOUNDARY);
        keyContent.append(firstInfo.getDate());
        keyContent.append(BOUNDARY);
        keyContent.append(firstInfo.getApp());
        keyContent.append(BOUNDARY);
        keyContent.append(firstInfo.getAppv());
        keyContent.append(BOUNDARY);
        keyContent.append(firstInfo.getDev());
        keyContent.append(BOUNDARY);
        keyContent.append(firstInfo.getSys());
        keyContent.append(BOUNDARY);
        keyContent.append(firstInfo.getSysv());
        keyContent.append(BOUNDARY);
        keyContent.append(PRIVATE_KEY);

        return DeviceUtils.getMD5(keyContent.toString());
    }

    /**
     * 上传数据处理逻辑
     *
     * @param crashInfos
     */
    private static void uploadInfoAction(final List<CrashInfoModel> crashInfos) {
//
//        if (crashInfos.size() > 0) {
//
//            BasicApiRequest crashUploadRequest = new BasicApiRequest(crashUploadUrl, HttpMethod.POST);
//            crashUploadRequest.addHeader("apikey", createAPIKey(crashInfos.get(0)));
//            RequestBody.JsonBody jsonBody = new RequestBody.JsonBody(getListBody(crashInfos));
//            SmartLogger.json(jsonBody.getJson());
//
//            crashUploadRequest.setBody(jsonBody);
//            CoreApplication.getInstance().getApiService().exec(crashUploadRequest, new RequestHandler<ApiRequest, ApiResponse>() {
//                @Override
//                public void onRequestStart(ApiRequest req) {
//
//                }
//
//                @Override
//                public void onRequestProgress(ApiRequest req, int count, int total) {
//
//                }
//
//                @Override
//                public void onRequestFinish(ApiRequest req, ApiResponse resp) {
//                    if (resp.getResultModel().status == 0) {
//                        SmartLogger.d("SUCCESS:" + resp.getResultModel().status + " | ");
//                        //1为已发送状态
//                        dataClient.motifyMessageStatus(crashInfos, dataClient.CRASH_ITEM_UPLOADING_COMPLETE);
//                    } else {
//                        SmartLogger.d("Finish Error:" + resp.getResultModel().status + " | ");
//                    }
//                }
//
//                @Override
//                public void onRequestFailed(ApiRequest req, ApiResponse resp) {
//                    SmartLogger.d("Crash Upload Fail");
//
//                }
//            });
//
//        } else {
//            CoreLog.w("No Analysis Info");
//        }
    }

    /**
     * 组装上传数据格式
     *
     * @param infos
     * @return
     */

    private static CrashUploadModel getListBody(List<CrashInfoModel> infos) {

        CrashUploadModel crashUploadModel = new CrashUploadModel();
        crashUploadModel.setData(infos);

        return crashUploadModel;
    }

}
