package com.justin.qingshan.httputils.callback

import okhttp3.Response

/**
 * Callback to parse [Response.body] to [String]
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
abstract class StringCallback : Callback<String>() {

    override fun parseNetworkResp(response: Response, id: Int): String {
        return response.body?.string() ?: ""
    }
}