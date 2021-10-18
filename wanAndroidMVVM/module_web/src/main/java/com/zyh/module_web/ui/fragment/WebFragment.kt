package com.zyh.module_web.ui.fragment

import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.*
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.utils.TextUtils
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.czl.lib_base.config.AppConstants
import com.google.android.material.appbar.AppBarLayout
import com.just.agentweb.*
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import com.zyh.lib_base.base.BaseFragment
import com.zyh.module_web.BR
import com.zyh.module_web.R
import com.zyh.module_web.databinding.WebFragmentWebBinding
import com.zyh.module_web.viewmodel.WebFmViewModel


/**
 * author: zhouyh
 * created on: 2021/9/22 4:25 下午
 * description:webview页面
 */
@Route(path = AppConstants.Router.Web.F_WEB)
class WebFragment : BaseFragment<WebFragmentWebBinding, WebFmViewModel>() {

    var agentWeb: AgentWeb? = null

    //是否加载失败
    private var errorFlag = false

    //当前web标题
    var currentTitle: String? = null

    //当前web链接
    var currentLink: String? = null

    //首页链接
    var homeUrl: String? = null

    //首页标题
    var homeTitle: String? = null


    override fun initContentView(): Int {
        return R.layout.web_fragment_web
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun isThemeRedStatusBar(): Boolean {
        return true
    }

    override fun useBaseLayout(): Boolean {
        return false
    }

    override fun initData() {
        super.initData()
        initWebView()
        /**
         * 监听编辑操作
         */
        binding.etWeb.apply {
            setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    // 得到一个长度为4的数组，分别表示左右上下四张图片 如果右边没有图片，不再处理
                    val drawable = compoundDrawables[2] ?: return false
                    //如果不是按下事件不要处理
                    if (event?.action != MotionEvent.ACTION_UP) {
                        return false
                    }
                    if (event?.x > width - paddingRight - drawable.intrinsicWidth) {
                        if (binding.etWeb.text.isNullOrEmpty()) return false
                        agentWeb?.urlLoader?.loadUrl(binding.etWeb.text.toString())
                    }
                    return false
                }

            })

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_GO, EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_SEARCH -> {
                        if (!binding.etWeb.text.isNullOrBlank()) {
                            agentWeb?.urlLoader?.loadUrl(binding.etWeb.text.toString())
                            true
                        } else {
                            false
                        }
                    }
                    else -> false
                }
            }
        }
    }


    //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
    override fun initViewObservable() {

        viewModel.uc.closeEvent.observe(this, {
            requireActivity().finish()
        })

        viewModel.uc.goForwardEvent.observe(this, {
            if (viewModel.canForwardFlag.get()!!) agentWeb?.webCreator?.webView?.goForward()
        })

        viewModel.uc.collectEvent.observe(this,{
            if (errorFlag){
                showNormalToast("当前页面加载错误,收藏失败")
                return@observe
            }
            viewModel.collectWebsite(currentTitle,currentLink)
            clearEditFocus()

        })

        viewModel.uc.copyCurrentLinkEvent.observe(this,{
            ClipboardUtils.copyText(currentLink)
            showSuccessToast("复制成功")
            clearEditFocus()
        })

        viewModel.uc.openBrowserEvent.observe(this,{
            clearEditFocus()
            var url = Uri.parse(currentLink)
            val intent = Intent(Intent(Intent.ACTION_VIEW,url))
            startActivity(intent)
        })

        viewModel.uc.showMenuEvent.observe(this,{

        })


        viewModel.showWebLinkMenuFlag.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (viewModel.showWebLinkMenuFlag.get()!!) {
                    binding.etWeb.setText(currentLink)
                } else {
                    binding.etWeb.setText(currentTitle)
                }
            }
        })




    }

    /**
     * 初始化webview
     */
    private fun initWebView() {
        val webView = NestedScrollAgentWebView(context)
        val lp = CoordinatorLayout.LayoutParams(-1, -1)
        //设置联动
        lp.behavior = AppBarLayout.ScrollingViewBehavior()
        homeUrl = arguments?.getString(AppConstants.BundleKey.WEB_URL)
        viewModel.collectFlag.set(arguments?.getBoolean(AppConstants.BundleKey.WEB_URL_COLLECT_FLAG))
        //初始化agentWeb
        agentWeb = AgentWeb.with(this)
            //设置web父容器
            .setAgentWebParent(binding.clWebRoot, 1, lp)
            //设置默认加载进度条指示器
            .useDefaultIndicator(ContextCompat.getColor(requireContext(), R.color.md_theme_red), 1)
            .setWebView(webView)
            .interceptUnkownUrl()
            .setWebViewClient(mWebClient)
            .setWebChromeClient(object : WebChromeClient(){
                override fun onReceivedTitle(view: WebView, title: String?) {
                    super.onReceivedTitle(view, title)
                    //设置标题
                    viewModel.tvTitle.set(title)
                    if (currentLink == homeUrl && !view.canGoBack() && !errorFlag){
                        this@WebFragment.homeTitle = title
                        //保存到本地
                        if (!TextUtils.isEmpty(homeUrl)&&!TextUtils.isEmpty(homeTitle)){
                            viewModel.saveBrowseHistory(homeTitle!!,homeUrl!!)
                        }
                    }
                    this@WebFragment.currentTitle = title
                }
            })
            .createAgentWeb()
            .ready()
            .go(homeUrl)

        //设置WebSettings
        webView.settings.apply {
            //支持javascript
            javaScriptEnabled = true
            //设置可以缩放
            setSupportZoom(true)
            // 设置出现缩放工具
            builtInZoomControls = true
            //扩大比例的缩放
            useWideViewPort = true
            //自适应屏幕
            loadWithOverviewMode = true
            displayZoomControls = false
        }
    }

    /**
     * 重新加载
     */
    override fun reload() {
        super.reload()
        agentWeb?.urlLoader?.reload()
    }

    /**
     * web回退处理
     */
    override fun back() {
        if (!viewModel.canGoBackFlag.get()!!){
            super.back()
        }else{
            agentWeb?.back()
        }
    }

    /**
     *支持返回键事件
     */
    override fun onBackPressedSupport(): Boolean {
        agentWeb?:return super.onBackPressedSupport()
        if (agentWeb!!.webCreator.webView.canGoBack()){
            agentWeb!!.back()
        }else{
            return super.onBackPressedSupport()
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        agentWeb?.webLifeCycle?.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        agentWeb?.webLifeCycle?.onResume()
    }

    override fun onPause() {
        super.onPause()
        agentWeb?.webLifeCycle?.onPause()
    }

    /**
     * 支持懒加载
     */
    override fun enableLazy(): Boolean {
        return false
    }

    //web客户端对象
    private val mWebClient = object : WebViewClient() {


        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            errorFlag = false
            currentLink = url
            clearEditFocus()
        }


        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            //拦截apk下载链接
            if (url.startsWith(DefaultWebClient.INTENT_SCHEME) && url.endsWith(".apk")){
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                return true
            }
            return super.shouldOverrideUrlLoading(view,url)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest,
        ): Boolean {
            val url = request.url.toString()
            if (url.startsWith(DefaultWebClient.INTENT_SCHEME) || url.endsWith(".apk")
            ) {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return true
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?,
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return
            }
            errorFlag = true
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError,
        ) {
            super.onReceivedError(view, request, error)
            if (request.isForMainFrame){
                errorFlag = true
            }
        }

        override fun onReceivedHttpError(
            view: WebView?,
            request: WebResourceRequest?,
            errorResponse: WebResourceResponse?,
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
            val statusCode = errorResponse?.statusCode
            if (404 == statusCode || 500 == statusCode) {
                errorFlag = true
                showErrorStatePage()
            }
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (!errorFlag){
                showSuccessStatePage()
            }
            viewModel.canForwardFlag.set(view.canGoForward())
            if (currentLink==homeUrl && !view.canGoBack()){
                viewModel.canGoBackFlag.set(false)
                return
            }
            viewModel.canGoBackFlag.set(view.canGoBack())
        }


    }

    private fun clearEditFocus() {
        binding.etWeb.apply {
            clearFocus()
            KeyboardUtils.hideSoftInput(this)
        }
    }


}