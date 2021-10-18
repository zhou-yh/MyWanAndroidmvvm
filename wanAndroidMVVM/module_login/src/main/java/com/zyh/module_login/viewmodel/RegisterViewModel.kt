package com.zyh.module_login.viewmodel

import android.view.View
import androidx.databinding.ObservableField
import com.zyh.lib_base.base.*
import com.zyh.lib_base.binding.command.BindingAction
import com.zyh.lib_base.binding.command.BindingCommand
import com.zyh.lib_base.binding.command.BindingConsumer
import com.zyh.lib_base.data.DataRepository
import com.zyh.lib_base.event.LiveBusCenter
import com.zyh.lib_base.extension.ApiSubscriptHelper
import com.zyh.lib_base.utils.RxThreadHelper
import java.util.*


/**
 * author: zhouyh
 * created on: 2021/9/17 11:36 下午
 * description: 注册viewModel
 * 绑定application和数据源
 */
class RegisterViewModel(application: MyApplication, model: DataRepository) :
    BaseViewModel<DataRepository>(application, model) {

    var account:ObservableField<String> =  ObservableField("")
    var password:ObservableField<String> = ObservableField("")
    var repassword:ObservableField<String> = ObservableField("")

    val onAccountChangeCommand:BindingCommand<String> = BindingCommand(BindingConsumer{
        account.set(it)
    })

    val onPasswordChangeCommand:BindingCommand<String> = BindingCommand(BindingConsumer{
        password.set(it)
    })

    val onRepasswordChangeCommand:BindingCommand<String> = BindingCommand(BindingConsumer{
        repassword.set(it)
    })

    val onBackClick : View.OnClickListener = View.OnClickListener {
        finish()
    }

    var onRegisterClickCommand : BindingCommand<Any> = BindingCommand(BindingAction {
        if(password.get()!=repassword.get()){
            showNormalToast("两次密码不一致")
            return@BindingAction
        }

        model.register(account.get()!!,password.get()!!,repassword.get()!!)
            .compose(RxThreadHelper.rxSchedulerHelper(this))
            .doOnSubscribe { showLoading() }
            .subscribe(object : ApiSubscriptHelper<BaseBean<*>>(){
                override fun onResult(t: BaseBean<*>) {
                    dismissLoading()
                    if (t.errorCode == 0){
                        showSuccessToast("注册成功")
                        LiveBusCenter.postRegisterSuccessEvent(account.get(),password.get())
                        finish()
                    }
                }

                override fun onFailed(msg: String?) {
                    dismissLoading()
                    showErrorToast(msg)
                }
            })
    })



    //前往登录
    val onBackToLoginCommand:BindingCommand<Void> = BindingCommand(BindingAction {
        finish()
    })






}