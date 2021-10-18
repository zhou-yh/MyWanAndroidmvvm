package com.zyh.module_project.ui.activity

import com.zyh.lib_base.base.BaseActivity
import com.zyh.lib_base.databinding.CommonContainerBinding
import com.zyh.lib_base.mvvm.viewmodel.CommonViewModel
import com.zyh.module_project.BR
import com.zyh.module_project.R


/**
 * author: zhouyh
 * created on: 2021/9/23 5:47 下午
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
        super.initData()
    }
}