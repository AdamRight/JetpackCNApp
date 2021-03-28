package com.cainiao.common.model

import androidx.lifecycle.LiveData

/**
 * 创建一个空的LiveData对象类
 */
class AbsentLiveDataL<T : Any?> private constructor() : LiveData<T>() {

    init {
        postValue(null)
    }

    companion object {
        fun <T : Any?> create(): LiveData<T> {
            return AbsentLiveDataL()
        }
    }
}