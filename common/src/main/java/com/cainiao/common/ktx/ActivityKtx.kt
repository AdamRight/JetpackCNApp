package com.cainiao.common.ktx

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.core.app.ComponentActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner

/**
 * Activity的ktx，扩展函数和属性
 */

//region 扩展函数

/**
 * Activity中使用DataBinding 简化setContentView
 * [layoutId] 布局文件
 * @return 返回一个ViewDataBinding实例
 */
fun <T : ViewDataBinding> Activity.bindView(@LayoutRes layoutId: Int): T {
    return DataBindingUtil.setContentView(this, layoutId)
}

/**
 * Activity中使用DataBinding 简化setContentView
 * [view]
 * @return 返回一个ViewDataBinding实例 可空
 */
fun <T : ViewDataBinding> Activity.bindView(view: View): T? {
    return DataBindingUtil.bind<T>(view)
}

/**
 * 沉浸式状态栏实现
 */
fun Activity.immediateStatusBar() {
    window.apply {
        addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}

/**
 * 隐藏输入法
 */
fun Activity.dismissKeyBoard(view: View) {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?
    inputManager?.hideSoftInputFromWindow(view.windowToken, 0)
}
//endregion

//region 扩展属性
/**
 * 扩展lifeCycleOwner属性，便于和Fragment之间使用lifeCycleOwner 一致性
 */
val ComponentActivity.viewLifeCycleOwner: LifecycleOwner
    get() = this

/**
 * Activity的扩展字段，便于和Fragment中使用liveData之类的时候，参数一致性
 */
val Activity.context: Context
    get() = this

//endregion