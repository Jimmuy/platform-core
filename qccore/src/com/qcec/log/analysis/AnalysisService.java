package com.qcec.log.analysis;

import android.text.format.DateFormat;

import com.qcec.app.QCApplication;
import com.qcec.datamodel.GsonConverter;
import com.qcec.dataservice.base.RequestHandler;
import com.qcec.dataservice.request.ApiRequest;
import com.qcec.dataservice.request.BasicApiRequest;
import com.qcec.dataservice.request.HttpMethod;
import com.qcec.dataservice.request.JsonBody;
import com.qcec.dataservice.response.ApiResponse;
import com.qcec.log.QCLog;
import com.qcec.log.ConsoleLog;
import com.qcec.log.LogDBHelper;
import com.qcec.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by lorin on 16/3/17.
 */
public class AnalysisService {

    private static final String TAG = "AnalysisService";
    private static final String PRIVATE_KEY = "Wk3QHughJ2XtF/Qd/axwydEedRj";
    private static final String BOUNDARY = "g9sI+RcI3";

    private static AnalysisDataClient analysisDataClient = null;
    public static String markUploadUrl;

    private static int limitCount = 20;
    public static boolean isDebug = false;

    private static String uuid = DeviceUtils.getUUID(QCApplication.getInstance());

    /**
     * 传入URL，初始化AnalysisService
     * PS:URL需要从APP层传入，若要用埋点功能此方法必须事先调用
     *
     * @param url
     */
    public static void config(String url) {
        if (null == analysisDataClient) {
            analysisDataClient = new AnalysisDataClient(LogDBHelper.getInstance(), 1000);
        }
        markUploadUrl = url;
        checkInfoProcessStatus(false, null);
    }

    public static void setLimitCount(int limit) {
        if (limit > 0) {
            limitCount = limit;
        }
    }

    public static void enableDebug(boolean bool) {
        isDebug = bool;
    }

    /**
     * 创建埋点新数据，通过checkInfoProcessStatus方法判断处理逻辑
     *
     * @param category
     * @param action
     * @param page
     * @param label
     * @param extra
     */
    public static void addNewMarkPoint(String category, String action, String page, String label, Map<String, String> extra) {

        StringBuffer sbContent = new StringBuffer();
        sbContent.append(DateFormat.format("yyyy-MM-dd kk:mm:ss", System.currentTimeMillis()));
        sbContent.append("|");
        sbContent.append(uuid);
        sbContent.append("|");
        sbContent.append(category);
        sbContent.append("|");
        sbContent.append(page);
        sbContent.append("|");
        sbContent.append(action);
        sbContent.append("|");
        sbContent.append(label);
        sbContent.append("|");
        if (null == extra || extra.size() == 0) {
            sbContent.append("");
        } else {
            sbContent.append(GsonConverter.toJson(extra));
        }

        ConsoleLog.i(sbContent.toString());
        checkInfoProcessStatus(true, sbContent.toString());

    }

    /**
     * 修改埋点数据状态对外暴露方法
     *
     * @param list
     * @param status
     */
    public static void motifyMessage(ArrayList<AnalysisInfo> list, int status) {

        analysisDataClient.motifyMessageStatus(list, status);
    }

    /**
     * 查询为发送数据对外暴露方法
     *
     * @return
     */
    public static ArrayList<AnalysisInfo> getUnsentMessages() {

        return analysisDataClient.queryAllUnsentMessages();
    }

    /**
     * 查询表中所有数据对外暴露方法
     *
     * @return
     */
    public static ArrayList<AnalysisInfo> getAllMessages() {

        return analysisDataClient.queryAllMessages();
    }

    /**
     * 数据处理方法，有一下规则：
     * 1、Debug模式下没有数据库操作
     * 2、点击新插入判断条目超过limitCount触发上传
     * 3、启动时判断条目大于0触发上传
     *
     * @param isAddNew
     */
    private static void checkInfoProcessStatus(boolean isAddNew, String content) {
        if (isDebug) {
            if(isAddNew) {
                ArrayList<AnalysisInfo> queryInfos = new ArrayList<>();
                AnalysisInfo markPointInfo = new AnalysisInfo();
                markPointInfo.setContent(content);
                queryInfos.add(markPointInfo);
                uploadInfo(queryInfos);
            }
            return;
        }

        if(isAddNew) {
            //非Debug模式下写入数据库
            analysisDataClient.addMarkMessage(content);
            //查询所有未发送数据
            ArrayList<AnalysisInfo> queryInfos = getUnsentMessages();

            if (queryInfos.size() > limitCount) {
                QCLog.d("Notify Upload");
                analysisDataClient.motifyMessageStatus(queryInfos,analysisDataClient.ANALYSIS_ITEM_UPLOADING);
                uploadInfo(queryInfos);

            } else {
                QCLog.d("Alanysis Info#Current Existed Count: " + queryInfos.size() + " [Add Successful]");
            }
        } else {
            ArrayList<AnalysisInfo> queryInfos = getUnsentMessages();
            if (queryInfos.size() > 0) {
                QCLog.d("Notify Upload");
                analysisDataClient.motifyMessageStatus(queryInfos,analysisDataClient.ANALYSIS_ITEM_UPLOADING);
                uploadInfo(queryInfos);
            }

        }

    }

    /**
     * 创建上传需要的标识APIKey
     *
     * @param firstContent
     * @return
     */
    public static String createAPIKey(String firstContent) {
        String[] strArr = firstContent.split("\\|", -1);

        StringBuffer keyContent = new StringBuffer();
        keyContent.append(strArr[6]);//time
        keyContent.append(BOUNDARY);
        keyContent.append(strArr[1]);//UUID
        keyContent.append(BOUNDARY);
        keyContent.append(strArr[4]);//category
        keyContent.append(BOUNDARY);
        keyContent.append(strArr[3]);//page
        keyContent.append(BOUNDARY);
        keyContent.append(strArr[2]);//action
        keyContent.append(BOUNDARY);
        keyContent.append(strArr[5]);//label
        keyContent.append(BOUNDARY);
        keyContent.append(strArr[0]);//extra
        keyContent.append(BOUNDARY);
        keyContent.append(PRIVATE_KEY);

        return DeviceUtils.getMD5(keyContent.toString());
    }

    /**
     * 上传数据处理逻辑
     *
     * @param markInfos
     */
    public static void uploadInfo(final ArrayList<AnalysisInfo> markInfos) {
//
//        if (markInfos.size() > 0) {
//
//            BasicApiRequest analysisUploadRequest = new BasicApiRequest(AnalysisService.markUploadUrl, HttpMethod.POST);
//            analysisUploadRequest.addHeader("apikey", AnalysisService.createAPIKey(markInfos.get(0).getContent()));
//            JsonBody jsonBody = new JsonBody(getListBody(markInfos));
//            analysisUploadRequest.setBody(jsonBody);
//
//            QCApplication.getInstance().getApiService().exec(analysisUploadRequest, new RequestHandler<ApiRequest, ApiResponse>() {
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
//                    QCLog.d(markInfos.size() + " record Upload Successful!!!");
//
//                    if (!isDebug && resp.getResultModel().status == 0 && null != markInfos && markInfos.size() > 0) {
//                        analysisDataClient.deleteMarkMessagesByid(markInfos);
//                    }
//                }
//
//                @Override
//                public void onRequestFailed(ApiRequest req, ApiResponse resp) {
//                    if (!isDebug && null != markInfos && markInfos.size() > 0) {
//                        analysisDataClient.motifyMessageStatus(markInfos, analysisDataClient.ANALYSIS_ITEM_COMMON);
//                    }
//                }
//            });
//
//        } else {
//            QCLog.w("No Analysis Info");
//        }
    }


    /**
     * 组装上传数据格式
     *
     * @param infos
     * @return
     */
    private static AnalysisUploadModel getListBody(List<AnalysisInfo> infos) {
        List<String> bodyStr = new ArrayList<String>();
        for (int i = 0; i < infos.size(); i++) {
            bodyStr.add(infos.get(i).getContent());
        }

        AnalysisUploadModel markUploadObject = new AnalysisUploadModel();
        markUploadObject.setData(bodyStr);

        return markUploadObject;
    }

}
