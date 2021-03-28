package com.cainiao.common.network

import androidx.collection.SimpleArrayMap
import com.cainiao.common.network.config.CniaoInterceptor
import com.cainiao.common.network.config.KtHttpLogInterceptor
import com.cainiao.common.network.config.LocalCookieKar
import com.cainiao.common.network.config.RetryInterceptor
import com.cainiao.common.network.support.IHttpCallback
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OkHttpApi : HttpApi {

    private val mClient = OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS) //完整的请求超时时长，从发起请求到接收返回数据
        .connectTimeout(10, TimeUnit.SECONDS) //与服务器建立连接时长
        .readTimeout(10, TimeUnit.SECONDS) //读取服务器返回数据的时长
        .writeTimeout(10, TimeUnit.SECONDS) //向服务器写入数据的时长
        .retryOnConnectionFailure(true) //重连
        .followRedirects(false) // 重定向
        .cache(Cache(File("data/user/0/com.cainiao/cache", "okHttp"), 1024))
        .cookieJar(LocalCookieKar())
        .addNetworkInterceptor(CniaoInterceptor())
        .addNetworkInterceptor(KtHttpLogInterceptor() {
            setLogLevel(KtHttpLogInterceptor.LogLevel.BODY)
        })
        .addNetworkInterceptor(RetryInterceptor(1))
        .build()

    private var baseUrl = "http://api.qingyunke.com/"

    private val cacheCall = SimpleArrayMap<Any, Call>()

    override fun get(params: Map<String, Any>, path: String, callback: IHttpCallback) {
        val url = "$baseUrl$path"
        val urlBuilder = url.toHttpUrl().newBuilder()
        params.forEach { entry ->
            urlBuilder.addEncodedQueryParameter(entry.key, entry.value.toString())
        }
        val request = Request.Builder()
            .get()
            .tag(params)
            .url(urlBuilder.build())
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build()
        val newCall = mClient.newCall(request)
        cacheCall.put(request.tag(), newCall)
        newCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailed(e.message ?: "")
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.body?.toString() ?: "")
            }
        })
    }

    override fun post(body: Any, path: String, callback: IHttpCallback) {
        val request = Request.Builder()
            .post(Gson().toJson(body).toRequestBody())
            .url("https://testapi.cniao5.com/accounts/login")
            .tag(body)
            .build()

        val newCall = mClient.newCall(request)
        //存储请求，用于取消
        cacheCall.put(request.tag(), newCall)
        newCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailed(e.message ?: "")
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.body?.string() ?: "")
            }

        })
    }

    override fun cancelCall(tag: Any) {
        val call = cacheCall.get(tag)
        call?.run {
            call.cancel()
            cacheCall.remove(tag)
        }
    }

    override fun cancelAllCall() {
        for (i in 0 until cacheCall.size()) {
            cacheCall.remove(cacheCall.keyAt(i))?.cancel()
        }
    }


    private suspend fun Call.call(async: Boolean = true): Response {
        return suspendCancellableCoroutine {
            if (async) {
                enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        if (it.isCancelled) return
                        it.resumeWithException(e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        it.resume(response)
                    }
                })
            } else {
                it.resume(execute())
            }
            it.invokeOnCancellation {
                try {
                    cancel()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }
}