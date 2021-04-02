package com.cainiao.login

import com.alibaba.android.arouter.facade.annotation.Route
import com.cainiao.common.base.BaseActivity
import com.cainiao.common.network.config.SP_KEY_USER_TOKEN
import com.cainiao.common.utils.CniaoSpUtils
import com.cainiao.login.databinding.ActivityLoginBinding
import com.cainiao.login.net.RegisterRsp
import com.cainiao.service.repo.CniaoDbHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

@Route(path = "/login/login")
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private val loginViewModel: LoginViewModel by viewModel()

    override fun getLayoutId() = R.layout.activity_login

    override fun initView() {
        super.initView()
        dataBinding?.apply {
            vm = loginViewModel
            mtoolbarLogin.setNavigationOnClickListener { finish() }
        }
    }

    override fun initConfig() {
        super.initConfig()
        loginViewModel.apply {
            liveRegisterRsp.observerKt {
                if (it?.is_register == RegisterRsp.FLAG_IS_REGISTERED) {
                    repoLogin()
                }
            }

            liveLoginRsp.observerKt {
                it?.run {
                    CniaoDbHelper.insertUserInfo(this@LoginActivity, it)
                    CniaoSpUtils.put(SP_KEY_USER_TOKEN, it.token)
                    finish()
                }
            }
        }

    }
}