package com.zyh.lib_base.net

import com.zyh.lib_base.data.DataRepository
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


/**
 * author: zhouyh
 * created on: 2021/5/12 10:24 上午
 * description:请求参数拦截器
 */
class RequestDataInterceptor:Interceptor,KoinComponent {

    private val dataRepository by inject<DataRepository>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request();
        val url = request.url()
        val method = request.method()
        if ("POST" == method){
            val body = request.body()
            val builder = request.url().newBuilder()
            //将源body写入缓存
            val buffer = Buffer()
            body?.writeTo(buffer)
            val requestBuilder = request.newBuilder()
            // 未加密的请求参数
            val requestData = URLDecoder.decode(buffer.readString(StandardCharsets.UTF_8),"utf-8")
            // 新的请求体
            val newRequestBody: RequestBody
            try {
                // 可在这对请求参数加密后重新发起请求
                newRequestBody = RequestBody.create(body?.contentType(),requestData)
                val newRequest = requestBuilder
                    .post(newRequestBody)
                    .url(builder.build())
                    .build()
                return chain.proceed(newRequest)
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
        return chain.proceed(request)

    }
}