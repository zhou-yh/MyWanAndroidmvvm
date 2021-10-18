package com.zyh.lib_base.binding.viewadapter.viewpager

import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.zyh.lib_base.binding.command.BindingCommand


/**
 * author: zhouyh
 * created on: 2021/9/18 6:01 下午
 * description:viewpage
 */
object ViewAdapter {

    @JvmStatic
    @BindingAdapter(
        value = ["onPageScrolledCommand","onPageSelectCommand","onPageScrollStateChangeCommand"],
        requireAll = false)
    fun onScrollChangeCommand(
        viewGroup: ViewGroup,
        onPageScrolledCommand: BindingCommand<ViewPagerDataWrapper?>?,
        onPageSelectCommand:BindingCommand<Int?>?,
        onPageScrolledStateChangeCommand:BindingCommand<Int?>?
    ) {
        if (viewGroup is ViewPager2){
            viewGroup.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    onPageSelectCommand?.execute(position)
                }
            })
        }

        if (viewGroup is ViewPager){
            viewGroup.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                private var state = 0
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                    onPageScrolledCommand?.execute(
                        ViewPagerDataWrapper(
                            position.toFloat(), positionOffset,positionOffsetPixels,state)
                    )
                }

                override fun onPageSelected(position: Int) {
                    onPageSelectCommand?.execute(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    this.state = state
                    onPageScrolledStateChangeCommand?.execute(state)
                }

            })
        }
    }

    class ViewPagerDataWrapper(
        var position: Float,
        var positionOffset: Float,
        var positionOffsetPixels: Int,
        var state: Int
    )
}