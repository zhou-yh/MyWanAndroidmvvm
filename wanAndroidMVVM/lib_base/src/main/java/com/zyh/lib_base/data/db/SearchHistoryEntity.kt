package com.zyh.lib_base.data.db

import org.litepal.crud.LitePalSupport


/**
 * author: zhouyh
 * created on: 2021/9/23 3:22 下午
 * description: 历史搜索实体
 */
data class SearchHistoryEntity(
    var history:String,
    var searchData:Int,
    var userEntity: UserEntity
):LitePalSupport()
