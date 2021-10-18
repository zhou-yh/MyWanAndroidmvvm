package com.zyh.lib_base.base


/**
 * author: zhouyh
 * created on: 2021/5/12 6:28 下午
 * description:
 */
interface IModel {
    /**
     * ViewModel销毁时清除Model，与ViewModel共消亡。Model层同样不能持有长生命周期对象
     */
    fun onCleared()
}