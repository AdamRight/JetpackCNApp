package com.cainiao.login

import com.cainiao.common.network.KtRetrofit
import com.cainiao.common.network.config.BASE_URL
import com.cainiao.login.net.LoginService
import com.cainiao.login.repo.ILoginResource
import com.cainiao.login.repo.LoginRepo
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Service模块相关的Koin module配置
 */
val moduleLogin: Module = module {

    single {
        KtRetrofit.initConfig(BASE_URL)
            .getService(LoginService::class.java)
    }

    single {
        LoginRepo(get())
    } bind ILoginResource::class

    viewModel {
        LoginViewModel(get())
    }
}