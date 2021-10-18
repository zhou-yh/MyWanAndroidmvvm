package com.zyh.lib_base.route

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter


/**
 * author: zhouyh
 * created on: 2021/5/12 7:13 下午
 * description: 路由
 */
object RouteCenter {
    fun navigate(path:String,bundle : Bundle?=null):Any?{
        val build = ARouter.getInstance().build(path)
        return if (bundle == null)build.navigation()else build.with(bundle).navigation()
    }
}