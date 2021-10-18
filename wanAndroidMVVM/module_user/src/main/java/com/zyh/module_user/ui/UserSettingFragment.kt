package com.zyh.module_user.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.czl.lib_base.config.AppConstants
import com.zyh.lib_base.base.BaseFragment
import com.zyh.lib_base.utils.DayModeUtil
import com.zyh.lib_base.utils.DialogHelper
import com.zyh.module_user.BR
import com.zyh.module_user.R
import com.zyh.module_user.databinding.UserFragmentSettingBinding
import com.zyh.module_user.viewmodel.UserSettingVm
import org.koin.android.ext.android.bind


/**
 * author: zhouyh
 * created on: 2021/10/14 4:37 下午
 * description:设置页面
 */
@Route(path = AppConstants.Router.User.F_USER_SETTING)
class UserSettingFragment : BaseFragment<UserFragmentSettingBinding,UserSettingVm>() {


    override fun initContentView(): Int {
       return R.layout.user_fragment_setting
    }

    override fun initVariableId(): Int {
       return BR.viewModel
    }


    override fun initData() {
        viewModel.apply {
            setTvCacheSize()
            tvTitle.set("设置")
            logoutVisible.set(!model.getLoginName().isNullOrBlank())
            readHistoryState.set(model.getReadHistoryFlag())
        }

        binding.scSys.isChecked = viewModel.model.getFollowSysUiModelState()
        if (!binding.scSys.isChecked) binding.scMode.isChecked = viewModel.model.getUserUiModelState()
    }


    override fun initViewObservable() {
        super.initViewObservable()

        //退出登录事件监听
        viewModel.uc.confirmLogoutEvent.observe(this,{
            DialogHelper.showBaseDialog(requireContext(),"注销","是否确定退出登录？"){
                viewModel.logout()
            }
        })

        //跟随系统夜间模式
        viewModel.uc.switchSystemModelEvent.observe(this,{ checked->
            //跟随系统关闭后，选择夜间模式
            if (!checked){
                viewModel.model.saveUserUiModelState(DayModeUtil.isNightMode(requireContext()))
                binding.scSys.isChecked = DayModeUtil.isNightMode(requireContext())
                if (DayModeUtil.isNightMode(requireContext())){
                    DayModeUtil.setNightMode()
                }else{
                    DayModeUtil.setLightMode()
                }
            }

            if (viewModel.model.getFollowSysUiModelState()&&checked){
                return@observe
            }

            viewModel.model.saveFollowSysUiModelState(checked)
            if (checked){
                DayModeUtil.autoModeBySys()
                restart()
            }
        })

        viewModel.uc.switchNightModelEvent.observe(this,{checked->

            if (checked){
                if (!DayModeUtil.isNightMode(requireContext())){
                    DayModeUtil.setNightMode()
                    restart()
                }
            }else{
                if (DayModeUtil.isNightMode(requireContext())){
                    DayModeUtil.setLightMode()
                    restart()
                }
            }
        })
    }

    private fun restart() {
        back()
        startContainerActivity(AppConstants.Router.User.F_USER_SETTING)
        activity?.overridePendingTransition(R.anim.anim_fade_in,R.anim.anim_fade_out)
    }
}