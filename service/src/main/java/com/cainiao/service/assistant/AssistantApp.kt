package com.cainiao.service.assistant

import android.app.Application
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.kit.AbstractKit

object AssistantApp {

    fun initConfig(application: Application) {
        val mutableListOf = mutableListOf<AbstractKit>()
        mutableListOf.add(ServerHostKit())
        DoraemonKit.install(application, mutableListOf)
    }
}