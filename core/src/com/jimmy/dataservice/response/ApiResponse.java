package com.jimmy.dataservice.response;

import com.google.gson.JsonObject;
import com.jimmy.datamodel.ResultModel;

import java.util.Map;

public interface ApiResponse extends HttpResponse {

    /**
     * check if response is from cache
     * @return
     */
    boolean isFromCache();

    /**
     * convert request result to google json object
     *
     * @return
     */
    JsonObject getJsonResult();

    /**
     * convert request result to Map<String, Object>
     *
     * @return
     */
    Map<String, Object> getResultMap();

    /**
     * convert request result to base data model
     *
     * @return
     */
    ResultModel getResultModel();



}
