package com.zyh.module_square.viewmodel

import android.view.View
import com.czl.lib_base.config.AppConstants
import com.zyh.lib_base.base.BaseBean
import com.zyh.lib_base.base.BaseViewModel
import com.zyh.lib_base.base.MyApplication
import com.zyh.lib_base.binding.command.BindingAction
import com.zyh.lib_base.binding.command.BindingCommand
import com.zyh.lib_base.bus.event.SingleLiveEvent
import com.zyh.lib_base.data.DataRepository
import com.zyh.lib_base.data.bean.SquareListBean
import com.zyh.lib_base.extension.ApiSubscriptHelper
import com.zyh.lib_base.utils.RxThreadHelper


/**
 * author: zhouyh
 * created on: 2021/9/24 9:41 上午
 * description:
 */
class SquareViewModel(application: MyApplication, model: DataRepository) :
    BaseViewModel<DataRepository>(application, model) {

    var currentPage = 0
    val uc = UIChangeEvent()

    class UIChangeEvent {
        val onLoadCompleteEvent:SingleLiveEvent<SquareListBean?> = SingleLiveEvent()
        val onScrollTopEvent:SingleLiveEvent<Void> = SingleLiveEvent()

    }


    val onRefreshCommand:BindingCommand<Void> = BindingCommand(BindingAction {
        currentPage = -1
        getSquareData()
    })

    val onLoadMoreCommand:BindingCommand<Void> = BindingCommand(BindingAction {
        getSquareData()
    })



    val onEditShareClick: BindingCommand<Void> = BindingCommand(BindingAction {

    })


    val onScrollTopCommand: View.OnClickListener = View.OnClickListener {
        uc.onScrollTopEvent.call()
    }


    private fun getSquareData() {
        model.getSquareList(currentPage+1)
            .compose(RxThreadHelper.rxSchedulerHelper())
            .subscribe(object : ApiSubscriptHelper<BaseBean<SquareListBean>>(){
                override fun onResult(t: BaseBean<SquareListBean>) {
                    if (t.errorCode == 0){
                        currentPage++
                        uc.onLoadCompleteEvent.postValue(t.data)
                    }else{
                        uc.onLoadCompleteEvent.postValue(null)
                    }
                }

                override fun onFailed(msg: String?) {
                    showErrorToast(msg)
                    uc.onLoadCompleteEvent.postValue(null)
                }

            })
    }

    fun getCacheData() : List<SquareListBean.Data>{
        return model.getCacheListData(AppConstants.CacheKey.CACHE_SQUARE_LIST)?: emptyList()
    }



}