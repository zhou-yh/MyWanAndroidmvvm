package com.zyh.module_login.ui.fragment

import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.BarUtils
import com.czl.lib_base.config.AppConstants
import com.gyf.immersionbar.ImmersionBar
import com.zyh.lib_base.base.BaseFragment
import com.zyh.lib_base.widget.EditTextMonitor
import com.zyh.module_login.BR
import com.zyh.module_login.R
import com.zyh.module_login.databinding.LoginRegisterFragmentBinding
import com.zyh.module_login.viewmodel.RegisterViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject


/**
 * author: zhouyh
 * created on: 2021/9/17 11:35 下午
 * description:注册页面
 */
@Route(path = AppConstants.Router.Login.F_REGISTER)
class RegisterFragment: BaseFragment<LoginRegisterFragmentBinding, RegisterViewModel>() {


    override fun initContentView(): Int {
        return R.layout.login_register_fragment
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun useBaseLayout(): Boolean {
        return false
    }

    override fun onSupportVisible() {
        ImmersionBar.with(this).statusBarDarkFont(false).init()
    }

    override fun initData() {
        initBackBtnMargin()
        initBtnState()
    }

    /**
     * 初始化返回键布局
     */
    private fun initBackBtnMargin() {
        val layoutParams = binding.ivBack.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.setMargins(
            layoutParams.leftMargin,
            BarUtils.getStatusBarHeight(),
            layoutParams.rightMargin,
            layoutParams.bottomMargin
        )
        binding.ivBack.layoutParams = layoutParams
    }

    private fun initBtnState() {
        binding.btnRegister.isEnabled = false
        val accountSubject = PublishSubject.create<String>()
        val pwdSubject = PublishSubject.create<String>()
        val rePwdSubject = PublishSubject.create<String>()
        binding.etAccount.addTextChangedListener(EditTextMonitor(accountSubject))
        binding.etPassword.addTextChangedListener(EditTextMonitor(pwdSubject))
        binding.etRepassword.addTextChangedListener(EditTextMonitor(rePwdSubject))
        viewModel.addSubscribe(
            Observable.combineLatest(accountSubject,pwdSubject,rePwdSubject,
                {account:String,pwd:String,rePwd:String -> account.isNotBlank() && pwd.isNotBlank() && rePwd.isNotBlank()})
                .observeOn(AndroidSchedulers.mainThread()).subscribe{
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.setTextColor(if (it) Color.parseColor("#000000")else Color.parseColor("#ffffff"))
                    binding.btnRegister.setBackgroundResource(if (it) R.drawable.shape_round_white else R.drawable.gray_btn_corner_10dp)
                }
        )
    }
}