package com.zyh.lib_base.data.bean

import java.io.Serializable


/**
 * author: zhouyh
 * created on: 2021/5/11 10:49 下午
 * description:进入App第一帧缓存数据 避免每次进入app重新请求网络耗时耗流量 过期时长在AppConst类中控制
 */

data class HomeBannerCache(val homeBannerBeans: List<HomeBannerBean>):Serializable
data class HomeArticleCache(val homeArticleBeans: List<HomeArticleBean.Data>):Serializable
data class HomeSearchDataCache(val homeSearchDataBeans: List<SearchDataBean>):Serializable
data class HomeSquareCache(val squareCache: List<SquareListBean.Data>) : Serializable
data class HomeProjectSortCache(val sortCache: List<ProjectSortBean>) : Serializable
data class HomeProjectContentCache(val contentCache: List<ProjectBean.Data>) : Serializable
