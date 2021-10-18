package com.zyh.lib_base.data.db

import com.zyh.lib_base.data.bean.SearchHotKeyBean
import org.litepal.LitePal
import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import org.litepal.extension.find


/**
 * author: zhouyh
 * created on: 2021/9/23 3:19 下午
 * description:用户实体类
 */
data class UserEntity(
    @Column(unique = true)
    var uid : Int,
    var userName : String?,
    var historyEntities:ArrayList<SearchHistoryEntity> = ArrayList(),
    var browserEntities:ArrayList<WebHistoryEntity> = ArrayList()
) : LitePalSupport(){
    val id : Long = 0

    fun getRecentHistory() : List<SearchHistoryEntity>{
        val allHistory = LitePal.select("history").where("userentity_id=?",id.toString())
            .find<SearchHistoryEntity>().reversed()
        if (allHistory.isNullOrEmpty())return arrayListOf()
        return if (allHistory.size>=5) return allHistory.subList(0,5)else allHistory
    }

    fun getAllHistory():List<SearchHistoryEntity>{
        return LitePal.where("userentity_id=?",id.toString()).find()
    }

    fun getBrowserHistory():List<WebHistoryEntity>{
        return LitePal.where("userentity_id=?",id.toString()).order("browserData desc").find()
    }
}
