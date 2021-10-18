package com.zyh.lib_base.callback

import com.kingja.loadsir.callback.Callback
import com.zyh.lib_base.R


/**
 * author: zhouyh
 * created on: 2021/9/14 9:04 下午
 * description: 加载页面回调
 */
class LoadingCallback : Callback() {
    override fun onCreateView(): Int {
        return R.layout.common_loading_layout
    }
}