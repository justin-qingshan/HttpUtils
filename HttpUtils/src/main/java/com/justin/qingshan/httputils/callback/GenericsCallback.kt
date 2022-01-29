package com.justin.qingshan.httputils.callback

import okhttp3.Response

/**
 * Callback to parse [Response.body] to specific Entity of [T]
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
abstract class GenericsCallback<T>: Callback<T>() {

    override fun parseNetworkResp(response: Response, id: Int): T {
        val str = response.body?.string()
        return transform(str ?: "")
    }

    abstract fun transform(str: String): T
}