package com.justin.qingshan.httputils.cookie.store

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.justin.qingshan.httputils.utils.hexBytes
import com.justin.qingshan.httputils.utils.hexString
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PersistentCookieStore(context: Context) : CookieStore {

    companion object {
        const val COOKIE_NAME_PREFIX = "cookie_"
    }

    private var cookiePrefs: SharedPreferences = context.getSharedPreferences(COOKIE_NAME_PREFIX, 0)
    private val cookies: MutableMap<String, ConcurrentHashMap<String, Cookie>> = mutableMapOf()

    init {
        cookiePrefs.all.entries.forEach {
            val value = it.value as String?
            if (value != null && value.startsWith(COOKIE_NAME_PREFIX)) {
                val cookieNames = TextUtils.split(value, ",")
                Arrays.stream(cookieNames).forEach { _cookieName ->
                    val encodedCookie =
                        cookiePrefs.getString("$COOKIE_NAME_PREFIX$_cookieName", null)
                    encodedCookie?.let { _encodedCookie ->
                        val decodedCookie = decodeCookie(_encodedCookie)
                        val map = (cookies[it.key] ?: ConcurrentHashMap())
                        map[_cookieName] = decodedCookie
                        cookies[it.key] = map
                    }
                }
            }
        }

    }

    override fun add(uri: HttpUrl, cookieList: List<Cookie>) {
        cookieList.forEach {
            add(uri, it)
        }
    }

    override fun get(uri: HttpUrl): List<Cookie> {
        val cookieList : MutableList<Cookie> = mutableListOf()
        cookies[uri.host]?.values?.forEach {
            if (it.expiresAt < System.currentTimeMillis()) {
                remove(uri, it)
            } else {
                cookieList.add(it)
            }
        }
        return cookieList
    }

    override fun getAll(): List<Cookie> {
        return cookies.flatMap {
            it.value.values
        }
    }

    override fun remove(uri: HttpUrl, cookie: Cookie?): Boolean {
        if (cookie == null) {
            return false
        }
        val name = getCookieToken(cookie)

        cookies[uri.host]?.let {
            it.remove(name)
            val prefWriter = cookiePrefs.edit()
            if (cookiePrefs.contains(COOKIE_NAME_PREFIX + name)) {
                prefWriter.remove(COOKIE_NAME_PREFIX + name)
            }
            prefWriter.putString(uri.host, TextUtils.join(",", cookies[uri.host]?.keys ?: mutableListOf<String>()))
            prefWriter.apply()
            return true
        }
        return false
    }

    override fun removeAll(): Boolean {
        val prefWriter = cookiePrefs.edit()
        prefWriter.clear()
        prefWriter.apply()
        cookies.clear()
        return true
    }

    private fun add(uri: HttpUrl, cookie: Cookie) {
        val name = getCookieToken(cookie)
        if (cookie.persistent) {
            val map = cookies[uri.host] ?: ConcurrentHashMap()
            map[name] = cookie
            cookies[uri.host] = map
        } else {
            cookies[uri.host]?.remove(name)
        }
        val prefWriter = cookiePrefs.edit()
        prefWriter.putString(uri.host, TextUtils.join(",", cookies[uri.host]?.keys ?: mutableSetOf<String>()))
        prefWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(HttpCookie(cookie)))
        prefWriter.apply()
    }

    private fun getCookieToken(cookie: Cookie): String {
        return cookie.name + cookie.domain
    }

    private fun encodeCookie(cookie: HttpCookie): String {
        val os = ByteArrayOutputStream()
        val oos = ObjectOutputStream(os)
        oos.writeObject(cookie)
        return os.toByteArray().hexString()
    }

    private fun decodeCookie(cookieString: String): Cookie {
        val bytes = cookieString.hexBytes()
        val stream = ByteArrayInputStream(bytes)
        val ins = ObjectInputStream(stream)
        return (ins.readObject() as HttpCookie).getCookie()
    }
}
