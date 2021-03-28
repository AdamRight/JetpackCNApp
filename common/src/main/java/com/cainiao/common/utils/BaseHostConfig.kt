package com.cainiao.common.utils

import com.cainiao.common.BuildConfig
import com.cainiao.common.network.config.BASE_URL


/**
 * 获取当前配置的baseHost
 */
fun getBaseHost(): String {
    return if (BuildConfig.DEBUG) {
        CniaoSpUtils.getString(SP_KEY_BASE_HOST) ?: HOST_DEV
    } else {
        HOST_PRODUCT
    }
}

/**
 * 更新配置host
 */
fun setBaseHost(host: String) {
    CniaoSpUtils.put(SP_KEY_BASE_HOST, host)
}


//配置host的key
private const val SP_KEY_BASE_HOST = "sp_key_base_host"

//不同的baseHost
const val HOST_DEV = BASE_URL//开发环境下的host配置
const val HOST_QA = "https://qa.course.api.cniao5.com/"//qa环境的host配置
const val HOST_PRODUCT = "https://release.course.api.cniao5.com/"//正式配置host