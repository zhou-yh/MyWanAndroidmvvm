package com.zyh.module_project.viewmodel

import android.view.View
import com.czl.lib_base.config.AppConstants
import com.zyh.lib_base.base.BaseBean
import com.zyh.lib_base.base.BaseViewModel
import com.zyh.lib_base.base.MyApplication
import com.zyh.lib_base.binding.command.BindingAction
import com.zyh.lib_base.binding.command.BindingCommand
import com.zyh.lib_base.bus.event.SingleLiveEvent
import com.zyh.lib_base.data.DataRepository
import com.zyh.lib_base.data.bean.ProjectBean
import com.zyh.lib_base.extension.ApiSubscriptHelper
import com.zyh.lib_base.utils.RxThreadHelper


/**
 * author: zhouyh
 * created on: 2021/9/27 2:52 下午
 * description:具体内容viewmodel
 */
class ContentViewModel(application: MyApplication, model: DataRepository) :
    BaseViewModel<DataRepository>(application, model) {

    var currentPage = 1
    var cid : String ?= null

    val uc = UIChangeEvent()

    class UIChangeEvent {
        val moveTopClick : SingleLiveEvent<Void> = SingleLiveEvent()
        val refreshCompleteEvent:SingleLiveEvent<ProjectBean> = SingleLiveEvent()
    }

    /**
     * 下拉刷新
     */
    val onRefreshCommand: BindingCommand<Void> = BindingCommand(BindingAction {
        currentPage = 0
        getProjectDataByCid()
    })


    /**
     * 加载更多
     */
    val onLoadMoreCommand: BindingCommand<Void> = BindingCommand(BindingAction {
        getProjectDataByCid()
    })

    /**
     * 返回顶部
     */
    val moveTopClick = View.OnClickListener{
       uc.moveTopClick.call()
    }



    private fun getProjectDataByCid() {
        cid?:uc.refreshCompleteEvent.postValue(null)
        cid?.let {
            model.getProjectByCid((currentPage+1).toString(), it)
                .compose(RxThreadHelper.rxSchedulerHelper())
                .subscribe(object :ApiSubscriptHelper<BaseBean<ProjectBean>>(){
                    override fun onResult(t: BaseBean<ProjectBean>) {
                        if (t.errorCode == 0){
                            currentPage++
                            uc.refreshCompleteEvent.postValue(t.data)
                        }else{
                            uc.refreshCompleteEvent.postValue(null)
                        }
                    }

                    override fun onFailed(msg: String?) {
                        showErrorToast(msg)
                        uc.refreshCompleteEvent.postValue(null)
                    }

                })
        }
    }


    /**
     * 获取缓存数据
     */
    fun getCacheList():List<ProjectBean.Data>{
        return model.getCacheListData(AppConstants.CacheKey.CACHE_PROJECT_CONTENT)?: emptyList()
    }




}