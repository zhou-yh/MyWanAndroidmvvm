package com.zyh.lib_base.di

import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.zyh.lib_base.api.ApiService
import com.zyh.lib_base.base.AppManager
import com.zyh.lib_base.base.AppViewModelFactory
import com.zyh.lib_base.base.BaseActivity
import com.zyh.lib_base.base.MyApplication
import com.zyh.lib_base.data.DataRepository
import com.zyh.lib_base.data.source.HttpDataImpl
import com.zyh.lib_base.data.source.HttpDataSource
import com.zyh.lib_base.data.source.LocalDataImpl
import com.zyh.lib_base.data.source.LocalDataSource
import com.zyh.lib_base.net.RetrofitClient
import com.zyh.lib_base.widget.LoginPopView
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.ConcurrentHashMap


/**
 * author: zhouyh
 * created on: 2021/5/12 5:42 下午
 * description:di注入的module
 */

val appModule = module {
    single { androidApplication() as MyApplication }
    // single->单例式  factory->每次都创建不同实例  viewModel->VM注入
    // androidApplication()->获取当前Application , androidContext() -> 获取context
    //1.获取api实例
    single { RetrofitClient.getInstance().create(ApiService::class.java) }
    // 2. 创建实例前若构造方法内有参数 则需先注入构造中的参数实例
    single<HttpDataSource> { HttpDataImpl(get()) }
    // 3. 获取本地数据调用的实例
    single<LocalDataSource> { LocalDataImpl() }
    // 4 .综合以上本地+网络两个数据来源 得到数据仓库
    single { DataRepository(get(), get()) }
    // bind 将指定的实例绑定到对应的class  single { AppViewModelFactory(androidApplication(), get()) } bind TestActivity::class
    single { AppViewModelFactory(get(), get()) }
    // 维护一个全局单例的登录弹窗组 避免多次弹出
    single(named("login_map")) { ConcurrentHashMap<Int, BasePopupView>(1) }
}

val factoryModule = module {
    // 用户模块 主动点击按钮弹出的pop
    factory(named("login")) {
        XPopup.Builder(AppManager.instance.currentActivity())
            .enableDrag(true)
            .moveUpToKeyboard(false)
            .autoOpenSoftInput(true)
            .asCustom(LoginPopView(AppManager.instance.currentActivity() as BaseActivity<*, *>))
    }

    // 未登录时  接口请求被动弹出的pop
    factory(named("token_login")) {
        XPopup.Builder(AppManager.instance.currentActivity())
            .enableDrag(true)
            .moveUpToKeyboard(false)
            .autoOpenSoftInput(true)
            .asCustom(LoginPopView(AppManager.instance.currentActivity() as BaseActivity<*, *>))
    }

}

val allModule = appModule + factoryModule