package com.cainiao.common.network

import com.cainiao.common.network.support.IHttpCallback


/**
 * 网络请求接口
 */
interface HttpApi {

    /**
     * get异步请求
     * [params] 请求参数
     * [path] 请求地址
     * [callback] 请求回调
     */
    fun get(params: Map<String, Any>, path: String, callback: IHttpCallback)

    /**
     * get同步请求
     */
    fun getSync(params: Map<String, Any>, path: String): Any? = Any()

    fun post(body: Any, path: String, callback: IHttpCallback)

    fun postSync(body: Any, path: String): Any? = Any()

    fun cancelCall(tag:Any)

    fun cancelAllCall()
}