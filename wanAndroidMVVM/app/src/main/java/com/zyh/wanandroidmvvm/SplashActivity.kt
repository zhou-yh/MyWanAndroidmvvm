package com.zyh.wanandroidmvvm


import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.czl.lib_base.config.AppConstants
import com.gyf.immersionbar.ImmersionBar
import com.zyh.lib_base.base.BaseActivity
import com.zyh.lib_base.route.RouteCenter
import com.zyh.lib_base.utils.DayModeUtil
import com.zyh.wanandroidmvvm.databinding.AppActivitySplashBindingImpl
import com.zyh.wanandroidmvvm.login.viewmodel.SplashViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * author: zhouyh
 * created on: 2021/5/12 6:58 下午
 * description:
 */
@Route(path=AppConstants.Router.App.F_Splash)
class SplashActivity : BaseActivity<AppActivitySplashBindingImpl,SplashViewModel>() {


    private val arrayLight = arrayListOf(R.drawable.splash_bg_light, R.drawable.bg_splash_light2)
    private val arrayDark = arrayListOf(R.drawable.splash_bg_dark, R.drawable.bg_splash_dark2)

    override fun initContentView(): Int {
       return R.layout.app_activity_splash
    }

    override fun initParam() {
        ImmersionBar.hideStatusBar(window)
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }


    override fun useBaseLayout(): Boolean {
        return false
    }

    override fun initData() {
        initSplashBg()
        toLogin()
    }


    private fun initSplashBg() {
        Glide.with(this).load(
            if (DayModeUtil.isNightMode(this))
                arrayDark[Random().nextInt(arrayDark.size)]
            else arrayLight[Random().nextInt(arrayLight.size)]
        )
            .override(ScreenUtils.getAppScreenWidth(), ScreenUtils.getAppScreenHeight())
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(true)
            .into(binding.ivSplash)
    }



    private fun toLogin(){
        viewModel.addSubscribe(
            Flowable.timer(1500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    if (viewModel.model.getLoginName().isNullOrBlank()){
                        startContainerActivity(AppConstants.Router.Login.F_LOGIN)
                        overridePendingTransition(R.anim.h_fragment_enter,0)
                    }else{
                        RouteCenter.navigate(AppConstants.Router.Main.A_MAIN)
                    }
                    finish()
                }
        )
    }
}