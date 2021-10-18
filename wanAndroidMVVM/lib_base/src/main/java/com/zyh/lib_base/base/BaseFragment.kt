package com.zyh.lib_base.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.gyf.immersionbar.ImmersionBar
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.Convertor
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.zyh.lib_base.R
import com.zyh.lib_base.callback.ErrorCallback
import com.zyh.lib_base.callback.LoadingCallback
import com.zyh.lib_base.mvvm.ui.ContainerFmActivity
import com.zyh.lib_base.route.RouteCenter
import com.zyh.lib_base.utils.DayModeUtil
import com.zyh.lib_base.utils.DialogHelper
import com.zyh.lib_base.utils.ToastHelper
import com.zyh.lib_base.widget.ShareArticlePopView
import me.yokeyword.fragmentation.SupportFragment
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.get
import java.lang.reflect.ParameterizedType


/**
 * author: zhouyh
 * created on: 2021/9/14 8:33 下午
 * description:基类Fragment
 */
abstract class BaseFragment<V : ViewDataBinding, VM : BaseViewModel<*>> :
    BaseRxFragment(), IBaseView {

    lateinit var binding: V   //绑定试图view
    lateinit var viewModel: VM
    private var viewModelId = 0
    private var dialog: BasePopupView? = null
    private lateinit var rootView: View
    protected var rootBinding: ViewDataBinding? = null
    //公共适配器
    protected var rvCommon: RecyclerView? = null
    //加载反馈页管理框架
    lateinit var loadService: LoadService<BaseBean<*>?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParam()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        var loadSir = LoadSir.Builder()
            .addCallback(ErrorCallback())
            .addCallback(LoadingCallback())
            .setDefaultCallback(SuccessCallback::class.java)
            .build()
        if (useBaseLayout()) {
            rootView = inflater.inflate(R.layout.activity_base, null, false)
                .findViewById(R.id.activity_root)
            rootBinding = DataBindingUtil.bind(rootView)
            binding =
                DataBindingUtil.inflate(inflater, initContentView(), rootView as ViewGroup, true)
            //有标题栏情况下绑定内容view
            loadService = loadSir.register(binding.root,
                Callback.OnReloadListener { reload() },
                Convertor<BaseBean<*>?> { t ->
                    if (t == null || t.errorCode != 0) {
                        ErrorCallback::class.java
                    } else {
                        SuccessCallback::class.java
                    }
                }) as LoadService<BaseBean<*>?>
            return rootView
        } else {
            binding = DataBindingUtil.inflate(inflater, initContentView(), container, false)
            loadService = loadSir.register(binding.root,
                Callback.OnReloadListener { reload() },
                Convertor<BaseBean<*>?> { t ->
                    if (t == null || t.errorCode != 0) {
                        ErrorCallback::class.java
                    } else {
                        SuccessCallback::class.java
                    }
                }) as LoadService<BaseBean<*>?>
        }
        return loadService.loadLayout
    }


    /**
     * 是否需要标题栏
     */
    protected open fun useBaseLayout(): Boolean {
        return true
    }

    override fun initParam() {

    }

    /**
     * 初始化根布局
     *@return 布局layout的id
     */
    abstract fun initContentView(): Int

    /**
     * 初始化viewmodel的id
     * @return BR的id
     */
    abstract fun initVariableId(): Int

    /**
     * 子类重写页面重试加载逻辑
     */
    open fun reload() {
        loadService.showCallback(LoadingCallback::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ImmersionBar.destroy(this)
        binding.unbind()
        rootBinding?.unbind()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //私有的初始化DataBinding和ViewModel方法
        initViewDataBinding()
        //私有的ViewModel与View的契约时间回调逻辑
        registerUIChangeLiveDataCallBack()

    }

    /**
     * 正常创建启动Fragment情况 onViewCreated-onLazyInitView-onEnterAnimationEnd
     * Viewpager创建实例 onViewCreated-onLazyInitView
     */
    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        if (enableLazy()){
            //页面初始化数据
            initData()
            //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
            initViewObservable()
        }
    }

    /**
     * 入栈动画完毕后执行
     */
    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)
        if (!enableLazy()){
            //页面初始化数据
            initData()
            //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
            initViewObservable()
        }

    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        if (isImmersionBarEnabled()){
            initStatusBar()
        }

        if (isThemeRedStatusBar()){
            initFitThemeStatusBar()
        }
    }

    open fun initStatusBar() {
        ImmersionBar.with(this)
            .statusBarDarkFont(!DayModeUtil.isNightMode(requireContext()), 0.2f)
            .statusBarColor(R.color.color_toolbar).init()
    }

    open fun isThemeRedStatusBar(): Boolean {
        return false
    }

    open fun initFitThemeStatusBar() {
        if (!DayModeUtil.isNightMode(requireContext())) {
            ImmersionBar.with(this)
                .statusBarDarkFont(false, 0.2f)
                .statusBarColor(R.color.md_theme_red)
                .fitsSystemWindows(true)
                .init()
        } else {
            ImmersionBar.with(this)
                .statusBarDarkFont(false, 0.2f)
                .statusBarColor(R.color.color_toolbar)
                .fitsSystemWindows(true)
                .init()
        }

    }



    /**
     * 注册viewmodel与view的契约UI回掉事件
     */
    private fun registerUIChangeLiveDataCallBack() {
        //加载对话框显示
        viewModel.uC.getShowLoadingEvent().observe(this, { title: String? ->
            showLoading(title)
        })
        //加载对话框消失
        viewModel.uC.getDismissDialogEvent().observe(this, {
            dismissLoading()
        })

        //调入新页面
        viewModel.uC.getStartActivityEvent().observe(this, { map ->
            val routePath: String = map[BaseViewModel.ParameterField.ROUTE_PATH] as String
            val bundle = map[BaseViewModel.ParameterField.BUNDLE] as Bundle?
            RouteCenter.navigate(routePath, bundle)
        })

        viewModel.uC.getStartFragmentEvent().observe(this, { map ->
            val routePath: String = map[BaseViewModel.ParameterField.ROUTE_PATH] as String
            val bundle: Bundle? = map[BaseViewModel.ParameterField.BUNDLE] as Bundle?
            start(RouteCenter.navigate(routePath, bundle) as SupportFragment)
        })

        //跳入ContainerActivity
        viewModel.uC.getStartContainerActivityEvent().observe(this, { params: Map<String?, Any?> ->
            val canonicalName = params[BaseViewModel.ParameterField.ROUTE_PATH] as String?
            val bundle = params[BaseViewModel.ParameterField.BUNDLE] as Bundle?
            val code = params[BaseViewModel.ParameterField.REQUEST_CODE] as Int?
            startContainerActivity(canonicalName, bundle, code)
        })

        //关闭界面
        viewModel.uC.getFinishEvent().observe(this,{
            back();
        })

        //关闭上一层
        viewModel.uC.getOnBackPressedEvent().observe(this,{
            { onBackPressedSupport() }
        })

        viewModel.uC.getScrollTopEvent().observe(this,{
            rvCommon?.smoothScrollToPosition(0)
        })

        viewModel.uC.getShowSharePopEvent().observe(this,{
            XPopup.Builder(context)
                .enableDrag(true)
                .moveUpToKeyboard(true)
                .autoOpenSoftInput(true)
                .asCustom(ShareArticlePopView(requireActivity() as BaseActivity<*, *>,it))
                .show()
        })
    }


    /**
     * 统一处理列表数据
     */
    fun <T> handleRecyclerViewData(
        nullFlag: Boolean,
        data: MutableList<*>?,
        mAdapter: BaseQuickAdapter<T,*>,
        ryCommon: ShimmerRecyclerView,
        smartCommon: SmartRefreshLayout,
        currentPage: Int,
        over: Boolean?,
        defaultPage: Int = 0
    ){
        this.rvCommon = ryCommon
        if (nullFlag){
            if (currentPage == defaultPage - 1)
                showErrorStatePage()
            smartCommon.finishRefresh(false)
            smartCommon.finishLoadMore(false)
            return
        }
        showSuccessStatePage()
        if (currentPage == defaultPage){
            ryCommon.hideShimmerAdapter()
            if (!mAdapter.hasEmptyView()){
                val emptyView = View.inflate(context,R.layout.common_empty_layout,null)
                emptyView.findViewById<ViewGroup>(R.id.ll_empty).setOnClickListener{
                    smartCommon.autoRefresh()
                }
                mAdapter.setEmptyView(emptyView)
            }
            mAdapter.setDiffNewData(data as MutableList<T>?)
            ryCommon.smoothScrollToPosition(0)
            if (over!!)smartCommon.finishRefreshWithNoMoreData()
            else smartCommon.finishRefresh(true)
            return
        }

        if (over!!)smartCommon.finishRefreshWithNoMoreData()
        else smartCommon.finishRefresh(true)
        smartCommon.finishLoadMore(true)
        mAdapter.addData(data as MutableList<T>)
    }


    /**
     * 是否开启懒加载，默认true
     */
    protected open fun enableLazy(): Boolean{
        return true
    }


    fun showErrorToast(msg: String?) {
        ToastHelper.showErrorToast(msg)
    }

    fun showNormalToast(msg: String?) {
        ToastHelper.showNormalToast(msg)
    }

    fun showSuccessToast(msg: String?) {
        ToastHelper.showSuccessToast(msg)
    }



    fun showErrorStatePage() {
        loadService.showCallback(ErrorCallback::class.java)
    }

    fun showLoadingStatePage(){
        loadService.showCallback(LoadingCallback::class.java)
    }

    fun showSuccessStatePage(){
        loadService.showCallback(SuccessCallback::class.java)
    }


    /**
     * 跳转容器页面
     * @param routePath Fragment路由地址
     * @param bundle 跳转所携带的信息
     */
    fun startContainerActivity(routePath: String?, bundle: Bundle? =null, reqCode: Int? = null) {
        val intent = Intent(context, ContainerFmActivity::class.java)
        intent.putExtra(ContainerFmActivity.FRAGMENT,routePath)
        if (bundle!=null){
            intent.putExtra(ContainerFmActivity.BUNDLE,bundle)
        }
        if (reqCode == null){
            startActivity(intent)
        }else{
            startActivityForResult(intent,reqCode)
        }
    }


    /**
     * 刷新布局
     */
    fun refreshLayout(){
        binding.setVariable(viewModelId,viewModel)
    }




    /**
     * 注入绑定
     */
    private fun initViewDataBinding() {
        viewModelId = initVariableId()
        viewModel = initViewModel()

        rootBinding?.setVariable(viewModelId, viewModel)
        rootBinding?.lifecycleOwner = this
        binding.setVariable(viewModelId, viewModel)
        //支持LiveData绑定xml，数据改变，UI自动会更新
        binding.lifecycleOwner = this
        //让ViewModel拥有View的生命周期感应
        lifecycle.addObserver(viewModel)
        //注入RxLifecycle生命周期
        viewModel.injectLifecycleProvider(this)
        //传入VM层交由M层数据驱动处理UI状态
        viewModel.loadService = loadService
    }

    /**
     * 动态实例化ViewModel
     * @return 继承BaseViewModel的ViewModel
     */
    private fun initViewModel(): VM {
        val type = javaClass.genericSuperclass
        val modelClass: Class<VM> = (type as ParameterizedType).actualTypeArguments[1] as Class<VM>
        return ViewModelProvider(this, get() as AppViewModelFactory).get(modelClass)
    }

    override fun initData() {

    }

    override fun initViewObservable() {

    }

    /**
     * 是否现实状态栏
     */
    open fun isImmersionBarEnabled(): Boolean {
        return useBaseLayout()
    }


    /**
     * 统一处理回退事件
     */
    open fun back() {
        if (preFragment == null){
            requireActivity().finish()
        }else{
            pop()
        }
    }

    fun showLoading(title: String?) {
        dialog = DialogHelper.showLoadingDialog(requireContext(), title)
    }

    fun dismissLoading() {
        dialog?.smartDismiss()
    }


}

