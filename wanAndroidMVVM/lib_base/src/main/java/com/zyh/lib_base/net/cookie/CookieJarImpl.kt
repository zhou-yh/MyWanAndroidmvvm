package com.zyh.lib_base.net.cookie

import com.zyh.lib_base.net.cookie.store.CookieStore
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl



/**
 * author: zhouyh
 * created on: 2021/5/12 12:07 下午
 * description:
 */
class CookieJarImpl(cookieStore: CookieStore?):CookieJar {

    private val cookieStore : CookieStore
    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        cookieStore.saveCookie(url,cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie?> {
        return cookieStore.loadCookie(url)
    }

    init {
        requireNotNull(cookieStore) { "cookieStore can not be null!" }
        this.cookieStore = cookieStore
    }
}