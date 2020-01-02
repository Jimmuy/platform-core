

package com.jimmy.iot.net.request;


import com.google.gson.reflect.TypeToken;
import com.jimmy.iot.net.callback.CallBack;
import com.jimmy.iot.net.callback.CallBackProxy;
import com.jimmy.iot.net.callback.CallClazzProxy;
import com.jimmy.iot.net.func.ApiResultFunc;
import com.jimmy.iot.net.func.RetryExceptionFunc;
import com.jimmy.iot.net.model.ApiResult;
import com.jimmy.iot.net.subsciber.CallBackSubsciber;
import com.jimmy.iot.net.utils.RxUtil;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * <p>描述：get请求</p>
 */
@SuppressWarnings(value = {"unchecked", "deprecation"})
public class GetRequest extends BaseRequest<GetRequest> {
    public GetRequest(String url) {
        super(url);
    }

    public <T> Observable<T> execute(Class<T> clazz) {
        return execute(new CallClazzProxy<ApiResult<T>, T>(clazz) {
        });
    }


    public <T> Observable<T> execute(Type type) {
        return execute(new CallClazzProxy<ApiResult<T>, T>(type) {
        });
    }


    public <T> Observable<T> execute(CallClazzProxy<? extends ApiResult<T>, T> proxy) {
        return build().generateRequest()
                .map(new ApiResultFunc(proxy.getType()))
                .compose(isSyncRequest ? RxUtil._main() : RxUtil._io_main())
                .retryWhen(new RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay));
    }


    public <T> Disposable execute(CallBack<T> callBack) {
        return execute(new CallBackProxy<ApiResult<T>, T>(callBack) {
        });
    }

    public <T> Disposable execute(CallBackProxy<? extends ApiResult<T>, T> proxy) {
        Observable<ApiResult<T>> observable = build().toObservable(apiManager.get(url, params.urlParamsMap), proxy);

        return observable.subscribeWith(new CallBackSubsciber<ApiResult<T>>(context, proxy.getCallBack()));

    }
    private <T> Observable<ApiResult<T>> toObservable(Observable observable, CallBackProxy<? extends ApiResult<T>, T> proxy) {
        return observable.map(new ApiResultFunc(proxy != null ? proxy.getType() : new TypeToken<ResponseBody>() {
        }.getType(),!isXmlRequest))
                .compose(isSyncRequest ? RxUtil._main() : RxUtil._io_main())
                .retryWhen(new RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay));
    }

    @Override
    protected Observable<ResponseBody> generateRequest() {

        return apiManager.get(url, params.urlParamsMap);
    }
}
