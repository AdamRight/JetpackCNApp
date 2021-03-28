package com.cainiao.common.network

import com.cainiao.common.BuildConfig
import com.cainiao.common.network.config.*
import com.cainiao.common.network.config.LocalCookieKar
import com.cainiao.common.network.support.LiveDataCallAdapterFactory
import com.cainiao.common.utils.HostInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object KtRetrofit {

    private val mClient = OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS) //完整的请求超时时长，从发起请求到接收返回数据
        .connectTimeout(10, TimeUnit.SECONDS) //与服务器建立连接时长
        .readTimeout(10, TimeUnit.SECONDS) //读取服务器返回数据的时长
        .writeTimeout(10, TimeUnit.SECONDS) //向服务器写入数据的时长
        .retryOnConnectionFailure(true) //重连
        .followRedirects(false) // 重定向
        .cache(Cache(File("data/user/0/com.zj.cainiao/cache", "okHttp"), 1024))
        .cookieJar(LocalCookieKar())
        .addInterceptor(HostInterceptor())
        .addNetworkInterceptor(CniaoInterceptor())
        .addNetworkInterceptor(KtHttpLogInterceptor() {
            if (BuildConfig.DEBUG) {
                setLogLevel(KtHttpLogInterceptor.LogLevel.BODY)
            } else {
                setLogLevel(KtHttpLogInterceptor.LogLevel.NONE)
            }
        })
        .addNetworkInterceptor(RetryInterceptor(1))
        .build()

    private val retrofitBuilder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .client(mClient)

    private var retrofit: Retrofit? = null

    /**
     * 初始化配置
     * [baseUrl]项目接口的基类url，以/结尾
     */
    fun initConfig(baseUrl: String = BASE_URL, okClient: OkHttpClient = mClient): KtRetrofit {
        retrofit = retrofitBuilder.baseUrl(baseUrl).client(okClient).build()
        return this
    }

    /**
     * 获取retrofit的Service对象
     * [serviceClazz] 定义的retrofit的service 接口类
     */
    fun <T> getService(serviceClazz: Class<T>): T {
        if (retrofit == null) {
            throw UninitializedPropertyAccessException("Retrofit必须初始化，需要配置baseURL")
        } else {
            return this.retrofit!!.create(serviceClazz)
        }
    }
}