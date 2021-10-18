package com.zyh.module_square.ui.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.LogUtils
import com.czl.lib_base.config.AppConstants
import com.zyh.lib_base.base.BaseFragment
import com.zyh.lib_base.data.bean.SquareListBean
import com.zyh.module_square.BR
import com.zyh.module_square.R
import com.zyh.module_square.adapter.SquareHomeAdapter
import com.zyh.module_square.databinding.SquareFragmentSquareBinding
import com.zyh.module_square.viewmodel.SquareViewModel


/**
 * author: zhouyh
 * created on: 2021/9/24 10:04 上午
 * description:广场页面
 */
@Route(path = AppConstants.Router.Square.F_SQUARE)
class SquareFragment : BaseFragment<SquareFragmentSquareBinding, SquareViewModel>() {


    private lateinit var mAdapter : SquareHomeAdapter

    override fun initContentView(): Int {
        return R.layout.square_fragment_square
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun useBaseLayout(): Boolean {
        return false
    }

    override fun reload() {
        super.reload()
        binding.srlCommon.autoRefresh()
    }

    override fun initData() {
        super.initData()
        initAdapter()
        loadData()

    }


    /**
     * 加载数据
     */
    private fun loadData() {
        val data = viewModel.getCacheData()
        if (!data.isNullOrEmpty()){
            binding.ryCommon.hideShimmerAdapter()
            mAdapter.setDiffNewData(data as MutableList<SquareListBean.Data>)
        }else{
            binding.srlCommon.autoRefresh()
        }
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.uc.onScrollTopEvent.observe(this,{
            binding.ryCommon.smoothScrollToPosition(0)
        })

        viewModel.uc.onLoadCompleteEvent.observe(this, Observer { data->
            if (!data?.datas.isNullOrEmpty()){
                viewModel.model.saveCacheListData(data!!.datas)
            }
            LogUtils.e(data?.datas.toString())
            handleRecyclerViewData(
                data == null,
                data?.datas as MutableList<*>?,
                mAdapter,
                binding.ryCommon,
                binding.srlCommon,
                viewModel.currentPage,
                data?.over,
            )
        })

    }


    fun initAdapter(){
        mAdapter = SquareHomeAdapter(this)
        mAdapter.setDiffCallback(mAdapter.diffConfig)
        binding.ryCommon.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            adapter = mAdapter
            showShimmerAdapter()
        }
    }
}