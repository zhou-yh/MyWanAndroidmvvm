package com.zyh.lib_base.data.source

import com.zyh.lib_base.base.BaseBean
import com.zyh.lib_base.data.bean.*
import io.reactivex.Observable



/**
 * author: zhouyh
 * created on: 2021/5/11 5:11 下午
 * description:网络数据源
 */
interface HttpDataSource {
    fun userLogin(account: String, password: String) : Observable<BaseBean<UserBean>>
    fun register(username: String, password: String, repassword: String): Observable<BaseBean<Any?>>

    fun logout(): Observable<BaseBean<Any?>>

    //首页模块
    fun getBannerData():Observable<BaseBean<List<HomeBannerBean>>>
    fun getSearchHotKey():Observable<BaseBean<List<SearchHotKeyBean>>>
    fun getHomeArticle(page:String = "0"):Observable<BaseBean<HomeArticleBean>>
    fun getHomeTopArticle(): Observable<BaseBean<List<HomeArticleBean.Data>>>
    fun getHomeProject(page: String ="0"):Observable<BaseBean<ProjectBean>>
    fun collectArticle(articleId: Int): Observable<BaseBean<Any?>>
    fun unCollectArticle(articleId: Int): Observable<BaseBean<Any?>>

    //web模块
    fun collectWebsite(name: String, link: String): Observable<BaseBean<Any?>>

    //项目模块
    fun getProjectSort():Observable<BaseBean<List<ProjectSortBean>>>
    fun getProjectByCid(page: String = "1", cid: String): Observable<BaseBean<ProjectBean>>

    //用户模块
    fun getUserShareData(page: String = "1"): Observable<BaseBean<UserShareBean>>
    fun getUserScoreDetail(page: String = "1"): Observable<BaseBean<UserScoreDetailBean>>
    fun getUserScore(): Observable<BaseBean<UserScoreBean>>
    fun getCollectArticle(page: String = "0"): Observable<BaseBean<CollectArticleBean>>

    //广场模块
    fun getSquareList(page: Int = 0): Observable<BaseBean<SquareListBean>>

}