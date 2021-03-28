package com.cainiao.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.observe

/**
 * Fragment的抽象基类
 */
abstract class BaseFragment<FragmentViewDataBinding : ViewDataBinding> : Fragment() {

    protected var dataBinding: FragmentViewDataBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return dataBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding?.lifecycleOwner = viewLifecycleOwner
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
        this.observe(viewLifecycleOwner) { data ->
            block(data)
        }
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