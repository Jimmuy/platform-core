package com.jimmy.sample.net;



import com.jimmy.iot.net.callback.CallBack;
import com.jimmy.iot.net.callback.CallBackProxy;
import com.jimmy.iot.net.callback.CallClazzProxy;
import com.jimmy.iot.net.request.GetRequest;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class DemoGetRequest extends GetRequest {
    public DemoGetRequest(String url) {
        super(url);
    }

    @Override
    public <T> Observable<T> execute(Type type) {
        return super.execute(new CallClazzProxy<DemoApiResult<T>, T>(type) {
        });
    }

    @Override
    public <T> Observable<T> execute(Class<T> clazz) {
        return super.execute(new CallClazzProxy<DemoApiResult<T>, T>(clazz) {
        });
    }

    @Override
    public <T> Disposable execute(CallBack<T> callBack) {
        return super.execute(new CallBackProxy<DemoApiResult<T>, T>(callBack) {
        });
    }
}