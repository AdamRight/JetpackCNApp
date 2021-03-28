package com.cainiao.common.network.config

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

internal class LocalCookieKar : CookieJar {

    /**
     * 本地cookies持久化存储
     */
    private val cookies = mutableListOf<Cookie>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        // 过期cookies
        val invalidCookies = mutableListOf<Cookie>()
        // 有效cookies
        val validCookies = mutableListOf<Cookie>()
        for (cookie in cookies) {
            if (cookie.expiresAt < System.currentTimeMillis()) {
                // 判断是否有效
                invalidCookies.add(cookie)
            } else if (cookie.matches(url)) {
                // 匹配对应的url
                validCookies.add(cookie)
            }
        }
        //移除无效的cookie
        cookies.removeAll(invalidCookies)
        return validCookies
    }

    /**
     * 保存Cookies
     */
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        this.cookies.addAll(cookies)
    }
}