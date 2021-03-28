package com.cainiao.common.widget

import android.view.MenuItem
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * BottomNavigationView和ViewPager2关联的中介者
 */
class BnvMediator(
    private val bnvMain: BottomNavigationView,
    private val vp2Main: ViewPager2,
    private val lifecycle: Lifecycle,
    private val config: ((bnvMain: BottomNavigationView, vp2Main: ViewPager2) -> Unit)? = null
) : LifecycleObserver {
    //存储bottomNavigationView的menu的item和其自身position的对应关系
    private val mapOf = mutableMapOf<MenuItem, Int>()

    private val pageSelectedCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            bnvMain.selectedItemId = bnvMain.menu[position].itemId
        }
    }

    init {
        //初始化bnv的item和index对应关系
        bnvMain.menu.forEachIndexed { index, item ->
            mapOf[item] = index
        }
    }

    /**
     * 关联BottomNavigationView和ViewPager2的选择关系
     */
    fun attach() {
        lifecycle.addObserver(this)
        config?.invoke(bnvMain, vp2Main)

        vp2Main.registerOnPageChangeCallback(pageSelectedCallback)

        bnvMain.setOnNavigationItemSelectedListener {
            vp2Main.setCurrentItem(mapOf[it] ?: 0, false)
            true
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        vp2Main.unregisterOnPageChangeCallback(pageSelectedCallback)
    }
}