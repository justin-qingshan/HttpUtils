package com.justin.qingshan.httputils.log

import android.text.TextUtils
import android.util.Log
import com.justin.qingshan.httputils.utils.isText
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

/**
 * Implementation of [Interceptor] to log request/response.
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
class LoggerInterceptor(
    private val tag: String = TAG,
    private val showResponse: Boolean = false
): Interceptor {

    companion object {
        const val TAG = "HttpUtils"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        logForRequest(chain.request())
        return logForResp(chain.proceed(chain.request()))
    }

    private fun logForResp(response: Response): Response {
        try {
            Log.i(tag, "=================== response's log ===================")
            val builder = response.newBuilder()
            val clone = builder.build()
            Log.i(tag, "url: ${clone.request.url}")
            Log.i(tag, "code: ${clone.code}")
            Log.i(tag, "protocol: ${clone.protocol}")

            if (!TextUtils.isEmpty(clone.message)) {
                Log.i(tag, "message: ${clone.message}")
            }

            if (showResponse) {
                clone.body?.let {
                    it.contentType()?.let { _mediaType ->
                        Log.i(tag, "response body's content type: $_mediaType")
                        if (_mediaType.isText()) {
                            val resp = it.string()
                            Log.i(tag, "response body's content: $resp")
                            return response.newBuilder()
                                .body(resp.toResponseBody(_mediaType))
                                .build()
                        } else {
                            Log.i(tag, "response body's content maybe [file part], too large to print, ignored!")
                        }
                    }
                }
            }
            Log.i(tag, "=================== response's log end ===================")
        } catch (t: Throwable) { }

        return response
    }

    private fun logForRequest(request: Request) {
        try {
            val url = request.url.toString()
            val headers = request.headers
            Log.i(tag, "=================== request log ===================")
            Log.i(tag, "method: ${request.method}")
            Log.i(tag, "url: $url")
            if (headers.size > 0) {
                Log.i(tag, "headers: $headers")
            }
            request.body?.let {
                it.contentType()?.let { _mediaType ->
                    Log.i(tag, "request body's content type: $_mediaType")
                    if (_mediaType.isText()) {
                        Log.i(tag, "request body's content: ${bodyToString(request)}")
                    } else {
                        Log.i(tag, "request body's content: maybe [file part] too large to print, ignored!")
                    }
                }
            }
            Log.i(tag, "=================== request log end ===================")
        } catch (t: Throwable) {}
    }

    private fun bodyToString(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body?.writeTo(buffer)
            buffer.readUtf8()
        } catch (t: Throwable) {
            "something error when show request body."
        }
    }
}