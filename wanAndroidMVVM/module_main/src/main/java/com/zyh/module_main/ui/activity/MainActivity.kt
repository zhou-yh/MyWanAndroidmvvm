package com.zyh.module_main.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.czl.lib_base.config.AppConstants
import com.zyh.lib_base.adapter.ViewPagerFmAdapter
import com.zyh.lib_base.base.BaseActivity
import com.zyh.lib_base.route.RouteCenter
import com.zyh.module_main.BR
import com.zyh.module_main.R
import com.zyh.module_main.databinding.MainActivityMainBinding
import com.zyh.module_main.viewmodel.MainViewModel
import me.yokeyword.fragmentation.SupportFragment

@Route(path = AppConstants.Router.Main.A_MAIN)
class MainActivity : BaseActivity<MainActivityMainBinding, MainViewModel>() {

    override fun initContentView(): Int {
        return R.layout.main_activity_main
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }


    override fun useBaseLayout(): Boolean {
        return false
    }

    override fun initViewObservable() {
        viewModel.uc.tabChangeLiveEvent.observe(this, {
            binding.viewPage2.setCurrentItem(it, false)
        })

        viewModel.uc.pageChangeLiveEvent.observe(this, {
            binding.bottomNavigation.selectTab(it, false)
        })
    }

    override fun initData() {
        setSwipeBackEnable(false)
        initBottomBar()
        initViewPager()
    }

    private fun initViewPager() {
        binding.viewPage2.isUserInputEnabled = false
        val homeFragment = RouteCenter.navigate(AppConstants.Router.Main.F_HOME) as SupportFragment
        val squareFragment = RouteCenter.navigate(AppConstants.Router.Square.F_SQUARE) as SupportFragment
        val projectFragment = RouteCenter.navigate(AppConstants.Router.Project.F_PROJECT) as SupportFragment
        val userFragment = RouteCenter.navigate(AppConstants.Router.User.F_USER) as SupportFragment
        val fragments = arrayListOf(homeFragment,squareFragment,projectFragment,userFragment)
        binding.viewPage2.apply {
            adapter = ViewPagerFmAdapter(supportFragmentManager,lifecycle,fragments)
            offscreenPageLimit = fragments.size
        }
    }

    private fun initBottomBar() {
        binding.bottomNavigation.apply {
            setMode(BottomNavigationBar.MODE_FIXED)
            setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
            addItem(
                BottomNavigationItem(
                    R.drawable.ic_home_on,
                    getString(R.string.main_tab_home))
                    .setActiveColorResource(R.color.md_theme_red)
                    .setInactiveIconResource(R.drawable.ic_home_off)
            )
            addItem(
                BottomNavigationItem(
                    R.drawable.ic_square_on,
                    getString(R.string.main_tab_square))
                    .setActiveColorResource(R.color.md_theme_red)
                    .setInactiveIconResource(R.drawable.ic_square_off)
            )
            addItem(
                BottomNavigationItem(
                    R.drawable.ic_project_on,
                    getString(R.string.main_tab_project))
                    .setActiveColorResource(R.color.md_theme_red)
                    .setInactiveIconResource(R.drawable.ic_project_off)
            )
            addItem(
                BottomNavigationItem(
                    R.drawable.ic_me_on,
                    getString(R.string.main_tab_me))
                    .setActiveColorResource(R.color.md_theme_red)
                    .setInactiveIconResource(R.drawable.ic_me_off)
            )
            setFirstSelectedPosition(0)
            initialise()
        }
    }
}