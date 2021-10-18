package com.zyh.module_user.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.blankj.utilcode.util.*
import com.czl.lib_base.config.AppConstants
import com.zyh.lib_base.base.BaseBean
import com.zyh.lib_base.base.BaseViewModel
import com.zyh.lib_base.base.MyApplication
import com.zyh.lib_base.binding.command.BindingAction
import com.zyh.lib_base.binding.command.BindingCommand
import com.zyh.lib_base.binding.command.BindingConsumer
import com.zyh.lib_base.bus.event.SingleLiveEvent
import com.zyh.lib_base.data.DataRepository
import com.zyh.lib_base.event.LiveBusCenter
import com.zyh.lib_base.extension.ApiSubscriptHelper
import com.zyh.lib_base.utils.RxThreadHelper
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.flowable.FlowableTimer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * author: zhouyh
 * created on: 2021/9/28 6:32 下午
 * description:系统设置
 */
class UserSettingVm(application: MyApplication, model: DataRepository) :
    BaseViewModel<DataRepository>(application, model) {

    val uc = UIChangeEvent()
    //跟随系统夜间模式
    val followSysUiModelState : ObservableBoolean = ObservableBoolean(model.getFollowSysUiModelState())
    //用户设置夜间模式
    val userUiModelState : ObservableBoolean = ObservableBoolean(model.getUserUiModelState())
    //阅读历史
    val readHistoryState : ObservableBoolean = ObservableBoolean(model.getReadHistoryFlag())
    //缓存
    val cacheSize : ObservableField<String> = ObservableField("")
    //检测更新
    val versionText = ObservableField("V${AppUtils.getAppVersionName()}")
    val logoutVisible : ObservableBoolean = ObservableBoolean(true)



    class UIChangeEvent {

        val switchSystemModelEvent : SingleLiveEvent<Boolean> = SingleLiveEvent()
        val switchNightModelEvent : SingleLiveEvent<Boolean> = SingleLiveEvent()
        val confirmLogoutEvent : SingleLiveEvent<Void> = SingleLiveEvent()
        val checkUpdateEvent = SingleLiveEvent<Void>()
        val feedbackEvent = SingleLiveEvent<Void>()
    }


    val switchSysModelCheckCommand: BindingCommand<Boolean> = BindingCommand { checked ->
        followSysUiModelState.set(checked)
        uc.switchSystemModelEvent.postValue(checked)
    }

    val switchUserModelCheckCommand:BindingCommand<Boolean> = BindingCommand{ checked->
        //不随系统模式才发送
        if (!followSysUiModelState.get()){
            userUiModelState.set(checked)
            uc.switchNightModelEvent.postValue(checked)
        }
    }
    val onCleanCacheClick:BindingCommand<Void> = BindingCommand(BindingAction {
        if (cacheSize.get() == "0.00B"){
            return@BindingAction
        }
        addSubscribe(Flowable.create(FlowableOnSubscribe<Boolean> {
            it.onNext(CleanUtils.cleanInternalCache())
        },BackpressureStrategy.BUFFER)
            .compose(RxThreadHelper.rxFlowSchedulerHelper())
            .doOnSubscribe{}
            .subscribe{
                FlowableTimer.timer(300,TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        dismissLoading()
                        cacheSize.set("0.00B")
                        showSuccessToast("清理成功")
                    }
            }
        )
    })

    val onAboutUsClickCommand: BindingCommand<Void> = BindingCommand(BindingAction {
//        startContainerActivity(AppConstants.Router.User.F_ABOUT_US)
    })

    val logoutClickCommand:BindingCommand<Void> = BindingCommand(BindingAction {
        uc.confirmLogoutEvent.call()
    })



    /**
     *显示缓存
     */
    fun setTvCacheSize() {
        addSubscribe(Flowable.create(FlowableOnSubscribe<String> {
            it.onNext(
                ConvertUtils.byte2FitMemorySize(
                    FileUtils.getLength(PathUtils.getInternalAppCachePath()),
                    2)
            )
        },BackpressureStrategy.BUFFER)
            .compose(RxThreadHelper.rxFlowSchedulerHelper())
            .subscribe {
                cacheSize.set(it)
            })
    }

    fun logout() {
        model.logout().compose(RxThreadHelper.rxSchedulerHelper())
            .subscribe(object : ApiSubscriptHelper<BaseBean<*>>(){
                override fun onResult(t: BaseBean<*>) {
                    if (t.errorCode == 0){
                        logoutVisible.set(false)
                        model.clearLoginState()
                        LiveBusCenter.postLogoutEvent()
                    }
                }

                override fun onFailed(msg: String?) {
                    showErrorToast(msg)
                }

            })
    }


}