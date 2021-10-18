package com.zyh.lib_base.binding.viewadapter.bottombar

import androidx.databinding.BindingAdapter
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.zyh.lib_base.binding.command.BindingCommand


/**
 * author: zhouyh
 * created on: 2021/9/18 5:54 下午
 * description: 底部导航栏
 */
object ViewAdapter {

    @JvmStatic
    @BindingAdapter(value = ["onTabChangeCommand"],requireAll = false)
    fun onTabChangeCommand(  bar: BottomNavigationBar,
                             onTabSelectedCommand: BindingCommand<Int?>?) {
        bar.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener{
            override fun onTabSelected(position: Int) {
                onTabSelectedCommand?.execute(position)
            }

            override fun onTabUnselected(position: Int) {

            }

            override fun onTabReselected(position: Int) {

            }

        })
    }
}