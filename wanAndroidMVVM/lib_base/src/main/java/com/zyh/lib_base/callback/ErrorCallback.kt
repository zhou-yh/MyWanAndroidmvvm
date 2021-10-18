package com.zyh.lib_base.callback

import com.kingja.loadsir.callback.Callback
import com.zyh.lib_base.R


/**
 * author: zhouyh
 * created on: 2021/9/14 8:56 下午
 * description:
 */
class ErrorCallback: Callback() {
    override fun onCreateView(): Int {
        return R.layout.common_error_layout
    }
}