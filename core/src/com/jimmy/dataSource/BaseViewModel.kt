package com.jimmy.huishou.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


//ViewModel基类---load函数用来更新页面，会通知所有接口refresh。
open class BaseViewModel : ViewModel() {
    var loadLiveData = MutableLiveData<Boolean>()
        get() = field

    fun load() {
        loadLiveData.value = true
    }

}