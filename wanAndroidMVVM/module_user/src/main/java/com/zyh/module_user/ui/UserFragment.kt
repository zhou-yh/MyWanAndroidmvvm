package com.zyh.module_user.ui

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.czl.lib_base.config.AppConstants
import com.lxj.xpopup.core.BasePopupView
import com.zyh.lib_base.base.BaseFragment
import com.zyh.lib_base.event.LiveBusCenter
import com.zyh.module_user.BR
import com.zyh.module_user.R
import com.zyh.module_user.databinding.UserFragmentUserBinding
import com.zyh.module_user.viewmodel.UserViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named


/**
 * author: zhouyh
 * created on: 2021/9/23 6:21 下午
 * description: 我的界面
 */
@Route(path = AppConstants.Router.User.F_USER)
class UserFragment : BaseFragment<UserFragmentUserBinding, UserViewModel>() {

    private val loginPopView: BasePopupView by inject(named("login"))

    override fun initContentView(): Int {
        return R.layout.user_fragment_user
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun useBaseLayout(): Boolean {
        return false
    }


    override fun initData() {
        super.initData()
        binding.userData = viewModel.model.getUserData()
        binding.userData?.let {
            viewModel.getUserCollectData()
            viewModel.getUserShareData()
        }

        GlobalScope.launch {
            delay(1000L)
            println("hello this is coroutines test")
        }


    }

    override fun reload() {
        super.reload()
        binding.smartCommon.autoRefresh()
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.uc.refreshEvent.observe(this, Observer {
            binding.smartCommon.finishRefresh(1500)
        })


        //接收用户登录成功事件
        LiveBusCenter.observeLoginSuccessEvent(this,{
            binding.userData = viewModel.model.getUserData()
            viewModel.getUserCollectData()
            viewModel.getUserShareData()
        })


        //接收用户注销事件
        LiveBusCenter.observeLogoutEvent(this,{
            binding.userData = null
            viewModel.apply {
                tvCollect.set("0")
                tvScore.set("0")
                tvShare.set("0")
            }
        })

        viewModel.uc.showLoginPopEvent.observe(this,{
            loginPopView.show()
        })
    }
}