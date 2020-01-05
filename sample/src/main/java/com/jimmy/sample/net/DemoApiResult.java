package com.jimmy.sample.net;


import com.jimmy.iot.net.model.ApiResult;

public class DemoApiResult<T> extends ApiResult<T> {

    public T data;

    @Override
    public T getBaseData() {
        return data;
    }

    @Override
    public boolean isOk() {
        //重写isOK用来判定网络是否成功
        return super.isOk();
    }

    @Override
    public int getCode() {
        return super.getCode();
    }
}
