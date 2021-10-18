package com.zyh.lib_base.net


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zyh.lib_base.base.BaseBean
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http.HttpHeaders
import java.nio.charset.StandardCharsets


/**
 * author: zhouyh
 * created on: 2021/5/12 10:58 上午
 * description: 对服务器返回结果处理解密 token失效等
 */
class ResponseInterceptor:Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        val responseBody = response.body()
        val url = request.url().toString()
        if (HttpHeaders.hasBody(response)){
            if (responseBody == null)return response
            val mediaType = responseBody.contentType()
            when(response.code()){
                503, 504, 500, 404, 403, 400->{
                    response = response.newBuilder().code(response.code()).message("连接服务器失败，请稍后再试").build()
                }
                200->{
                    val source = responseBody.source()
                    source.request(Long.MAX_VALUE)
                    val buffer = source.buffer
                    var charset = StandardCharsets.UTF_8
                    if (charset!=null){
                        // 服务器返回结果 可处理加解密或者token失效
                        val bodyString = buffer.clone().readString(charset)
                        val baseBean = Gson().fromJson<BaseBean<*>>(
                            bodyString,object :TypeToken<BaseBean<*>>(){}.type)
                        when(baseBean.errorCode){
                            -1001->{
                                // token/cookie失效 ApiSubscriberHelper 已在ApiSubscriberHelper网络业务层处理
                                // LiveBusCenter.postTokenExpiredEvent(baseBean.errorMsg)
                            }
                        }
                        val newResponseBody = ResponseBody.create(mediaType,bodyString)
                        response = response.newBuilder().body(newResponseBody).build()
                    }
                }
            }
        }
        return response

    }
}