package com.jimmy.log.analysis;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lorin on 16/3/17.
 */
public class AnalysisInfo implements Parcelable{

    private int _id;
    private String content;
    private int hasSent;//0为未上传，1为上传中，2为已上传

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHasSent() {
        return hasSent;
    }

    public void setHasSent(int hasSent) {
        this.hasSent = hasSent;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.content);
        dest.writeInt(this.hasSent);
    }

    public AnalysisInfo() {
    }

    protected AnalysisInfo(Parcel in) {
        this._id = in.readInt();
        this.content = in.readString();
        this.hasSent = in.readInt();
    }

    public static final Creator<AnalysisInfo> CREATOR = new Creator<AnalysisInfo>() {
        public AnalysisInfo createFromParcel(Parcel source) {
            return new AnalysisInfo(source);
        }

        public AnalysisInfo[] newArray(int size) {
            return new AnalysisInfo[size];
        }
    };
}
