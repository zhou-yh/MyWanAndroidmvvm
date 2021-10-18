package com.zyh.module_square.adapter

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.czl.lib_base.config.AppConstants
import com.zyh.lib_base.binding.command.BindingAction
import com.zyh.lib_base.binding.command.BindingCommand
import com.zyh.lib_base.binding.command.BindingConsumer
import com.zyh.lib_base.data.bean.SquareListBean
import com.zyh.module_square.R
import com.zyh.module_square.databinding.SquareItemHomeBinding
import com.zyh.module_square.ui.fragment.SquareFragment


/**
 * author: zhouyh
 * created on: 2021/9/28 3:41 下午
 * description:
 */
class SquareHomeAdapter(val mFragment:SquareFragment):
    BaseQuickAdapter<SquareListBean.Data,BaseDataBindingHolder<SquareItemHomeBinding>>
        (R.layout.square_item_home) {


    val tvShare = " 分享 "
    val tvAuthor = " 作者 "

    override fun convert(
        holder: BaseDataBindingHolder<SquareItemHomeBinding>,
        item: SquareListBean.Data,
    ) {
        holder.dataBinding?.apply {
            data = item
            adapter = this@SquareHomeAdapter
            executePendingBindings()
        }
    }


    val onUserNameClickCommand:BindingCommand<Any> = BindingCommand(BindingConsumer {

    })


    val onCollectClickCommand:BindingCommand<Any> = BindingCommand(BindingConsumer {

    })

    /**
     * 使用bindingConsumer是需要传递对象
     */
    val onItemClickCommand:BindingCommand<Any> = BindingCommand(BindingConsumer {
        if (it is SquareListBean.Data){
            mFragment.startContainerActivity(AppConstants.Router.Web.F_WEB, Bundle().apply {
                putString(AppConstants.BundleKey.WEB_URL,it.link)
            })
        }

    })


    /**
     * 配置差异数据监听
     */
    val diffConfig = object : DiffUtil.ItemCallback<SquareListBean.Data>(){
        override fun areItemsTheSame(
            oldItem: SquareListBean.Data,
            newItem: SquareListBean.Data,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SquareListBean.Data,
            newItem: SquareListBean.Data,
        ): Boolean {
            return oldItem.title == newItem.title
        }

    }

}