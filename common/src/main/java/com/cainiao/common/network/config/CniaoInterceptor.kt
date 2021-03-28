package com.cainiao.common.network.config

import com.blankj.utilcode.util.*
import com.cainiao.common.utils.CniaoSpUtils
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.CacheControl
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

/**
 * 加密请求参数拦截器
 */
class CniaoInterceptor : Interceptor {

    companion object {
        val gson = GsonBuilder()
            .enableComplexMapKeySerialization()
            .create()
        private val mapType = object : TypeToken<Map<String, Any>>() {}.type
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        // 公共请求参数
        val attachHeaders = mutableListOf<Pair<String, String>>(
            "appid" to NET_CONFIG_APP_ID,
            "platform" to "android",//如果重复请求，可能会报重复签名错误，yapi 平台标记则不会
            "timestamp" to System.currentTimeMillis().toString(),
            "brand" to DeviceUtils.getManufacturer(),
            "model" to DeviceUtils.getModel(),
            "uuid" to DeviceUtils.getUniqueDeviceId(),
            "network" to NetworkUtils.getNetworkType().name,
            "system" to DeviceUtils.getSDKVersionName(),
            "version" to AppUtils.getAppVersionName()
        )

        // 如果有token加入请求参数中
        val localToken =
            CniaoSpUtils.getString(SP_KEY_USER_TOKEN, originRequest.header("token")) ?: ""
        if (localToken.isNotEmpty()) {
            attachHeaders.add("token" to localToken)
        }

        // 需要加密的请求参数 公共请求参数加上原本的请求参数
        val signHeaders = mutableListOf<Pair<String, String>>()
        signHeaders.addAll(attachHeaders)

        //get的请求，参数
        if (originRequest.method == "GET") {
            originRequest.url.queryParameterNames.forEach { key ->
                signHeaders.add(key to (originRequest.url.queryParameter(key) ?: ""))
            }
        }

        val requestBody = originRequest.body
        if ("POST" == originRequest.method) {
            // post请求需要将内部的字段遍历出来参与sign计算
            if (requestBody is FormBody) {
                for (i in 0 until requestBody.size) {
                    signHeaders.add(requestBody.name(i) to requestBody.value(i))
                }
            }

            // json形式的body，将json转成Map遍历
            if (requestBody?.contentType()?.type == "application" && requestBody.contentType()?.subtype == "json") {
                runCatching {
                    val buffer = Buffer()
                    requestBody.writeTo(buffer)
                    buffer.readByteString().utf8()
                }.onSuccess {
                    val map = gson.fromJson<Map<String, Any>>(it, mapType)
                    map.forEach { p ->
                        signHeaders.add(p.key to p.value.toString())
                    }
                }
            }
        }

        // 加密的请求参数按照Ascii排序 用&拼接加上"&appkey=appKeyValue"
        val signValue = signHeaders
            .sortedBy { it.first }
            .joinToString("&") { "${it.first}=${it.second}" }
            .plus("&appkey=$NET_CONFIG_APP_KEY")

        val newRequest = originRequest.newBuilder()
            .cacheControl(CacheControl.FORCE_NETWORK)
        attachHeaders.forEach {
            newRequest.header(it.first, it.second)
        }
        // 添加签名信息
        newRequest.header("sign", EncryptUtils.encryptMD5ToString(signValue))
        return chain.proceed(newRequest.build())
    }
}