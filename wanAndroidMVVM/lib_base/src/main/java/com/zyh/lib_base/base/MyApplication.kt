package com.zyh.lib_base.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.czl.lib_base.config.AppConstants
import com.lxj.xpopup.XPopup
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import com.zyh.lib_base.BuildConfig
import com.zyh.lib_base.R
import com.zyh.lib_base.di.allModule
import com.zyh.lib_base.utils.DayModeUtil
import com.zyh.lib_base.utils.SpHelper
import com.zyh.lib_base.utils.ToastHelper
import es.dmoral.toasty.Toasty
import io.reactivex.plugins.RxJavaPlugins
import me.jessyan.autosize.AutoSizeConfig
import me.yokeyword.fragmentation.Fragmentation
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.litepal.LitePal


/**
 * author: zhouyh
 * created on: 2021/5/11 3:12 下午
 * description:
 * 这个类具有`open`属性，可以被其他类继承
 */
open class MyApplication:Application(){

    override fun onCreate() {
        super.onCreate()
        //调试状态下打开log
        if (BuildConfig.DEBUG){
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
        setApplication(this)

        //初始化MMKV
        MMKV.initialize(this)
        //初始化LitePal数据库
        LitePal.initialize(this)
        Fragmentation.builder()
            .stackViewMode(Fragmentation.NONE)  //栈视图模式
            .debug(BuildConfig.DEBUG)
            .install()

        // 屏幕适配
        AutoSizeConfig.getInstance().setCustomFragment(true).setBaseOnWidth(false)
            .setExcludeFontScale(true).designHeightInDp = 720
        //是否开启日志打印
        LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG).setConsoleSwitch(BuildConfig.DEBUG)

        //koin注入
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(allModule)
        }

        RxJavaPlugins.setErrorHandler {
            ToastHelper.showErrorToast("系统错误")
            it.printStackTrace()
        }

        //设置吐司不以循环展示
        Toasty.Config.getInstance().allowQueue(false).apply()
        XPopup.setPrimaryColor(ContextCompat.getColor(this,R.color.md_theme_red))
        //切换情景模式
        initNightModel()
    }

    private fun initNightModel() {
        if (SpHelper.decodeBoolean(AppConstants.SpKey.SYS_UI_MODE)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }else{
            if (SpHelper.decodeBoolean(AppConstants.SpKey.USER_UI_MODE)) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        }
    }


    companion object {
        init {
            ClassicsFooter.REFRESH_FOOTER_FINISH = ""
            SmartRefreshLayout.setDefaultRefreshInitializer { _, layout ->
                layout.apply {
                    setEnableOverScrollDrag(true)
                    setEnableScrollContentWhenLoaded(false)
                    setEnableAutoLoadMore(true)
                    setEnableOverScrollBounce(true)
                    setFooterHeight(60f)
                }
            }
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                layout.apply {
                    setPrimaryColorsId(R.color.md_theme_red, R.color.white)
                }
                MaterialHeader(context).setColorSchemeColors(
                    ContextCompat.getColor(
                        context,
                        R.color.md_theme_red
                    )
                )
            }
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                ClassicsFooter(context).setFinishDuration(0)
            }
        }
    }

    private fun setApplication(application: MyApplication) {
        //初始化工具类
        Utils.init(application)
        //注册监听每个activity的生命周期，便于堆栈式管理
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                AppManager.instance.addActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                AppManager.instance.removeActivity(activity)
            }

        })


    }

}