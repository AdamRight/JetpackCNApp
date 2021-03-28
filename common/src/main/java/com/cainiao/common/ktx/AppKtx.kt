package com.cainiao.common.ktx

import android.app.Application

/**
 * Application相关的ktx扩展
 */

val Application.application: Application
    get() = this