package com.jimmy.debug;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jimmy on 16/4/6.
 */
public class DomainModel implements Parcelable {

    public String title;
    public String url;
    public DomainType domainType;

    public DomainModel() {
    }

    public DomainModel(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public DomainModel(String title, String url, DomainType domainType) {
        this.title = title;
        this.url = url;
        this.domainType = domainType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeInt(this.domainType == null ? -1 : this.domainType.ordinal());
    }


    protected DomainModel(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
        int tmpDomainType = in.readInt();
        this.domainType = tmpDomainType == -1 ? null : DomainType.values()[tmpDomainType];
    }

    public static final Creator<DomainModel> CREATOR = new Creator<DomainModel>() {
        @Override
        public DomainModel createFromParcel(Parcel source) {
            return new DomainModel(source);
        }

        @Override
        public DomainModel[] newArray(int size) {
            return new DomainModel[size];
        }
    };
}
