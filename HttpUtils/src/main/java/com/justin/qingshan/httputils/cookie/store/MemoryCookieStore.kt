package com.justin.qingshan.httputils.cookie.store

import okhttp3.Cookie
import okhttp3.HttpUrl
import java.util.stream.Collectors

class MemoryCookieStore : CookieStore {

    private val allCookies: MutableMap<String, MutableList<Cookie>> = mutableMapOf()

    override fun add(uri: HttpUrl, cookieList: List<Cookie>) {
        val cookies = allCookies[uri.host] ?: mutableListOf()
        val newCookieNameSet = cookieList.stream()
            .map { it.name }
            .collect(Collectors.toSet())
        cookies.removeAll {
            newCookieNameSet.contains(it.name)
        }
        cookies.addAll(cookieList)
        allCookies[uri.host] = cookies
    }

    override fun get(uri: HttpUrl): List<Cookie> {
        return allCookies[uri.host] ?: listOf()
    }

    override fun getAll(): List<Cookie> {
        return allCookies.flatMap { it.value }
    }

    override fun remove(uri: HttpUrl, cookie: Cookie?): Boolean {
        return allCookies[uri.host]?.removeIf { it.name == cookie?.name } ?: false
    }

    override fun removeAll(): Boolean {
        allCookies.clear()
        return true
    }
}