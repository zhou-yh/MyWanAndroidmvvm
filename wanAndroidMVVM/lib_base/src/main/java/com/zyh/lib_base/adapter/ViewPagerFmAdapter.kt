package com.zyh.lib_base.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.yokeyword.fragmentation.SupportFragment


/**
 * author: zhouyh
 * created on: 2021/9/18 8:38 下午
 * description:
 */
class ViewPagerFmAdapter(
    manager: FragmentManager,
    lifecycle: Lifecycle,
    private val fragments: List<SupportFragment>,
) : FragmentStateAdapter(manager, lifecycle) {

    override fun getItemCount(): Int {
       return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}