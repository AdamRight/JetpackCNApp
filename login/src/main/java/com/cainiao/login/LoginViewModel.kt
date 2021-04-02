package com.cainiao.login

import android.content.Context
import android.view.View
import androidx.databinding.ObservableField
import com.blankj.utilcode.util.ToastUtils
import com.cainiao.common.base.BaseViewModel
import com.cainiao.login.net.LoginReqBody
import com.cainiao.login.repo.ILoginResource

/**
 * 登录界面逻辑的viewModel
 */
class LoginViewModel(private val resource: ILoginResource) : BaseViewModel() {

    //账号，密码 的observable 对象
    val obMobile = ObservableField<String>()
    val obPassword = ObservableField<String>()


    val liveRegisterRsp = resource.registerRsp
    val liveLoginRsp = resource.loginRsp

    private fun checkRegister(get: String) = serverAwait {
        resource.checkRegister(get)
    }

    /**
     * 调用登录
     * val mobi: String = "18648957777",
     * val password: String = "cn5123456"
     */
    internal fun repoLogin() {
        val account = obMobile.get() ?: return
        val password = obPassword.get() ?: return
        serverAwait {
            resource.requestLogin(LoginReqBody(account, password))
        }
    }

    fun goLogin() {
        val account = obMobile.get() ?: return
        checkRegister(account)
    }

    //region 未实现
    fun wechat(ctx: Context) {
        ToastUtils.showShort("点击了微信登录")
    }

    fun qq(v: View) {
        ToastUtils.showShort("点击了QQ登录")
    }

    fun weibo() {
        ToastUtils.showShort("点击了微博登录")
    }

    fun AA(view: View) {
        ToastUtils.showShort("静态点击方式")
    }
    //endregion
}