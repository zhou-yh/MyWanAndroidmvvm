package com.zyh.lib_base.data

import com.zyh.lib_base.base.BaseBean
import com.zyh.lib_base.base.BaseModel
import com.zyh.lib_base.data.bean.*
import com.zyh.lib_base.data.source.HttpDataSource
import com.zyh.lib_base.data.source.LocalDataSource
import io.reactivex.Observable
import java.io.Serializable


/**
 * author: zhouyh
 * created on: 2021/5/11 4:59 下午
 * description:数据中心（本地+在线） 外部通过Koin依赖注入调用
 * 对于缓存或者在线数据的增删查改统一通过该数据仓库调用
 */
class DataRepository constructor(
    private val mHttpDataSource: HttpDataSource,
    private val mLocalDataSource: LocalDataSource,
) : BaseModel(), LocalDataSource, HttpDataSource {



    override fun userLogin(account: String, password: String): Observable<BaseBean<UserBean>> {
        return mHttpDataSource.userLogin(account,password)
    }



    override fun register(
        username: String,
        password: String,
        repassword: String
    ): Observable<BaseBean<Any?>> {
        return mHttpDataSource.register(username, password, repassword)
    }

    override fun logout(): Observable<BaseBean<Any?>> {
        return mHttpDataSource.logout()
    }

    override fun getBannerData(): Observable<BaseBean<List<HomeBannerBean>>> {
        return mHttpDataSource.getBannerData()
    }

    override fun getSearchHotKey(): Observable<BaseBean<List<SearchHotKeyBean>>> {
        return mHttpDataSource.getSearchHotKey()
    }

    override fun getHomeArticle(page:String): Observable<BaseBean<HomeArticleBean>> {
        return mHttpDataSource.getHomeArticle(page)
    }

    override fun getHomeTopArticle(): Observable<BaseBean<List<HomeArticleBean.Data>>> {
        return mHttpDataSource.getHomeTopArticle()
    }

    override fun getHomeProject(page: String): Observable<BaseBean<ProjectBean>> {
        return mHttpDataSource.getHomeProject(page)
    }

    override fun collectArticle(articleId: Int): Observable<BaseBean<Any?>> {
        return mHttpDataSource.collectArticle(articleId)
    }

    override fun unCollectArticle(articleId: Int): Observable<BaseBean<Any?>> {
        return mHttpDataSource.unCollectArticle(articleId)
    }

    override fun collectWebsite(name: String, link: String): Observable<BaseBean<Any?>> {
        return mHttpDataSource.collectWebsite(name,link)
    }

    override fun getProjectSort(): Observable<BaseBean<List<ProjectSortBean>>> {
        return mHttpDataSource.getProjectSort()
    }

    override fun getProjectByCid(page: String, cid: String): Observable<BaseBean<ProjectBean>> {
        return mHttpDataSource.getProjectByCid(page,cid)
    }

    override fun getUserShareData(page: String): Observable<BaseBean<UserShareBean>> {
        return mHttpDataSource.getUserShareData(page)
    }

    override fun getUserScoreDetail(page: String): Observable<BaseBean<UserScoreDetailBean>> {
        return mHttpDataSource.getUserScoreDetail(page)
    }

    override fun getUserScore(): Observable<BaseBean<UserScoreBean>> {
       return mHttpDataSource.getUserScore()
    }

    override fun getCollectArticle(page: String): Observable<BaseBean<CollectArticleBean>> {
        return mHttpDataSource.getCollectArticle(page)
    }

    override fun getSquareList(page: Int): Observable<BaseBean<SquareListBean>> {
        return mHttpDataSource.getSquareList(page)
    }


    override fun saveUserData(userBean: UserBean) {
        mLocalDataSource.saveUserData(userBean)
    }

    override fun getUserData(): UserBean? {
        return mLocalDataSource.getUserData()
    }

    override fun getUserId(): Int {
        return mLocalDataSource.getUserId()
    }

    override fun <T : Serializable> saveCacheListData(list: List<T>) {
        return mLocalDataSource.saveCacheListData(list)
    }

    override fun <T : Serializable> getCacheListData(key: String): List<T>? {
        return mLocalDataSource.getCacheListData(key)
    }


    override fun getLoginName(): String?{
        return mLocalDataSource.getLoginName()
    }

    override fun saveUserBrowseHistory(title: String, link: String) {
        return mLocalDataSource.saveUserBrowseHistory(title,link)
    }

    override fun clearLoginState() {
        return mLocalDataSource.clearLoginState()
    }

    override fun saveReadHistoryState(visible: Boolean) {
        return mLocalDataSource.saveReadHistoryState(visible)
    }

    override fun getReadHistoryState(): Boolean {
        return mLocalDataSource.getReadHistoryState()
    }

    override fun saveFollowSysUiModelState(isFollow: Boolean) {
        return mLocalDataSource.saveFollowSysUiModelState(isFollow)
    }

    override fun getFollowSysUiModelState(): Boolean {
        return mLocalDataSource.getFollowSysUiModelState()
    }

    override fun saveUserUiModelState(isFollow: Boolean) {
        return mLocalDataSource.saveUserUiModelState(isFollow)
    }

    override fun getUserUiModelState(): Boolean {
        return mLocalDataSource.getUserUiModelState()
    }

    override fun saveReadHistoryFlag(isVisible: Boolean) {
        return mLocalDataSource.saveReadHistoryFlag(isVisible)
    }

    override fun getReadHistoryFlag(): Boolean {
        return mLocalDataSource.getReadHistoryFlag()
    }


}