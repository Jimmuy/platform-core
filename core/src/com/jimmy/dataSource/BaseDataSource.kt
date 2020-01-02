package com.jimmy.huishou.base

import androidx.annotation.Nullable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jimmy.dataSource.Resource
import com.jimmy.dataSource.Status

/*数据仓库，提供数据库查询数据和网络请求查询数据两种方式，
并暴露方法，以方便配置是否需要数据库数据预加载以及网络请求之后，
数据是否需要添加进数据库等操作*/
abstract class BaseDataSource<T> {
    private val result = MediatorLiveData<Resource<T>>()

    init {
        result.value = Resource.loading(null)
        //如果需要从数据库预加载页面则先从数据库取数据
        takeIf { preloadLocal() }.apply {
            attemptLoadLocal()
        }
    }

    private fun attemptLoadLocal() {
        val localLiveData = loadLocal()
        localLiveData?.let { liveData ->
            result.addSource(liveData) {
                result.removeSource(liveData)
                it?.takeIf {
                    it is List<*>
                }?.apply {
                    this.takeIf { (this as List<*>).isNotEmpty() }?.apply { result.value = it }
                        ?: result.setValue(Resource.empty(null))
                } ?: result.setValue(it)
            }
        }

        takeIf { shouldFetchFromNet() }.apply { attemptFetchFromNet() }
    }


    private fun attemptFetchFromNet() {
        result.setValue(Resource.loading(null))
        var netLiveData: LiveData<Resource<T>> = fetchFromNet()
        result.addSource(netLiveData) {
            when (netLiveData.value?.status) {
                Status.SUCCESS -> {
                    //网络请求之后判断数据是否为空，为空则同步更新数据库清空，不为空的话则更新数据库
                    netLiveData.value?.let { value ->
                        value.data?.takeIf { data ->
                            data is List<*>
                        }?.apply {
                            //判断如果是list则是不是为空列表
                            this.takeIf { list ->
                                (list as List<*>).isNotEmpty()
                            }.apply {
                                result.setValue(Resource(Status.SUCCESS, this, null))
                            } ?: result.setValue(Resource(Status.EMPTY, null, "list size is 0"))
                        } ?: result.setValue(value)
                    }
                    updateLocalData(netLiveData.value?.data)
                }
                Status.ERROR -> {
                    result.setValue(Resource(Status.ERROR, null, netLiveData.value?.message))
                }
            }

        }


    }

    /*更新数据库*/
    protected open fun updateLocalData(data: T?) {}

    /*网络请求*/
    abstract fun fetchFromNet(): LiveData<Resource<T>>

    /*是否需要从网络获取数据，一般情况下都需要*/
    protected open fun shouldFetchFromNet(): Boolean {
        return true
    }

    /*加载本地数据库数据*/
    @Nullable
    protected open fun loadLocal(): LiveData<Resource<T>>? {
        return null
    }

    /*请求网络之前是否需要先从数据库加载之前你的旧数据*/
    protected open fun preloadLocal(): Boolean {
        return false
    }

    fun getAsLiveData(): MutableLiveData<Resource<T>> {
        return result
    }

}