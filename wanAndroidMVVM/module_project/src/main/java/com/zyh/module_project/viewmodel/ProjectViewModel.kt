package com.zyh.module_project.viewmodel

import com.czl.lib_base.config.AppConstants
import com.zyh.lib_base.base.BaseBean
import com.zyh.lib_base.base.BaseViewModel
import com.zyh.lib_base.base.MyApplication
import com.zyh.lib_base.bus.event.SingleLiveEvent
import com.zyh.lib_base.data.DataRepository
import com.zyh.lib_base.data.bean.ProjectBean
import com.zyh.lib_base.data.bean.ProjectSortBean
import com.zyh.lib_base.extension.ApiSubscriptHelper
import com.zyh.lib_base.utils.RxThreadHelper


/**
 * author: zhouyh
 * created on: 2021/9/24 11:42 上午
 * description:
 */
class ProjectViewModel(application: MyApplication, model: DataRepository) :
    BaseViewModel<DataRepository>(application, model) {

    //加载完成事件
    val loadCompletedEvent: SingleLiveEvent<List<ProjectSortBean>> = SingleLiveEvent()

    /**
     * 获取网络数据
     */
    fun getProjectSort(){
        model.getProjectSort().compose(RxThreadHelper.rxSchedulerHelper())
            .subscribe(object:ApiSubscriptHelper<BaseBean<List<ProjectSortBean>>>(){
                override fun onResult(t: BaseBean<List<ProjectSortBean>>) {
                    if (t.errorCode == 0){
                        loadCompletedEvent.postValue(t.data)
                    }else{
                        loadCompletedEvent.postValue(null)
                    }
                }

                override fun onFailed(msg: String?) {
                    showErrorToast(msg)
                }

            })
    }

    /**
     * 获取缓存数据
     */
    fun getCacheSort():List<ProjectSortBean>{
        return model.getCacheListData(AppConstants.CacheKey.CACHE_PROJECT_SORT)?: emptyList()
    }


}