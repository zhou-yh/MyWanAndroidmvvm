package com.zyh.module_main.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.LogUtils
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.czl.lib_base.config.AppConstants
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.gyf.immersionbar.ImmersionBar
import com.zyh.lib_base.base.BaseFragment
import com.zyh.lib_base.data.bean.*
import com.zyh.lib_base.event.LiveBusCenter
import com.zyh.lib_base.utils.DayModeUtil
import com.zyh.lib_base.utils.RxThreadHelper
import com.zyh.module_main.BR
import com.zyh.module_main.R
import com.zyh.module_main.adapter.HomeArticleAdapter
import com.zyh.module_main.adapter.HomeProjectAdapter
import com.zyh.module_main.adapter.MyBannerAdapter
import com.zyh.module_main.databinding.MainFragmentHomeBinding
import com.zyh.module_main.viewmodel.HomeViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * author: zhouyh
 * created on: 2021/9/18 8:59 下午
 * description:首页
 */
@Route(path = AppConstants.Router.Main.F_HOME)
class HomeFragment : BaseFragment<MainFragmentHomeBinding, HomeViewModel>() {


    private lateinit var bannerAdapter: MyBannerAdapter
    private lateinit var bannerSkeleton: SkeletonScreen
    lateinit var mArticleAdapter: HomeArticleAdapter
    lateinit var mProjectAdapter: HomeProjectAdapter
    private var hotKeyList: List<String>? = null
    private var changeSearchTask: Disposable? = null

    override fun onSupportVisible() {
        ImmersionBar.with(this).fitsSystemWindows(true)
            .statusBarDarkFont(!DayModeUtil.isNightMode(requireContext())).init()
    }

    override fun initContentView(): Int {
        return R.layout.main_fragment_home
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }


    override fun useBaseLayout(): Boolean {
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingStatePage()
    }

    override fun initData() {
        super.initData()
        viewModel.tabSelectedPosition.set(0)
        initBanner()
        initArticleAdapter()
        initProjectAdapter()
        loadData()
    }

    fun initProjectAdapter() {
        mProjectAdapter = HomeProjectAdapter(this)
        mProjectAdapter.setDiffCallback(mProjectAdapter.diffConfig)
        binding.ryProject.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = mProjectAdapter
            setDemoLayoutReference(R.layout.main_item_project_skeleton)
            setDemoLayoutManager(ShimmerRecyclerView.LayoutMangerType.LINEAR_VERTICAL)
            showShimmerAdapter()
        }
    }

    override fun reload() {
        super.reload()
        binding.smartAuto.autoRefresh()
    }

    /**
     * 加载数据
     */
    private fun loadData() {
        //首先从缓存加载数据
        val bannerObservable = Observable.create<List<HomeBannerBean?>>() {
            it.onNext(viewModel.getCacheData(AppConstants.CacheKey.CACHE_HOME_BANNER))
        }.subscribeOn(Schedulers.io())
        val articleObservable = Observable.create<List<HomeArticleBean.Data?>>() {
            it.onNext(viewModel.getCacheData(AppConstants.CacheKey.CACHE_HOME_ARTICLE))
        }.subscribeOn(Schedulers.io())
        val keyWordObservable = Observable.create<List<SearchHotKeyBean>>() {
            it.onNext(viewModel.getCacheData(AppConstants.CacheKey.CACHE_HOME_KEYWORD))
        }.subscribeOn(Schedulers.io())

        //merge合并后，按时间线并行执行 concat 合并会按发送顺序串行执行
        viewModel.addSubscribe(Observable.merge(
            bannerObservable,
            articleObservable,
            keyWordObservable
        ).compose(RxThreadHelper.rxSchedulerHelper())
            .subscribe({
                showSuccessStatePage()
                if (it.isNotEmpty()) {
                    when (it[0]) {
                        is HomeBannerBean? -> {
                            //隐藏加载动画
                            bannerSkeleton.hide()
                            bannerAdapter = MyBannerAdapter(it as List<HomeBannerBean?>, this)
                            binding.banner.adapter = bannerAdapter
                        }
                        is HomeArticleBean.Data? -> {
                            binding.ryArticle.hideShimmerAdapter()
                            //设置新实例
                            mArticleAdapter.setDiffNewData(it as MutableList<HomeArticleBean.Data>?)
                        }
                        is SearchHotKeyBean? -> {

                        }
                    }
                } else {
                    println("m1 smart is " + binding.smartAuto.autoRefresh())
                }
            }) {
                showSuccessStatePage()
                it.printStackTrace()
                binding.smartAuto.autoRefresh()
            })
    }

    private fun initArticleAdapter() {
        mArticleAdapter = HomeArticleAdapter(this)
        mArticleAdapter.setDiffCallback(mArticleAdapter.diffConfig)
        binding.ryArticle.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = mArticleAdapter
            setDemoLayoutReference(R.layout.main_article_item_skeleton)
            setDemoLayoutManager(ShimmerRecyclerView.LayoutMangerType.LINEAR_VERTICAL)
            showShimmerAdapter()
        }
    }

    fun initBanner() {
        bannerSkeleton = Skeleton.bind(binding.banner)
            .load(R.layout.main_item_project_skeleton)
            .show()

    }

    /**
     * 页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
     */
    override fun initViewObservable() {

        // 轮播图数据获取完成
        viewModel.uc.bannerCompleteEvent.observe(this, {
            bannerSkeleton.hide()
            //要检测一个 lateinit var 是否已经初始化过，请在该属性的引用上使用 .isInitialized：
            if (!this::bannerAdapter.isInitialized) {
                // 第一次加载
                viewModel.model.saveCacheListData(it)
                bannerAdapter = MyBannerAdapter(it, this)
                binding.banner.adapter = bannerAdapter
            } else {
                bannerAdapter.setData(binding.banner, it)
            }
        })

        // 接收文章列表数据
        viewModel.uc.loadArticleCompleteEvent.observe(this, { data ->
            if(!data?.datas.isNullOrEmpty()){
                viewModel.model.saveCacheListData(data!!.datas)
            }

            handleRecyclerViewData(
                data == null,
                data?.datas as MutableList<*>?,
                mArticleAdapter,
                binding.ryArticle,
                binding.smartAuto,
                viewModel.currentArticlePage,
                data?.over
            )
        })

        //接收项目列表数据
        viewModel.uc.loadProjectCompleteEvent.observe(this,{data ->
//            if (!data?.datas.isNullOrEmpty()){
//                viewModel.model.saveCacheListData(data!!.datas)
//            }
            handleRecyclerViewData(
                data == null,
                data?.datas as MutableList<*>?,
                mProjectAdapter,
                binding.ryProject,
                binding.smartAuto,
                viewModel.currentProjectPage,
                data?.over
            )
        })


        // 点击项目tab判断数据是否为空(第一次加载)
        viewModel.uc.tabSelectedEvent.observe(this, { position ->
            LogUtils.e("position ==${position}")
            if (position == 1 && mProjectAdapter.data.isNullOrEmpty()) {
                viewModel.currentProjectPage = -1
                viewModel.getProject()
            }
            if (position == 0 && mArticleAdapter.data.isNullOrEmpty()) {
                viewModel.currentArticlePage = -1
                viewModel.getArticle()
            }
        })


        // 接收用户登录成功事件
        LiveBusCenter.observeLoginSuccessEvent(this) {

        }
    }

//    private fun setTimerHotKey(list: List<SearchHotKeyBean>) {
//        hotKeyList = list.map { it.name }
//        changeSearchTask?.dispose()
//        // 发布定时任务更换搜索框关键字
//        changeSearchTask = Flowable.interval(0, 10, TimeUnit.SECONDS)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                val hotKeyword = list[Random().nextInt(list.size)].name
//                binding.searchBar.setPlaceHolder(hotKeyword)
//            }) {
//                it.printStackTrace()
//                LogUtils.e("定时更换搜索热词失败")
//            }
//        viewModel.addSubscribe(changeSearchTask!!)
//    }





}