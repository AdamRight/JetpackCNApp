package com.cainiao.login.net

import androidx.annotation.Keep
import com.cainiao.service.repo.CniaoUserInfo

/**
 * 查询手机号是否注册
 */
@Keep
data class RegisterRsp(
    val is_register: Int = FLAG_UN_REGISTERED
) {
    companion object {
        const val FLAG_IS_REGISTERED = 1   // 已经注册
        const val FLAG_UN_REGISTERED = 0 // 未注册
    }
}

/**
 * 手机号密码登录
 */
typealias LoginRsp = CniaoUserInfo