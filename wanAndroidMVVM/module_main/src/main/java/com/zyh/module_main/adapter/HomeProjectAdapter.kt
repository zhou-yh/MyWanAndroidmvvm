package com.zyh.module_main.adapter

import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.czl.lib_base.config.AppConstants
import com.lxj.xpopup.XPopup
import com.zyh.lib_base.binding.command.BindingCommand
import com.zyh.lib_base.binding.command.BindingConsumer
import com.zyh.lib_base.data.bean.ProjectBean
import com.zyh.lib_base.extension.ImagePopLoader
import com.zyh.module_main.R
import com.zyh.module_main.databinding.MainItemProjectBinding
import com.zyh.module_main.pop.ProjectItemSettingPop
import com.zyh.module_main.ui.fragment.HomeFragment


/**
 * author: zhouyh
 * created on: 2021/9/20 5:23 下午
 * description: 项目列表适配器
 */
class HomeProjectAdapter(val mFragment: HomeFragment):
    BaseQuickAdapter<ProjectBean.Data,
            BaseDataBindingHolder<MainItemProjectBinding>>(R.layout.main_item_project) {

    override fun convert(
        holder: BaseDataBindingHolder<MainItemProjectBinding>,
        item: ProjectBean.Data,
    ) {
        holder.dataBinding?.apply {
            data = item
            adapter = this@HomeProjectAdapter
            executePendingBindings()
        }
    }

    /**
     * Item点击事件
     */
    val onProjectItemClickCommand:BindingCommand<Any> = BindingCommand(BindingConsumer {
        if (it is ProjectBean.Data){
            val bundle = Bundle()
            bundle.putString(AppConstants.BundleKey.WEB_URL,it.link)
            mFragment.startContainerActivity(AppConstants.Router.Web.F_WEB,bundle)

        }
    })

    /**
     * 设置点击事件
     */
    val onProjectSettingClickCommand:BindingCommand<Any> = BindingCommand(BindingConsumer {
        if (it is ProjectBean.Data){
            XPopup.Builder(context)
                .hasShadowBg(false) //设置遮罩
                .isDestroyOnDismiss(true)
                .atView(getViewByPosition(getItemPosition(it),R.id.iv_setting))
                .asCustom(ProjectItemSettingPop(mFragment,it))
                .show()
        }
    })

    /**
     * 点击图片
     */
    val onProjectImageClickCommand:BindingCommand<Any> = BindingCommand(BindingConsumer {
        if (it is ProjectBean.Data){
            XPopup.Builder(context)
                .asImageViewer(
                    getViewByPosition(getItemPosition(it),
                        R.id.iv_items_works_list) as ImageView,it.envelopePic,ImagePopLoader())
                .show()
        }
    })


    val diffConfig = object : DiffUtil.ItemCallback<ProjectBean.Data>(){
        override fun areItemsTheSame(
            oldItem: ProjectBean.Data,
            newItem: ProjectBean.Data,
        ): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ProjectBean.Data,
            newItem: ProjectBean.Data,
        ): Boolean {
            return oldItem.title == newItem.title && oldItem.collect == newItem.collect
        }

    }



}