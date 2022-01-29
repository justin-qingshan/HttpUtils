package com.justin.qingshan.httputils.request

import okhttp3.Request
import okhttp3.RequestBody

/**
 * Request call to GET
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
class GetRequestCall<T>: RequestCall<T>() {

    override fun buildRequestBody(): RequestBody? {
        return null
    }

    override fun buildRequest(requestBuilder: Request.Builder, requestBody: RequestBody?): Request {
        return requestBuilder.get().build()
    }
}