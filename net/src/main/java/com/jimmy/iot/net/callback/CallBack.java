
package com.jimmy.iot.net.callback;



import com.jimmy.iot.net.exception.ApiException;
import com.jimmy.iot.net.utils.Utils;

import java.lang.reflect.Type;

/**
 * <p>描述：网络请求回调</p>
 */
public abstract class CallBack<T> implements IType<T> {
    public abstract void onStart();

    public abstract void onCompleted();

    public abstract void onError(ApiException e);

    public abstract void onSuccess(T t);

    @Override
    public Type getType() {//获取需要解析的泛型T类型
        return Utils.findNeedClass(getClass());
    }

    public Type getRawType() {//获取需要解析的泛型T raw类型
        return Utils.findRawType(getClass());
    }
}
