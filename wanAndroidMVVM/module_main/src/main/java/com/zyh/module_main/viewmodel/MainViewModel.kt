package com.zyh.module_main.viewmodel

import com.zyh.lib_base.base.BaseViewModel
import com.zyh.lib_base.base.MyApplication
import com.zyh.lib_base.binding.command.BindingCommand
import com.zyh.lib_base.binding.command.BindingConsumer
import com.zyh.lib_base.bus.event.SingleLiveEvent
import com.zyh.lib_base.data.DataRepository


/**
 * author: zhouyh
 * created on: 2021/9/18 5:35 下午
 * description:主页viewmodel
 */
class MainViewModel(application: MyApplication, model: DataRepository) :
    BaseViewModel<DataRepository>(application, model) {

    val uc = UiChangeEvent()

    inner class UiChangeEvent{
        val tabChangeLiveEvent:SingleLiveEvent<Int> = SingleLiveEvent()
        val pageChangeLiveEvent:SingleLiveEvent<Int> = SingleLiveEvent()
    }

    val onTabSelectedListener:BindingCommand<Int> = BindingCommand(BindingConsumer {
        uc.tabChangeLiveEvent.postValue(it)
    })

    val onPageSelectedListener:BindingCommand<Int> = BindingCommand(BindingConsumer {
        uc.pageChangeLiveEvent.postValue(it)
    })

}