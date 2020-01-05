package com.jimmy.sample.net;


import com.jimmy.debug.DomainModel;
import com.jimmy.sample.BaseApplication;
import com.jimmy.sample.BuildConfig;
import com.jimmy.utils.GsonConverter;
import com.jimmy.utils.PreferenceUtils;

import java.util.Arrays;
import java.util.List;

public class DomainConfig {

    private DomainConfig() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }


//    public static DomainModel API_OFFICIAL_DOMAIN = new DomainModel("正式", "http://cashier.test.miyahub.net/");
    private static DomainModel API_OFFICIAL_DOMAIN = new DomainModel("正式", "http://cashier.miyahub.com/");
    private static DomainModel API_DEV_DOMAIN = new DomainModel("开发", "http://cashier.test.miyahub.net/");
    public static DomainModel apiDomain = getInitApiDomain();

    private static DomainModel getInitApiDomain() {
        DomainModel domainModel = GsonConverter.decode(PreferenceUtils.getPrefString(
                BaseApplication.Companion.get(), "domain",""), DomainModel.class);
        if (domainModel == null) {
            domainModel = DomainConfig.getDefaultApiDomain();
        }
        return domainModel;
    }

    /**
     * 获取默认weexDomain
     */
    public static DomainModel getDefaultApiDomain() {
        return BuildConfig.DEBUG ? API_DEV_DOMAIN : API_OFFICIAL_DOMAIN;
    }

    public static List<DomainModel> getApiDomainList() {
        return Arrays.asList(API_OFFICIAL_DOMAIN, API_DEV_DOMAIN);
    }



}
