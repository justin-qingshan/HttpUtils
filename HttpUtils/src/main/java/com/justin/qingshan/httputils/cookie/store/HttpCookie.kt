package com.justin.qingshan.httputils.cookie.store

import okhttp3.Cookie
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class HttpCookie(private val cookie: Cookie): Serializable {

    private var clientCookie: Cookie? = null

    fun getCookie(): Cookie {
        return clientCookie ?: cookie
    }

    fun writeObject(out: ObjectOutputStream) {
        out.writeObject(cookie.name)
        out.writeObject(cookie.value)
        out.writeLong(cookie.expiresAt)
        out.writeObject(cookie.domain)
        out.writeObject(cookie.path)
        out.writeBoolean(cookie.secure)
        out.writeBoolean(cookie.httpOnly)
        out.writeBoolean(cookie.hostOnly)
        out.writeBoolean(cookie.persistent)
    }

    fun readObj(ins: ObjectInputStream) {
        clientCookie = Cookie.Builder()
            .name(ins.readObject() as String)
            .value(ins.readObject() as String)
            .expiresAt(ins.readLong())
            .domain(ins.readObject() as String)
            .path(ins.readObject() as String)
            .apply {
                if (ins.readBoolean()) secure()
            }
            .apply {
                if (ins.readBoolean()) httpOnly()
            }
            .apply {
                ins.readBoolean()
                ins.readBoolean()
            }
            .build()
    }

}