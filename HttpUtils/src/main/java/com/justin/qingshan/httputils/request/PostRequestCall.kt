package com.justin.qingshan.httputils.request

import com.justin.qingshan.httputils.MEDIA_TYPE_JSON
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Request call to post string.
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
class PostRequestCall<T>: RequestCall<T>() {

    private var content: String = ""
    private var mediaType: MediaType = MEDIA_TYPE_JSON

    fun content(content: () -> String) = apply { this.content = content() }

    fun mediaType(mediaType: () -> MediaType) = apply { this.mediaType = mediaType() }

    override fun buildRequestBody(): RequestBody {
        return content.toRequestBody(mediaType)
    }

    override fun buildRequest(requestBuilder: Request.Builder, requestBody: RequestBody?): Request {
        return requestBuilder.post(requestBody ?: "".toRequestBody(mediaType))
            .build()
    }
}