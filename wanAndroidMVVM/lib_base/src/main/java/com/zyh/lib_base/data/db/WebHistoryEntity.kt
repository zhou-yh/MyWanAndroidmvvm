package com.zyh.lib_base.data.db

import org.litepal.crud.LitePalSupport


/**
 * author: zhouyh
 * created on: 2021/9/23 3:24 下午
 * description: 历史web浏览实体
 */
data class WebHistoryEntity(
    var webTitle:String,
    var webLink:String,
    var browserData:Int,
    var userEntity: UserEntity
):LitePalSupport()
