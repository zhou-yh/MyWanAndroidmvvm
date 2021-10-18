package com.zyh.module_main.ui.activity

import com.zyh.lib_base.base.BaseActivity
import com.zyh.lib_base.databinding.CommonContainerBinding
import com.zyh.lib_base.mvvm.viewmodel.CommonViewModel
import com.zyh.module_main.BR
import com.zyh.module_main.R
import com.zyh.module_main.ui.fragment.HomeFragment


/**
 * author: zhouyh
 * created on: 2021/9/23 5:14 下午
 * description:
 */
class AloneActivity : BaseActivity<CommonContainerBinding, CommonViewModel>() {
    override fun initContentView(): Int {
        return R.layout.common_container
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun useBaseLayout(): Boolean {
        return false
    }

    override fun initData() {
        if (findFragment(HomeFragment::class.java)==null){
            loadRootFragment(R.id.fl_container,HomeFragment())
        }
    }

}