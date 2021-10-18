package com.zyh.module_main.pop

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.czl.lib_base.config.AppConstants
import com.lxj.xpopup.core.HorizontalAttachPopupView
import com.zyh.lib_base.base.BaseBean
import com.zyh.lib_base.binding.command.BindingAction
import com.zyh.lib_base.binding.command.BindingCommand
import com.zyh.lib_base.data.bean.ProjectBean
import com.zyh.lib_base.event.LiveBusCenter
import com.zyh.lib_base.extension.ApiSubscriptHelper
import com.zyh.module_main.R
import com.zyh.module_main.databinding.MainItemPopSettingAttachBinding
import com.zyh.module_main.ui.fragment.HomeFragment


/**
 * author: zhouyh
 * created on: 2021/9/21 10:37 上午
 * description:项目设置弹框
 */
class ProjectItemSettingPop(val mFragment: HomeFragment, val bean: ProjectBean.Data) :
    HorizontalAttachPopupView(mFragment.requireContext()) {


    private var dataBinding: MainItemPopSettingAttachBinding? = null
    val tvCollect = "收藏"
    val tvUnCollect = "取消收藏"

    override fun getImplLayoutId(): Int {
        return R.layout.main_item_pop_setting_attach
    }

    override fun onCreate() {
        super.onCreate()
        dataBinding = DataBindingUtil.bind(popupImplView)
        dataBinding?.apply {
            this.data = bean
            pop = this@ProjectItemSettingPop
            executePendingBindings()
        }
    }

    override fun onDestroy() {
        dataBinding?.unbind()
        super.onDestroy()

    }


    val onFindSameClick : BindingCommand<Void> = BindingCommand(BindingAction {
        mFragment.startContainerActivity(AppConstants.Router.Square.F_SYS_CONTENT, Bundle().apply {
            putString(AppConstants.BundleKey.SYS_CONTENT_TITLE,bean.chapterName)
            putString("cid",bean.chapterId.toString())
        })
        dismiss()
    })

    val onCollectCommand : BindingCommand<Void> = BindingCommand(BindingAction {
        if (!bean.collect){
            mFragment.viewModel.collectArticle(bean.id)
                .subscribe(object : ApiSubscriptHelper<BaseBean<*>>(){
                    override fun onResult(t: BaseBean<*>) {
                        if (t.errorCode == 0){
                            LiveBusCenter.postRefreshUserFmEvent()
                            mFragment.showSuccessToast("收藏成功")
                            bean.collect = true
                            //同步热门文章列表item
                            val list = mFragment.mArticleAdapter.data.filter {
                                x -> x.id == bean.id
                            }
                            if (list.isNotEmpty()) list[0].collect = true
                        }
                    }

                    override fun onFailed(msg: String?) {
                        mFragment.showErrorToast(msg)
                    }

                })
        }else{
            mFragment.viewModel.unCollectArticle(bean.id)
                .subscribe(object : ApiSubscriptHelper<BaseBean<*>>(){
                    override fun onResult(t: BaseBean<*>) {
                        if (t.errorCode == 0){
                            LiveBusCenter.postRefreshUserFmEvent()
                            bean.collect = false
                            //同步热门文章列表item
                            val list = mFragment.mArticleAdapter.data.filter {
                                x->x.id == bean.id
                            }
                            if (list.isNotEmpty()) list[0].collect = false
                        }
                    }

                    override fun onFailed(msg: String?) {
                        mFragment.showErrorToast(msg)
                    }
                })
        }

    })


}