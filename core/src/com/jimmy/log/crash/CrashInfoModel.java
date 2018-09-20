package com.jimmy.log.crash;

import com.google.gson.annotations.Expose;

/**
 * Created by lorin on 16/2/4.
 */
public class CrashInfoModel {

    private transient int _id;
    private transient int hasSent;//0为false，1为true

    private String uuid;//设备唯表示
    private String date;//yyyy-MM-dd HH:mm:ss
    private String app;//应用名
    private String appv;//应用版本号
    private String dev;//硬件型号
    private String sys;//操作系统
    private String sysv;//操作系统版本
    private String cause;//异常原因
    private String exp;//异常信息
    private String stack;//页面栈
    private String extra;//附加信息

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getHasSent() {
        return hasSent;
    }

    public void setHasSent(int hasSent) {
        this.hasSent = hasSent;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getAppv() {
        return appv;
    }

    public void setAppv(String appv) {
        this.appv = appv;
    }

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public String getSysv() {
        return sysv;
    }

    public void setSysv(String sysv) {
        this.sysv = sysv;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
