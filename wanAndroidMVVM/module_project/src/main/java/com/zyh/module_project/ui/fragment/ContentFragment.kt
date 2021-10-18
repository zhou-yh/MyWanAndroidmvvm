package com.zyh.module_project.ui.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.zyh.lib_base.base.BaseFragment
import com.zyh.lib_base.data.bean.ProjectBean
import com.zyh.lib_base.utils.RxThreadHelper
import com.zyh.module_project.BR
import com.zyh.module_project.R
import com.zyh.module_project.adapter.ProjectItemGridAdapter
import com.zyh.module_project.databinding.ProjectFragmentContentBinding
import com.zyh.module_project.viewmodel.ContentViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*


/**
 * author: zhouyh
 * created on: 2021/9/27 2:51 下午
 * description:项目Tab Viewpager2+Fragment
 */
class ContentFragment:BaseFragment<ProjectFragmentContentBinding,ContentViewModel>(){

    private lateinit var mAdapter: ProjectItemGridAdapter
    private var firstLoad = true   //首次加载 默认为true

    companion object{
        const val SORT_ID = "sort_id"
        fun getInstance(id:String):ContentFragment = ContentFragment().apply {
            arguments = Bundle().apply {
                putString(SORT_ID,id)
            }
        }
    }


    override fun useBaseLayout(): Boolean {
        return false
    }

    override fun initContentView(): Int {
        return R.layout.project_fragment_content
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initData() {
        viewModel.cid = arguments?.get(SORT_ID).toString()
        initAdapter();

    }

    override fun onResume() {
        super.onResume()
        //懒加载
        if (firstLoad){
            // 仅加载第一个fragment的数据缓存
            if (parentFragment is ProjectFragment && (parentFragment as ProjectFragment)
                    .binding.viewpager.currentItem == 0){
                loadFirstFragmentCache()
                return
            }

            refreshData()
        }


    }


    /**
     * 加载第一页数据缓存
     */
    private fun loadFirstFragmentCache() {

        val contentObservable = Observable.create<List<ProjectBean.Data>>{
            it.onNext(viewModel.getCacheList())
        }.subscribeOn(Schedulers.io())

        viewModel.addSubscribe(
            contentObservable
                .compose(RxThreadHelper.rxSchedulerHelper())
                .subscribe({ catchData->
                    if (catchData.isNotEmpty()){
                        firstLoad = false;
                        mAdapter.setDiffNewData(catchData as MutableList<ProjectBean.Data>)
                    }else{
                        refreshData()
                    }
                }){
                    it.printStackTrace()
                    refreshData()
                }
        )
    }

    private fun refreshData() {
        binding.srlProjectContent.autoRefresh()
    }

    /**
     * 重新加载
     */
    override fun reload() {
        refreshData()
    }


    /**
     * 初始化列表
     */
    private fun initAdapter() {
        val manager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        //解决item跳动
        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        mAdapter = ProjectItemGridAdapter(this)
        mAdapter.setDiffCallback(mAdapter.diffCallback)
        binding.ryCommon.apply {
            layoutManager = manager
            adapter = mAdapter
            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    // 解决滑到顶部留白问题
                    val first = IntArray(2)
                    manager.findFirstCompletelyVisibleItemPositions(first)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && (first[0] == 1 || first[1] == 1)) {
                        manager.invalidateSpanAssignments()
                    }
                }
            })
        }


    }

    override fun initViewObservable() {
        super.initViewObservable()


        viewModel.uc.refreshCompleteEvent.observe(this, Observer{
            //仅缓存第一个fragment数据
            if (parentFragment is ProjectFragment && (parentFragment as ProjectFragment).binding
                    .viewpager.currentItem == 0 && !it?.datas.isNullOrEmpty()){
                viewModel.model.saveCacheListData(it!!.datas)
            }
            if (it == null){
                binding.srlProjectContent.finishRefresh(500)
                binding.srlProjectContent.finishLoadMore(false)
                return@Observer
            }
            //成功加载数据后关闭懒加载开关
            firstLoad = false
            binding.srlProjectContent.finishRefresh(500)
            if (it.over) {
                binding.srlProjectContent.finishLoadMoreWithNoMoreData()
            } else {
                binding.srlProjectContent.finishLoadMore(true)
            }
            /**
             * 如果加载更多，添加数据并返回
             */
            if (viewModel.currentPage > 1){
                mAdapter.addData(it.datas)
                return@Observer
            }

            /**
             * 刷新数据设置新数据 转换成可变对象MutableList
             */
            mAdapter.setDiffNewData(it.datas as MutableList<ProjectBean.Data>)

        })

        viewModel.uc.moveTopClick.observe(this, Observer {
            binding.ryCommon.smoothScrollToPosition(0)
        })
    }
}