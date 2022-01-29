package com.justin.qingshan.httputils.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CookieJarImpl(): CookieJar {

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        TODO("Not yet implemented")
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        TODO("Not yet implemented")
    }
}