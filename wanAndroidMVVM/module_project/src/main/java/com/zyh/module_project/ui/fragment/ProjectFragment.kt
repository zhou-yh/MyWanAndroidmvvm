package com.zyh.module_project.ui.fragment

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.LogUtils
import com.czl.lib_base.config.AppConstants
import com.google.android.material.tabs.TabLayoutMediator
import com.zyh.lib_base.adapter.ViewPagerFmAdapter
import com.zyh.lib_base.base.BaseFragment
import com.zyh.lib_base.data.bean.ProjectSortBean
import com.zyh.module_project.BR
import com.zyh.module_project.R
import com.zyh.module_project.databinding.ProjectFragmentProjectBinding
import com.zyh.module_project.viewmodel.ProjectViewModel


/**
 * author: zhouyh
 * created on: 2021/9/24 11:44 上午
 * description:
 */
@Route(path = AppConstants.Router.Project.F_PROJECT)
class ProjectFragment : BaseFragment<ProjectFragmentProjectBinding, ProjectViewModel>() {


    override fun initContentView(): Int {
        return R.layout.project_fragment_project
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun useBaseLayout(): Boolean {
        return false
    }

    override fun initData() {
        //先取缓存数据
        val data = viewModel.getCacheSort()
        LogUtils.e("project:${data.toString()}" )
        if (!data.isNullOrEmpty()){
            initViewPager(data)
        }else{
            viewModel.getProjectSort()
        }

    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.loadCompletedEvent.observe(this, Observer { data ->
            if (!data.isNullOrEmpty()){
                viewModel.model.saveCacheListData(data)
            }
            initViewPager(data)
        })
    }


    /**
     * 初始化viewpager
     */
    private fun initViewPager(it: List<ProjectSortBean>) {
        val fragments = arrayListOf<ContentFragment>()
        val tabTitles = arrayListOf<String>()
        for (data in it){
            binding.tabLayout.addTab(binding.tabLayout.newTab())
            tabTitles.add(data.name)
            fragments.add(ContentFragment.getInstance(data.id.toString()))
        }

        binding.viewpager.apply {
            adapter = ViewPagerFmAdapter(childFragmentManager,lifecycle,fragments)
            //优化体验设置该属性后第一次将自动加载所有fragment 在子fragment内部添加懒加载机制
            offscreenPageLimit = fragments.size
        }

        /**
         * 绑定tablayout 和 viewpager
         */
        TabLayoutMediator(binding.tabLayout,binding.viewpager){ tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }


    override fun reload() {
        super.reload()
        viewModel.getProjectSort()
    }
}
