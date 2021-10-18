package com.zyh.lib_base.base

import java.io.Serializable


/**
 * author: zhouyh
 * created on: 2021/5/11 5:14 下午
 * description: 基类bean
 */
class BaseBean<T> : Serializable {

    var data : T ?= null
    var errorCode : Int = 0
    var errorMsg : String ?= null

    /**
     * 伴生对象，只加载一次，只有在调用的时候伴生对象才会载入
     */
    companion object{
        private const val serializableUid = 5223230387175917834L
    }

    override fun toString(): String {
        return "BaseBean(data=$data, errorCode=$errorCode, errorMsg=$errorMsg)"
    }


}