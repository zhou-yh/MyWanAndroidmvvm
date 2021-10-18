package com.zyh.lib_base.net

import okhttp3.Interceptor
import okhttp3.Response


/**
 * author: zhouyh
 * created on: 2021/5/12 10:15 上午
 * description:
 */
class BaseInterceptor(private val headers : Map<String,String>?):Interceptor{


    override fun intercept(chain: Interceptor.Chain): Response {
        var builder = chain.request().newBuilder()
        if (headers!=null && headers.isNotEmpty()){
            val keys = headers.keys
            for (headerKey in keys){
                builder.addHeader(headerKey,headers[headerKey]).build()
            }
        }
        return chain.proceed(builder.build())

    }
}