package com.zyh.lib_base.data.source

import com.zyh.lib_base.data.bean.HomeBannerBean
import com.zyh.lib_base.data.bean.UserBean
import io.reactivex.Flowable
import java.io.Serializable


/**
 * author: zhouyh
 * created on: 2021/5/11 5:11 下午
 * description:本地数据源
 */
interface LocalDataSource {

    fun getLoginName():String?
    fun saveUserData(userBean: UserBean)
    fun getUserData(): UserBean?
    fun getUserId(): Int
    fun saveUserBrowseHistory(title:String,url:String)
    //清除登录状态
    fun clearLoginState()




    fun saveReadHistoryState(visible: Boolean)
    fun getReadHistoryState(): Boolean



    //设置模块 保存跟随系统夜间模式设置
    fun saveFollowSysUiModelState(isFollow : Boolean = true)
    fun getFollowSysUiModelState():Boolean

    //用户夜间模式
    fun saveUserUiModelState(isFollow : Boolean = true)
    fun getUserUiModelState():Boolean

    //阅读历史
    fun saveReadHistoryFlag(isVisible:Boolean = true)
    fun getReadHistoryFlag():Boolean




    
    fun <T:Serializable>saveCacheListData(list: List<T>)
    fun <T:Serializable>getCacheListData(key:String):List<T>?
}