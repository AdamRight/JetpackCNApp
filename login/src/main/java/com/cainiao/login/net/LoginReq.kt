package com.cainiao.login.net

import androidx.annotation.Keep

@Keep
data class LoginReqBody(
    val mobi: String,
    val password: String
)