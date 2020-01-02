
package com.jimmy.iot.net.request;


import com.google.gson.reflect.TypeToken;
import com.jimmy.iot.net.callback.CallBack;
import com.jimmy.iot.net.callback.CallBackProxy;
import com.jimmy.iot.net.func.ApiResultFunc;
import com.jimmy.iot.net.func.HandleFuc;
import com.jimmy.iot.net.func.RetryExceptionFunc;
import com.jimmy.iot.net.model.ApiResult;
import com.jimmy.iot.net.subsciber.CallBackSubsciber;
import com.jimmy.iot.net.transformer.HandleErrTransformer;
import com.jimmy.iot.net.utils.RxUtil;
import com.jimmy.iot.net.utils.Utils;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * <p>描述：自定义请求，例如你有自己的ApiService</p>
 */
@SuppressWarnings(value = {"unchecked", "deprecation"})
public class CustomRequest extends BaseRequest<CustomRequest> {
    public CustomRequest() {
        super("");
    }

    @Override
    public CustomRequest build() {
        return super.build();
    }

    /**
     * 创建api服务  可以支持自定义的api，默认使用BaseApiService,上层不用关心
     *
     * @param service 自定义的apiservice class
     */
    public <T> T create(final Class<T> service) {
        checkvalidate();
        return retrofit.create(service);
    }

    private void checkvalidate() {
        Utils.checkNotNull(retrofit, "请先在调用build()才能使用");
    }

    /**
     * 调用call返回一个Observable<T>
     * 举例：如果你给的是一个Observable<ApiResult<AuthModel>> 那么返回的<T>是一个ApiResult<AuthModel>
     */
    public <T> Observable<T> call(Observable<T> observable) {
        checkvalidate();
        return observable.compose(RxUtil.io_main())
                .compose(new HandleErrTransformer())
                .retryWhen(new RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay));
    }

    public <T> void call(Observable<T> observable, CallBack<T> callBack) {
        call(observable, new CallBackSubsciber(context, callBack));
    }

    public <R> void call(Observable observable, Observer<R> subscriber) {
        observable.compose(RxUtil.io_main())
                .subscribe(subscriber);
    }


    /**
     * 调用call返回一个Observable,针对ApiResult的业务<T>
     * 举例：如果你给的是一个Observable<ApiResult<AuthModel>> 那么返回的<T>是AuthModel
     */
    public <T> Observable<T> apiCall(Observable<ApiResult<T>> observable) {
        checkvalidate();
        return observable
                .map(new HandleFuc<T>())
                .compose(RxUtil.<T>io_main())
                .compose(new HandleErrTransformer<T>())
                .retryWhen(new RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay));
    }

    public <T> Disposable apiCall(Observable<T> observable, CallBack<T> callBack) {
        return call(observable, new CallBackProxy<ApiResult<T>, T>(callBack) {
        });
    }

    public <T> Disposable call(Observable<T> observable, CallBackProxy<? extends ApiResult<T>, T> proxy) {
        Observable<ApiResult<T>> toObservable = build().toObservable(observable, proxy);

        return toObservable.subscribeWith(new CallBackSubsciber<ApiResult<T>>(context, proxy.getCallBack()));

    }

    @SuppressWarnings(value = {"unchecked", "deprecation"})
    private <T> Observable<ApiResult<T>> toObservable(Observable observable, CallBackProxy<? extends ApiResult<T>, T> proxy) {
        return observable.map(new ApiResultFunc(proxy != null ? proxy.getType() : new TypeToken<ResponseBody>() {
        }.getType(), !isXmlRequest))
                .compose(isSyncRequest ? RxUtil._main() : RxUtil._io_main())
                .retryWhen(new RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay));
    }

    @Override
    protected Observable<ResponseBody> generateRequest() {
        return null;
    }
}
