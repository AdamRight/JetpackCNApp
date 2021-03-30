package com.cainiao.service.repo

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 数据库操作帮助类
 */
object CniaoDbHelper {

    /**
     * 新增登录用户保存到数据库中
     */
    fun insertUserInfo(context: Context, userInfo: CniaoUserInfo) {
        GlobalScope.launch(Dispatchers.IO) {
            CniaoDatabase.getInstance(context)
                .userDao
                .insertUser(userInfo)
        }
    }

    fun updateUserInfo(context: Context, userInfo: CniaoUserInfo) {
        GlobalScope.launch(Dispatchers.IO) {
            CniaoDatabase.getInstance(context)
                .userDao
                .updateUser(userInfo)
        }
    }

    /**
     * 推出登录删除数据库中的用户信息
     */
    fun deleteUserInfo(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            getUserInfo(context)?.let { info ->
                CniaoDatabase.getInstance(context).userDao.deleteUser(info)
            }
        }
    }

    /**
     * 获取room数据表中存储的userInfo
     * return liveData形式
     */
    fun getLiveUserInfo(context: Context) =
        CniaoDatabase.getInstance(context).userDao.queryLiveUser()

    /**
     * 以普通数据对象的形式，获取userInfo
     */
    fun getUserInfo(context: Context) = CniaoDatabase.getInstance(context).userDao.queryUser()

}