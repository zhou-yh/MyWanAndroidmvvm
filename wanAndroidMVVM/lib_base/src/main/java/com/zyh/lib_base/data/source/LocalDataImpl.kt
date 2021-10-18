package com.zyh.lib_base.data.source

import com.blankj.utilcode.util.CacheDiskUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.czl.lib_base.config.AppConstants
import com.google.gson.reflect.TypeToken
import com.zyh.lib_base.data.bean.*
import com.zyh.lib_base.data.db.UserEntity
import com.zyh.lib_base.data.db.WebHistoryEntity
import com.zyh.lib_base.utils.SpHelper
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.litepal.LitePal
import org.litepal.extension.findFirst
import java.io.Serializable


/**
 * author: zhouyh
 * created on: 2021/5/11 11:22 下午
 * description:本地数据源实现类
 */
class LocalDataImpl : LocalDataSource {


    override fun getLoginName(): String? {
        return SpHelper.decodeString(AppConstants.SpKey.LOGIN_NAME)
    }


    override fun saveUserData(userBean: UserBean) {
        SpHelper.encode(AppConstants.SpKey.USER_ID, userBean.id)
        SpHelper.encode(AppConstants.SpKey.LOGIN_NAME, userBean.publicName)
        SpHelper.encode(AppConstants.SpKey.USER_JSON_DATA,
            GsonUtils.toJson(userBean, object : TypeToken<UserBean>() {}.type))

    }

    override fun getUserData(): UserBean? {
        val userJsonData = SpHelper.decodeString(AppConstants.SpKey.USER_JSON_DATA)
        return if (userJsonData.isBlank())
            return null
        else GsonUtils.fromJson(
            userJsonData,
            object : TypeToken<UserBean>() {}.type
        )
    }

    override fun getUserId(): Int {
        return SpHelper.decodeInt(AppConstants.SpKey.USER_ID)
    }

    /**
     * 保存用户浏览历史
     */
    override fun saveUserBrowseHistory(title: String, url: String) {
        Flowable.just(1)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                if (getUserId()==0){
                    return@subscribe
                }
                //找到当前用户的数据
                val entity = LitePal.where("uid=?",getUserId().toString()).findFirst<UserEntity>()
                val allWebHistory = entity?.getBrowserHistory()
                //遍历去重 并删除
                if (!allWebHistory.isNullOrEmpty()){
                    allWebHistory.filter { x -> x.webLink == url && x.webTitle == title }
                        .forEach{ y -> y.delete()}
                }
                val userEntity = UserEntity(getUserId(),getLoginName())
                val webHistoryEntity = WebHistoryEntity(title,url,System.currentTimeMillis().toInt(),userEntity)
                userEntity.browserEntities.add(webHistoryEntity)
                webHistoryEntity.userEntity = userEntity
                userEntity.saveOrUpdate("uid=?",userEntity.uid.toString())
                webHistoryEntity.save()
            }){
                it.printStackTrace()
                LogUtils.e("阅读历史保存失败，error=${it.message}")
            }
    }

    override fun clearLoginState() {
        SpHelper.clearAll();
    }

    override fun saveReadHistoryState(visible: Boolean) {
        SpHelper.encode(AppConstants.SpKey.READ_HISTORY_STATE,visible)
    }

    override fun getReadHistoryState(): Boolean {
        return SpHelper.decodeBoolean(AppConstants.SpKey.READ_HISTORY_STATE,true)
    }

    override fun saveFollowSysUiModelState(isFollow: Boolean) {
        SpHelper.encode(AppConstants.SpKey.SYS_UI_MODE,isFollow)
    }

    override fun getFollowSysUiModelState():Boolean{
        return SpHelper.decodeBoolean(AppConstants.SpKey.SYS_UI_MODE,true)
    }

    override fun saveUserUiModelState(isFollow: Boolean) {
        return SpHelper.encode(AppConstants.SpKey.USER_UI_MODE,isFollow)
    }

    override fun getUserUiModelState(): Boolean {
        return SpHelper.decodeBoolean(AppConstants.SpKey.USER_UI_MODE,false)
    }

    override fun saveReadHistoryFlag(isVisible: Boolean) {
        return SpHelper.encode(AppConstants.SpKey.READ_HISTORY_STATE,isVisible)
    }

    override fun getReadHistoryFlag(): Boolean {
        return SpHelper.decodeBoolean(AppConstants.SpKey.READ_HISTORY_STATE,true)
    }

    override fun <T : Serializable> saveCacheListData(list: List<T>) {
        if (list.isNotEmpty()) {
            when (list[0]) {
                is HomeBannerBean -> {
                    CacheDiskUtils.getInstance().put(
                        AppConstants.CacheKey.CACHE_HOME_BANNER,
                        HomeBannerCache(list as List<HomeBannerBean>),
                        AppConstants.CacheKey.CACHE_SAVE_TIME_SECONDS
                    )
                }
                is HomeArticleBean.Data->{
                    CacheDiskUtils.getInstance().put(
                        AppConstants.CacheKey.CACHE_HOME_ARTICLE,
                        HomeArticleCache(list as List<HomeArticleBean.Data>),
                        AppConstants.CacheKey.CACHE_SAVE_TIME_SECONDS
                    )
                }

                is ProjectSortBean->{
                    CacheDiskUtils.getInstance().put(
                        AppConstants.CacheKey.CACHE_PROJECT_SORT,
                        HomeProjectSortCache(list as List<ProjectSortBean>),
                        AppConstants.CacheKey.CACHE_SAVE_TIME_SECONDS
                    )
                }

                is ProjectBean.Data ->{
                    CacheDiskUtils.getInstance().put(
                        AppConstants.CacheKey.CACHE_PROJECT_CONTENT,
                        HomeProjectContentCache(list as List<ProjectBean.Data>),
                        AppConstants.CacheKey.CACHE_SAVE_TIME_SECONDS
                    )
                }

                is SquareListBean.Data ->{
                    CacheDiskUtils.getInstance().put(
                        AppConstants.CacheKey.CACHE_SQUARE_LIST,
                        HomeSquareCache(list as List<SquareListBean.Data>),
                        AppConstants.CacheKey.CACHE_SAVE_TIME_SECONDS
                    )
                }
            }

        }
    }

    override fun <T : Serializable> getCacheListData(key: String): List<T>? {
        return when (val serializable = CacheDiskUtils.getInstance().getSerializable(key)) {
            is HomeBannerCache? -> {
                serializable?.homeBannerBeans as List<T>?
            }
            is HomeArticleCache? ->{
                serializable?.homeArticleBeans as List<T>
            }
            is HomeSquareCache? -> {
                serializable?.squareCache as List<T>
            }
            is HomeProjectSortCache?->{
                serializable?.sortCache as List<T>
            }
            is HomeProjectContentCache?->{
                serializable?.contentCache as List<T>
            }
            else -> emptyList()

        }
    }
}