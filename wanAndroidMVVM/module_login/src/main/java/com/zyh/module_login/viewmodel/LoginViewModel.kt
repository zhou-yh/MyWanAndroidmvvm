package com.zyh.module_login.viewmodel

import androidx.databinding.ObservableField
import com.czl.lib_base.config.AppConstants
import com.zyh.lib_base.base.*
import com.zyh.lib_base.binding.command.BindingAction
import com.zyh.lib_base.binding.command.BindingCommand
import com.zyh.lib_base.binding.command.BindingConsumer
import com.zyh.lib_base.data.DataRepository
import com.zyh.lib_base.data.bean.UserBean
import com.zyh.lib_base.extension.ApiSubscriptHelper
import com.zyh.lib_base.route.RouteCenter
import com.zyh.lib_base.utils.RxThreadHelper
import java.util.*


/**
 * author: zhouyh
 * created on: 2021/9/14 11:08 上午
 * description: 登录模块viewModel
 */
class LoginViewModel(application: MyApplication, model: DataRepository) :
    BaseViewModel<DataRepository>(application, model) {

    var account = ObservableField("")
    var password = ObservableField("")


    val onAccountChangeCommand : BindingCommand<String> = BindingCommand(BindingConsumer {
        account.set(it)
    })

    val onPasswordChangeCommand: BindingCommand<String> = BindingCommand(BindingConsumer {
        password.set(it)
    })

    var onLoginClick:BindingCommand<Any> = BindingCommand(BindingAction {
        loginByPwd();
    })

    var onRegisterClickCommand:BindingCommand<Any> = BindingCommand(BindingAction {
        startContainerActivity(AppConstants.Router.Login.F_REGISTER)
    })

    val touristClickCommand:BindingCommand<Void> = BindingCommand(BindingAction {
        RouteCenter.navigate(AppConstants.Router.Main.A_MAIN)
        AppManager.instance.finishAllActivity()
    })


    private fun loginByPwd(){
        if (account.get().isNullOrBlank() || password.get().isNullOrBlank()){
            showNormalToast("账号密码不能为空")
            return
        }

        model.apply {
            userLogin(account.get()!!, password.get()!!)
                .compose(RxThreadHelper.rxSchedulerHelper(this@LoginViewModel))
                .doOnSubscribe { showLoading() }
                .subscribe(object : ApiSubscriptHelper<BaseBean<UserBean>>(){
                    override fun onResult(t: BaseBean<UserBean>) {
                        dismissLoading()
                        if (t.errorCode == 0){
                            t.data?.let {
                                saveUserData(it)
                            }
                            RouteCenter.navigate(AppConstants.Router.Main.A_MAIN)
                            AppManager.instance.finishAllActivity()
                        }
                    }

                    override fun onFailed(msg: String?) {
                        dismissLoading()
                        showNormalToast(msg)
                    }

                })
        }
    }



}