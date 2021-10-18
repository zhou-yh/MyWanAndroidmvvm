package com.zyh.module_login.ui.fragment

import android.graphics.Color
import com.alibaba.android.arouter.facade.annotation.Route
import com.czl.lib_base.config.AppConstants
import com.gyf.immersionbar.ImmersionBar
import com.zyh.lib_base.base.BaseFragment
import com.zyh.lib_base.event.LiveBusCenter
import com.zyh.lib_base.widget.EditTextMonitor
import com.zyh.module_login.BR
import com.zyh.module_login.R
import com.zyh.module_login.databinding.LoginFragmentLoginBinding
import com.zyh.module_login.viewmodel.LoginViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject


/**
 * author: zhouyh
 * created on: 2021/9/14 11:04 上午
 * description:
 */
@Route(path = AppConstants.Router.Login.F_LOGIN)
class LoginFragment : BaseFragment<LoginFragmentLoginBinding, LoginViewModel>() {


    override fun initContentView(): Int {
        return R.layout.login_fragment_login
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun onSupportVisible() {
        ImmersionBar.with(this).statusBarDarkFont(false).init()
    }

    override fun useBaseLayout(): Boolean {
        return false
    }

    override fun initData() {
        initBtnState()
    }

    private fun initBtnState() {
        binding.btnLogin.isEnabled = false
        val accountSubject = PublishSubject.create<String>()
        val pwdSubject = PublishSubject.create<String>()
        binding.etAccount.addTextChangedListener(EditTextMonitor(accountSubject))
        binding.etPwd.addTextChangedListener(EditTextMonitor(pwdSubject))
        viewModel.addSubscribe(Observable.combineLatest(
            accountSubject,
            pwdSubject,
            { account: String, pwd: String ->
                account.isNotBlank() && pwd.isNotBlank()
            }).observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.btnLogin.isEnabled = it
                binding.btnLogin.setTextColor(
                    if (it) Color.parseColor("#000000") else Color.parseColor("#ffffff")
                )
                binding.btnLogin.setBackgroundResource(if (it) R.drawable.shape_round_white else R.drawable.gray_btn_corner_10dp)
            }
        )
    }

    override fun initViewObservable() {
        LiveBusCenter.observeRegisterSuccessEvent(this){
            viewModel.account.set(it.account)
            viewModel.password.set(it.pwd)
        }
    }
}