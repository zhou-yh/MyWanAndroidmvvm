package com.zyh.module_user.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.czl.lib_base.config.AppConstants
import com.zyh.lib_base.base.BaseBean
import com.zyh.lib_base.base.BaseViewModel
import com.zyh.lib_base.base.MyApplication
import com.zyh.lib_base.binding.command.BindingAction
import com.zyh.lib_base.binding.command.BindingCommand
import com.zyh.lib_base.bus.event.SingleLiveEvent
import com.zyh.lib_base.data.DataRepository
import com.zyh.lib_base.data.bean.CollectArticleBean
import com.zyh.lib_base.data.bean.UserShareBean
import com.zyh.lib_base.extension.ApiSubscriptHelper
import com.zyh.lib_base.utils.RxThreadHelper


/**
 * author: zhouyh
 * created on: 2021/9/23 6:06 下午
 * description: 用户界面viewModel
 */
class UserViewModel(application: MyApplication, model: DataRepository) :
    BaseViewModel<DataRepository>(application, model) {

    var tvScore = ObservableField("0")
    var tvCollect = ObservableField("0")
    var tvShare = ObservableField("0")
    val historyVisible = ObservableBoolean(model.getReadHistoryState())

    var firstSuccessLoadFlag = false


    val uc = UIChangeEvent()

    class UIChangeEvent {
        //刷新数据事件
        val refreshEvent: SingleLiveEvent<Void> = SingleLiveEvent()

        //未登录，弹出登录事件
        val showLoginPopEvent: SingleLiveEvent<Void> = SingleLiveEvent()
    }

    //刷新事件
    val onRefreshCommand: BindingCommand<Void> = BindingCommand(BindingAction {
        getUserCollectData()
        getUserShareData()
        uc.refreshEvent.call()
    })

    //设置事件
    val onSettingClickCommand: BindingCommand<Void> = BindingCommand(BindingAction {
        startContainerActivity(AppConstants.Router.User.F_USER_SETTING)
    })

    val onUserNameClickCommand:BindingCommand<Void> = BindingCommand(BindingAction {
        uc.showLoginPopEvent.call()
    })

    //积分事件
    val onScoreClickCommand: BindingCommand<Void> = BindingCommand(BindingAction {

    })

    //收藏事件
    val onCollectClickCommand: BindingCommand<Void> = BindingCommand(BindingAction {

    })

    //分享事件
    val onShareClickCommand: BindingCommand<Void> = BindingCommand(BindingAction {

    })

    //待办事件
    val onTodoClickCommand: BindingCommand<Void> = BindingCommand(BindingAction {

    })

    //阅读历史事件
    val onReadHistoryClickCommand: BindingCommand<Void> = BindingCommand(BindingAction {

    })


    //获取分享数据
    fun getUserShareData() {
        model.getUserShareData()
            .compose(RxThreadHelper.rxSchedulerHelper(this))
            .subscribe(object : ApiSubscriptHelper<BaseBean<UserShareBean>>(){
                override fun onResult(t: BaseBean<UserShareBean>) {
                    if (t.errorCode == 0){
                        firstSuccessLoadFlag = true
                        t.data?.let {
                            tvScore.set(it.coinInfo.coinCount.toString())
                            tvShare.set(it.coinInfo.coinCount.toString())
                        }
                    }else{
                        firstSuccessLoadFlag = false
                    }
                }

                override fun onFailed(msg: String?) {
                    firstSuccessLoadFlag = false
                }

            })
    }

    //获取收藏
    fun getUserCollectData() {
        model.getCollectArticle()
            .compose(RxThreadHelper.rxSchedulerHelper())
            .subscribe(object : ApiSubscriptHelper<BaseBean<CollectArticleBean>>(){
                override fun onResult(t: BaseBean<CollectArticleBean>) {
                    if (t.errorCode == 0){
                        firstSuccessLoadFlag = true
                        t.data?.let {
                            tvCollect.set(it.datas.size.toString())
                        }
                    }else{
                        firstSuccessLoadFlag = false
                    }
                }

                override fun onFailed(msg: String?) {
                    firstSuccessLoadFlag = false
                }

            })
    }
}