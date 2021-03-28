package com.cainiao.common.network.config

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.cainiao.common.network.model.NetResponse
import com.cainiao.common.network.support.CniaoUtils
import okhttp3.*
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.StringBuilder

class KtHttpLogInterceptor(block: (KtHttpLogInterceptor.() -> Unit)? = null) : Interceptor {

    /**
     * log打印等级
     */
    enum class LogLevel {
        NONE, // 不打印
        BASIC,// 打印首行，
        HEADERS,// 打印请求头，响应头
        BODY   // 打印所有
    }

    /**
     * log打印颜色
     */
    enum class LogColor {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    private var logLevel =
        LogLevel.NONE
    private var logColor =
        LogColor.DEBUG
    private var tag = TAG

    init {
        block?.invoke(this)
    }

    companion object {
        const val TAG = "KTLog"

        const val MILLIS_PATTERN = "YYYY-MM-dd:mm:ss.SSSXXX"

        // 格式化时间
        fun toDateTimeStr(millis: Long, pattern: String): String =
            SimpleDateFormat(pattern, Locale.getDefault()).format(millis)
    }

    fun setLogLevel(level: LogLevel): KtHttpLogInterceptor {
        logLevel = level
        return this
    }

    fun setLogColor(color: LogColor): KtHttpLogInterceptor {
        logColor = color
        return this
    }

    fun setTag(tag: String): KtHttpLogInterceptor {
        this.tag = tag
        return this
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request() // 请求

        // 响应
        return kotlin.runCatching { chain.proceed(request) }
            .onFailure {
                it.printStackTrace()
                logIt(
                    it.message.toString(),
                    LogColor.ERROR
                )
            }
            .onSuccess { response ->
                if (logLevel == LogLevel.NONE)
                    return response
                logRequest(request, chain.connection())
                logResponse(response)
            }.getOrThrow()
    }

    private fun logRequest(request: Request, connection: Connection?) {
        val sb = StringBuilder()
        sb.appendLine("\r\n")
        sb.appendLine("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->")
        when (logLevel) {
            LogLevel.NONE -> {
            }
            LogLevel.BASIC -> {
                logBasicReq(sb, request, connection)
            }
            LogLevel.HEADERS -> {
                logHeadersReq(sb, request, connection)
            }
            LogLevel.BODY -> {
                logBodyReq(sb, request, connection)
            }
        }
        sb.appendLine("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->")
        logIt(sb)
    }

    private fun logResponse(response: Response) {
        val sb = StringBuffer()
        sb.appendLine("\r\n")
        sb.appendLine("<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<")
        when (logLevel) {
            LogLevel.NONE -> {
                /*do nothing*/
            }
            LogLevel.BASIC -> {
                logBasicRsp(sb, response)
            }
            LogLevel.HEADERS -> {
                logHeadersRsp(response, sb)
            }
            LogLevel.BODY -> {
                logHeadersRsp(response, sb)
                //body.string会抛IO异常
                kotlin.runCatching {
                    //peek类似于clone数据流，监视，窥探,不能直接用原来的body的string流数据作为日志，会消费掉io，所以这里是peek，监测
                    val peekBody = response.peekBody(1024 * 1024)
                    val fromJson =
                        GsonUtils.fromJson<NetResponse>(peekBody.string(), NetResponse::class.java)
                    sb.appendLine(CniaoUtils.unicodeDecode(fromJson.toString()))
                    sb.appendLine("data: " + CniaoUtils.decodeData(fromJson.data.toString()))
                }.getOrNull()
            }
        }
        sb.appendLine("<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<-<<")
        logIt(sb, LogColor.INFO)
    }

    //region logRequest
    private fun logBasicReq(sb: StringBuilder, request: Request, connection: Connection?) {
        sb.appendLine(
            "请求 method: ${request.method}，" +
                    "url: ${decodeUrlStr(request.url.toString()) ?: ""}，" +
                    "tag: ${request.tag()}，" +
                    "protocol: ${connection?.protocol() ?: Protocol.HTTP_1_1}"
        )
    }

    private fun logHeadersReq(sb: StringBuilder, request: Request, connection: Connection?) {
        logBasicReq(sb, request, connection)
        val headers = request.headers.joinToString { header ->
            "请求 header:  ${header.first}=${header.second}\n"
        }
        sb.appendLine(headers)
    }

    private fun logBodyReq(sb: StringBuilder, request: Request, connection: Connection?) {
        logHeadersReq(sb, request, connection)
        sb.appendLine("请求体: ${request.body.toString()}")
    }
    //endregion

    //region logResponse
    private fun logBasicRsp(sb: StringBuffer, response: Response) {
        sb.appendLine("响应 protocol: ${response.protocol} code: ${response.code} message: ${response.message}")
            .appendLine("响应 request Url: ${decodeUrlStr(response.request.url.toString())}")
            .appendLine(
                "响应 发送请求事件sentRequestTime: ${toDateTimeStr(
                    response.sentRequestAtMillis,
                    MILLIS_PATTERN
                )} 响应时间receivedResponseTime: ${toDateTimeStr(
                    response.receivedResponseAtMillis,
                    MILLIS_PATTERN
                )}"
            )
    }

    private fun logHeadersRsp(response: Response, sb: StringBuffer) {
        logBasicRsp(sb, response)
        val headersStr = response.headers.joinToString(separator = "") { header ->
            "响应 Header: {${header.first}=${header.second}}\n"
        }
        sb.appendLine(headersStr)
    }

    //endregion

    /**
     * 解码
     */
    private fun decodeUrlStr(url: String): String? {
        return kotlin.runCatching {
            URLDecoder.decode(url, "UTF-8")
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()
    }

    private fun logIt(any: Any, color: LogColor? = null) {
        when (color ?: this.logColor) {
            LogColor.VERBOSE -> Log.v(
                TAG, any.toString()
            )
            LogColor.DEBUG -> Log.d(
                TAG, any.toString()
            )
            LogColor.INFO -> Log.i(
                TAG, any.toString()
            )
            LogColor.WARN -> Log.w(
                TAG, any.toString()
            )
            LogColor.ERROR -> Log.e(
                TAG, any.toString()
            )
        }
    }
}