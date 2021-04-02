package com.cainiao.jetpack

import com.alibaba.android.arouter.launcher.ARouter
import com.cainiao.common.BaseApplication
import com.cainiao.login.moduleLogin
import com.cainiao.service.assistant.AssistantApp
import com.cainiao.service.moduleService
import org.koin.core.context.loadKoinModules
import com.cainiao.common.ktx.application

class CnApplication : BaseApplication() {

    private val modules = arrayListOf(
        moduleService, moduleLogin
    )

    override fun initConfig() {
        super.initConfig()

        //doKit的初始化配置
        AssistantApp.initConfig(application)
        loadKoinModules(modules)
        ARouter.init(application)
    }
}