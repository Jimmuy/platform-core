package com.jimmy.sample.net

import android.annotation.SuppressLint
import com.jimmy.app.CoreModel


/**
 * format : JSON
 * charset : utf-8
 * signType : RSA2
 * version : 1.0
 * merchantId : 2088711651677551
 * storeId : 320038
 * operatorId : 313
 * actType : 3
 * merchantName : 山东泰山新合作商贸连锁有限公司AS
 * storeName : 泰山新合作王西店
 */
@SuppressLint("ParcelCreator")
data class AccountModel(
    var token: String? = null,
    var version: String = "",
    /**
     * 登录名、用户名
     */
    var userName: String = "",
    
    var userPhone: String = ""
 
) : CoreModel()
