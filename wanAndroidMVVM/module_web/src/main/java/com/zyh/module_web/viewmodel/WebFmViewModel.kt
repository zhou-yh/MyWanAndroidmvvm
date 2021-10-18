package com.zyh.module_web.viewmodel

import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import com.zyh.lib_base.base.BaseBean
import com.zyh.lib_base.base.BaseViewModel
import com.zyh.lib_base.base.MyApplication
import com.zyh.lib_base.binding.command.BindingAction
import com.zyh.lib_base.binding.command.BindingCommand
import com.zyh.lib_base.bus.event.SingleLiveEvent
import com.zyh.lib_base.data.DataRepository
import com.zyh.lib_base.event.LiveBusCenter
import com.zyh.lib_base.extension.ApiSubscriptHelper
import com.zyh.lib_base.utils.RxThreadHelper
import io.reactivex.Observable


/**
 * author: zhouyh
 * created on: 2021/9/22 4:27 下午
 * description: webFragment的viewModel
 */
class WebFmViewModel(application: MyApplication, model: DataRepository) :
    BaseViewModel<DataRepository>(application, model) {

    val uc = UIChangeEvent()
    val showWebLinkMenuFlag = ObservableField(false)
    val collectFlag = ObservableField(false)

    //向前
    val canForwardFlag = ObservableField(false)
    //回退
    val canGoBackFlag = ObservableField(false)
    /**
     * UI事件
     */
    class UIChangeEvent {
        val closeEvent: SingleLiveEvent<Void> = SingleLiveEvent()
        val collectEvent: SingleLiveEvent<Void> = SingleLiveEvent()
        val goForwardEvent: SingleLiveEvent<Void> = SingleLiveEvent()
        val showMenuEvent: SingleLiveEvent<Void> = SingleLiveEvent()
        val openBrowserEvent: SingleLiveEvent<Void> = SingleLiveEvent()
        val copyCurrentLinkEvent: SingleLiveEvent<Void> = SingleLiveEvent()
    }




    /**
     * 获取焦点事件
     */
    val onWebLinkFocusCommand :BindingCommand<Boolean> = BindingCommand{ focus->
        showWebLinkMenuFlag.set(focus)
    }

    /**
     * 复制链接
     */
    val copyLinkClickCommand: BindingCommand<Void> = BindingCommand(BindingAction {
        uc.copyCurrentLinkEvent.call()
        showWebLinkMenuFlag.set(false)
    })

    /**
     * 用浏览器打开链接
     */
    val openOnBrowserClick :BindingCommand<Void> = BindingCommand(BindingAction {
        uc.openBrowserEvent.call()
        showWebLinkMenuFlag.set(false)
    })

    /**
     * 收藏链接
     */
    val onCollectClickCommand : BindingCommand<Void> = BindingCommand(BindingAction {
        uc.collectEvent.call()
    })


    /**
     * 打开菜单事件
     */
    val onMenuClickCommand: BindingCommand<Void> = BindingCommand(BindingAction {
        uc.showMenuEvent.call()
    })

    /**
     * 前进事件
     */
    val onGoForwardClick: View.OnClickListener = View.OnClickListener {
        uc.goForwardEvent.call()
    }

    fun collectWebsite(name: String?, link: String?){
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(link)){
            showNormalToast("网站标题或链接不能为空~")
            return
        }

        model.collectWebsite(name!!,link!!)
            .compose(RxThreadHelper.rxSchedulerHelper())
            .subscribe(object : ApiSubscriptHelper<BaseBean<Any?>>(){
                override fun onResult(t: BaseBean<Any?>) {
                    if (t.errorCode == 0){
                        collectFlag.set(true)
                        showSuccessToast("已收藏")
                        showWebLinkMenuFlag.set(false)
                        LiveBusCenter.postRefreshWebListEvent()
                    }
                }

                override fun onFailed(msg: String?) {
                    showErrorToast(msg)
                }

            })
    }

    /**
     * 保存浏览历史
     */
    fun saveBrowseHistory(title: String, link: String) {
        model.saveUserBrowseHistory(title, link)
    }

}