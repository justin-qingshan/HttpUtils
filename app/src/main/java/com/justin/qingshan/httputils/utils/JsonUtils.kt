package com.justin.qingshan.httputils.utils

import com.google.gson.Gson

val GSON = Gson()

fun <T> String.toEntity(clazz: Class<T>) : T {
    return GSON.fromJson(this, clazz)
}

fun <T> T.toJsonString(): String {
    return GSON.toJson(this)
}