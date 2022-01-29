package com.justin.qingshan.httputils.request

import android.text.TextUtils
import com.justin.qingshan.httputils.*
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http.HttpMethod
import java.lang.IllegalArgumentException

/**
 * Request call used to HEAD/PUT/DELETE/PATCH
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
class OtherRequestCall<T>(private val method: String): RequestCall<T>() {

    private var requestBody: RequestBody? = null
    private var content: String? = null

    fun requestBody(requestBody: () -> RequestBody) = apply { this.requestBody = requestBody() }

    fun content(content: () -> String) = apply { this.content = content() }

    override fun buildRequestBody(): RequestBody? {
        return if (requestBody == null
            && TextUtils.isEmpty(content)
            && HttpMethod.requiresRequestBody(method)) {
            throw IllegalStateException("requestBody and content can not be null in method: $method")
        } else if (requestBody == null && !TextUtils.isEmpty(content)) {
            content?.toRequestBody(MEDIA_TYPE_PLAIN)
        } else {
            requestBody
        }
    }

    override fun buildRequest(requestBuilder: Request.Builder, requestBody: RequestBody?): Request {
        return when(method) {
            METHOD_PUT -> if (requestBody == null) {
                throw IllegalArgumentException("no request body found when $method")
            } else {
                requestBuilder.put(requestBody)
            }
            METHOD_DELETE -> if (requestBody == null) requestBuilder.delete() else requestBuilder.delete(requestBody)
            METHOD_HEAD -> requestBuilder.head()
            METHOD_PATCH -> if (requestBody == null) {
                throw IllegalArgumentException("no request body found when $method")
            } else {
                requestBuilder.patch(requestBody)
            }
            else -> requestBuilder
        }.build()
    }

}