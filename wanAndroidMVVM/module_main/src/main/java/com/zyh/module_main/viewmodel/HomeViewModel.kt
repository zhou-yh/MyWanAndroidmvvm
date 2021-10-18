package com.zyh.module_main.viewmodel


import android.view.View
import androidx.databinding.ObservableInt
import com.blankj.utilcode.util.LogUtils
import com.zyh.lib_base.base.BaseBean
import com.zyh.lib_base.base.BaseViewModel
import com.zyh.lib_base.base.MyApplication
import com.zyh.lib_base.binding.command.BindingAction
import com.zyh.lib_base.binding.command.BindingCommand
import com.zyh.lib_base.binding.command.BindingConsumer
import com.zyh.lib_base.bus.event.SingleLiveEvent
import com.zyh.lib_base.data.DataRepository
import com.zyh.lib_base.data.bean.*
import com.zyh.lib_base.extension.ApiSubscriptHelper
import com.zyh.lib_base.utils.RxThreadHelper
import io.reactivex.Observable
import java.io.Serializable
import java.util.*


/**
 * author: zhouyh
 * created on: 2021/9/18 9:00 下午
 * description:
 */
class HomeViewModel(application: MyApplication, model: DataRepository) :
    BaseViewModel<DataRepository>(application, model) {

    var currentArticlePage: Int = 0
    var currentProjectPage = 0

    val uc = UiChangeEvent()

    var tabSelectedPosition: ObservableInt = ObservableInt(0)

    inner class UiChangeEvent {
        val bannerCompleteEvent: SingleLiveEvent<List<HomeBannerBean>> = SingleLiveEvent()
        val tabSelectedEvent: SingleLiveEvent<Int> = SingleLiveEvent()
        val moveToTopEvent:SingleLiveEvent<Int> = SingleLiveEvent()
        val openDrawerEvent: SingleLiveEvent<Void> = SingleLiveEvent()
        val searchIconClickEvent: SingleLiveEvent<Void> = SingleLiveEvent()
        val loadArticleCompleteEvent: SingleLiveEvent<HomeArticleBean> = SingleLiveEvent()
        val loadProjectCompleteEvent: SingleLiveEvent<ProjectBean> = SingleLiveEvent()
        //获取热点搜索关键字
        val loadSearchHotKeyEvent:SingleLiveEvent<List<SearchHotKeyBean>> = SingleLiveEvent()
    }



    /**
     * 刷新绑定
     */
    val onRefreshListener: BindingCommand<Void> = BindingCommand(BindingAction {
        LogUtils.e("m1 onRefreshListener start")
        currentArticlePage = -1
        currentProjectPage = -1
        getBanner()
        getSearchHotKeyword()
        when (tabSelectedPosition.get()) {
            0 -> getArticle()
            1 -> getProject()
        }
    })

    /**
     * 加载更多
     */
    val onLoadMoreListener:BindingCommand<Void> = BindingCommand(BindingAction {
        LogUtils.e("m1 onLoadMoreListener start")
        when(tabSelectedPosition.get()){
            0-> getArticle()
            1-> getProject()
        }
    })

    /**
     *切换tab
     */
    val onTabSelectedCommand:BindingCommand<Int> = BindingCommand(BindingConsumer {
        tabSelectedPosition.set(it)
        uc.tabSelectedEvent.postValue(it)
    })

    /**
     * 置顶
     */
    val fabOnClickListener: View.OnClickListener = View.OnClickListener {
        uc.moveToTopEvent.postValue(tabSelectedPosition.get())
    }

    /**
     * 搜索按钮事件
     */
    val onIconClickCommand: BindingCommand<Void> = BindingCommand(BindingAction {
        uc.searchIconClickEvent.call()
    })

    /**
     * 打开抽屉
     */
    val openDrawerCommand:BindingCommand<Void> = BindingCommand(BindingAction {
        uc.openDrawerEvent.call()
    })

    /**
     * 收藏
     */
    fun collectArticle(id: Int): Observable<BaseBean<Any?>> {
        return model.collectArticle(id).compose(RxThreadHelper.rxSchedulerHelper(this))
    }

    fun unCollectArticle(id: Int): Observable<BaseBean<Any?>> {
        return model.unCollectArticle(id).compose(RxThreadHelper.rxSchedulerHelper(this))
    }

    /**
     * 获取热门项目
     */
    fun getProject() {
        model.getHomeProject((currentProjectPage+1).toString())
            .compose(RxThreadHelper.rxSchedulerHelper(this))
            .subscribe(object : ApiSubscriptHelper<BaseBean<ProjectBean>>(){
                override fun onResult(t: BaseBean<ProjectBean>) {
                    if (t.errorCode == 0){
                        currentProjectPage++
                        uc.loadProjectCompleteEvent.postValue(t.data)
                    }else{
                        uc.loadProjectCompleteEvent.postValue(null)
                    }
                }

                override fun onFailed(msg: String?) {
                    uc.loadProjectCompleteEvent.postValue(null)
                    showErrorToast(msg)
                }

            })
    }

    /**
     * 获取热门博文
     */
    fun getArticle() {
        if (currentArticlePage == -1) {
            // 当前是第一页的时候合并首页置顶文章
            val mainObservable = model.getHomeArticle(((currentArticlePage + 1).toString()))
            //置顶文章
            val topObservable = model.getHomeTopArticle()
            //合并文章
            Observable.zip(mainObservable, topObservable, { t1, t2 ->
                //将topObservable数据加到mainObservable
                t2.data?.let {
                    it.forEach { data -> data.topFlag = true }
                    t1.data?.datas?.addAll(0, it)
                }
                t1
            }).compose(RxThreadHelper.rxSchedulerHelper())
                .subscribe(object: ApiSubscriptHelper<BaseBean<HomeArticleBean>>(){
                    override fun onResult(t: BaseBean<HomeArticleBean>) {
                        if (t.errorCode==0){
                            currentArticlePage++
                            uc.loadArticleCompleteEvent.postValue(t.data)
                        }else{
                            uc.loadArticleCompleteEvent.postValue(null)
                        }
                    }

                    override fun onFailed(msg: String?) {
                        showErrorToast(msg)
                    }

                })
            return
        }

        model.getHomeArticle((currentArticlePage+1).toString())
            .compose(RxThreadHelper.rxSchedulerHelper())
            .subscribe(object:ApiSubscriptHelper<BaseBean<HomeArticleBean>>(){

                override fun onResult(t: BaseBean<HomeArticleBean>) {
                    if (t.errorCode==0){
                        currentArticlePage++
                        uc.loadArticleCompleteEvent.postValue(t.data)
                    }else{
                        uc.loadArticleCompleteEvent.postValue(null)
                    }
                }
                override fun onFailed(msg: String?) {
                    showErrorToast(msg)
                }



            })


    }

    /**
     * 获取热点关键词
     */
    private fun getSearchHotKeyword() {
        model.getSearchHotKey()
            .compose(RxThreadHelper.rxSchedulerHelper(this))
            .subscribe(object : ApiSubscriptHelper<BaseBean<List<SearchHotKeyBean>>>(){
                override fun onResult(t: BaseBean<List<SearchHotKeyBean>>) {
                    if (t.errorCode == 0){
                        uc.loadSearchHotKeyEvent.postValue(t.data)
                    }
                }

                override fun onFailed(msg: String?) {
                   showErrorToast(msg)
                }

            })
    }


    /**
     * 获取轮播数据
     */
    private fun getBanner() {
        model.getBannerData()
            .compose(RxThreadHelper.rxSchedulerHelper(this))
            .subscribe(object : ApiSubscriptHelper<BaseBean<List<HomeBannerBean>>>(loadService) {
                override fun onResult(t: BaseBean<List<HomeBannerBean>>) {
                    if (t.errorCode == 0) {
                        uc.bannerCompleteEvent.postValue(t.data)
                    }
                }

                override fun onFailed(msg: String?) {
                    showErrorToast(msg)
                }

            })
    }

    /**
     * 得到缓存数据
     */
    fun <T : Serializable>getCacheData(key:String):List<T>{
        return model.getCacheListData<T>(key)?: emptyList()
    }
}
