package com.cainiao.common.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.cainiao.common.ktx.bindView
import com.cainiao.common.ktx.viewLifeCycleOwner

/**
 * 基类Activity
 */
abstract class BaseActivity<ActivityDataBinding : ViewDataBinding> : AppCompatActivity() {

    protected var dataBinding: ActivityDataBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = bindView<ActivityDataBinding>(getLayoutId()).also {
            it.lifecycleOwner = viewLifeCycleOwner
        }
        initConfig()
        initView()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        dataBinding?.unbind()
    }

    /**
     * 扩展liveData的observe函数
     */
    protected fun <T : Any> LiveData<T>.observerKt(block: (T?) -> Unit) {
        this.observe(viewLifeCycleOwner, Observer {
            block.invoke(it)
        })
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    open fun initConfig() {
    }

    open fun initView() {

    }

    open fun initData() {

    }
}