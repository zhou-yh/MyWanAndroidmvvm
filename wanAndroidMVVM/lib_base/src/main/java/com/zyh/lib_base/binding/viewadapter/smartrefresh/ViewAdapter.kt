package com.zyh.lib_base.binding.viewadapter.smartrefresh

import androidx.databinding.BindingAdapter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.zyh.lib_base.binding.command.BindingCommand


/**
 * author: zhouyh
 * created on: 2021/9/19 10:46 上午
 * description: 刷新和加载更多绑定
 */
object ViewAdapter{

    @JvmStatic
    @BindingAdapter("onRefreshCommand")
    fun onRefreshCommand(
        smartRefreshLayout:SmartRefreshLayout, onRefreshCommand: BindingCommand<*>?){
        smartRefreshLayout.setOnRefreshListener {
            onRefreshCommand?.execute()
        }
    }

    @JvmStatic
    @BindingAdapter("onLoadMoreCommand")
    fun onLoadMoreCommand(
        smartRefreshLayout:SmartRefreshLayout, onLoadMoreCommand: BindingCommand<*>?){
        smartRefreshLayout.setOnLoadMoreListener() {
            onLoadMoreCommand?.execute()
        }
    }



}