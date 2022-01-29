package com.justin.qingshan.httputils.cookie.store

import okhttp3.Cookie
import okhttp3.HttpUrl

interface CookieStore {

    fun add(uri: HttpUrl, cookieList: List<Cookie>)

    fun get(uri: HttpUrl): List<Cookie>

    fun getAll(): List<Cookie>

    fun remove(uri: HttpUrl, cookie: Cookie?): Boolean

    fun removeAll(): Boolean
}