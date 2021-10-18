package com.zyh.lib_base.data.source

import com.zyh.lib_base.api.ApiService
import com.zyh.lib_base.base.BaseBean
import com.zyh.lib_base.data.bean.*
import io.reactivex.Observable


/**
 * author: zhouyh
 * created on: 2021/5/11 11:22 下午
 * description:
 */
class HttpDataImpl(private val apiService: ApiService):HttpDataSource{


    override fun userLogin(account: String, password: String): Observable<BaseBean<UserBean>> {
        return apiService.pwdLogin(account,password);
    }

    override fun register(
        username: String,
        password: String,
        repassword: String,
    ): Observable<BaseBean<Any?>> {
       return apiService.register(username,password,repassword)
    }

    override fun logout(): Observable<BaseBean<Any?>> {
        return apiService.logout()
    }

    override fun getBannerData(): Observable<BaseBean<List<HomeBannerBean>>> {
        return apiService.getBannerData()
    }

    override fun getSearchHotKey(): Observable<BaseBean<List<SearchHotKeyBean>>> {
        return apiService.getSearchHotKey()
    }

    override fun getHomeArticle(page: String): Observable<BaseBean<HomeArticleBean>> {
        return apiService.getHomeArticle(page)
    }

    override fun getHomeTopArticle(): Observable<BaseBean<List<HomeArticleBean.Data>>> {
        return apiService.getHomeTopArticle()
    }

    override fun getHomeProject(page: String): Observable<BaseBean<ProjectBean>> {
        return apiService.getHomeProject(page)
    }

    override fun collectArticle(articleId: Int): Observable<BaseBean<Any?>> {
        return apiService.collectArticle(articleId)
    }

    override fun unCollectArticle(articleId: Int): Observable<BaseBean<Any?>> {
        return apiService.unCollectArticle(articleId)
    }

    override fun collectWebsite(name: String, link: String): Observable<BaseBean<Any?>> {
        return apiService.collectWebsite(name,link)
    }

    override fun getProjectSort(): Observable<BaseBean<List<ProjectSortBean>>> {
        return apiService.getProjectSort()
    }

    override fun getProjectByCid(page: String, cid: String): Observable<BaseBean<ProjectBean>> {
        return apiService.getProjectByCid(page,cid)
    }

    override fun getUserShareData(page: String): Observable<BaseBean<UserShareBean>> {
        return apiService.getUserShareData(page)
    }

    override fun getUserScoreDetail(page: String): Observable<BaseBean<UserScoreDetailBean>> {
        return apiService.getUserScoreDetail(page)
    }

    override fun getUserScore(): Observable<BaseBean<UserScoreBean>> {
        return apiService.getUserScore()
    }

    override fun getCollectArticle(page: String): Observable<BaseBean<CollectArticleBean>> {
        return apiService.getCollectArticle(page)
    }

    override fun getSquareList(page: Int): Observable<BaseBean<SquareListBean>> {
        return apiService.getSquareList(page)
    }


}