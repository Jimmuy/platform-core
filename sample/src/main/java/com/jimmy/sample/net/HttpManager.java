package com.jimmy.sample.net;


import com.jimmy.iot.net.request.GetRequest;
import com.jimmy.iot.net.request.PostRequest;

public class HttpManager {
    /**
     * get请求
     */
    public static GetRequest get(String url) {
        return new DemoGetRequest(url);
    }

    /**
     * post请求
     */
    public static PostRequest post(String url) {
        return new DemoPostRequest(url);
    }
}
