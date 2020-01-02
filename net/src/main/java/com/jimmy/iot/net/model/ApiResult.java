

package com.jimmy.iot.net.model;

/**
 * <p>描述：提供的默认的标注返回api</p>
 */
public class ApiResult<T> {
    private int code;
    private String msg;
    private T baseData;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 需要根据实际类型进行返回
     */
    public T getBaseData() {
        return baseData;
    }

    public void setBaseData(T baseData) {
        this.baseData = baseData;
    }

    public boolean isOk() {
        return code == 200;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", baseData=" + baseData +
                '}';
    }
}
