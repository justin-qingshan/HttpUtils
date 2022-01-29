package com.justin.qingshan.httputils.request

import android.net.Uri
import com.justin.qingshan.httputils.OkHttpUtils
import com.justin.qingshan.httputils.callback.Callback
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Abstract Request call.
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
abstract class RequestCall<T> {

    internal lateinit var url: String
    internal var tag: Any? = null
    internal val params: MutableMap<String, String> = mutableMapOf()
    internal val headers: MutableMap<String, String> = mutableMapOf()
    internal var id: Int = -1
    internal var callback: Callback<T>? = null

    private var readTimeout = 0L
    private var writeTimeout = 0L
    private var connTimeout = 0L

    private lateinit var request: Request

    fun url(url: () -> String) = apply { this.url = url() }

    fun tag(tag: () -> Any) = apply { this.tag = tag() }

    fun params(params: () -> Map<String, String>) = apply { this.params.putAll(params()) }

    fun addParam(param: () -> Pair<String, String>) = apply {
        val (key, value) = param()
        this.params[key] = value
    }

    fun headers(headers: () -> Map<String, String>) = apply { this.headers.putAll(headers()) }

    fun addHeader(header: () -> Pair<String, String>) = apply {
        val (key, value) = header()
        this.headers[key] = value
    }

    fun id(id: () -> Int) = apply { this.id = id() }

    fun readTimeout(readTimeout: () -> Long) = apply { this.readTimeout = readTimeout() }

    fun writeTimeout(writeTimeout: () -> Long) = apply { this.writeTimeout = writeTimeout() }

    fun connTimeout(connTimeout: () -> Long) = apply { this.connTimeout = connTimeout() }

    internal fun callback(callback: Callback<T>) = apply { this.callback = callback }

    protected abstract fun buildRequestBody(): RequestBody?

    protected open fun wrapRequestBody(requestBody: RequestBody?, callback: Callback<T>?): RequestBody? {
        return requestBody
    }

    protected abstract fun buildRequest(requestBuilder: Request.Builder, requestBody: RequestBody?): Request

    internal fun execute() {
        val call = buildCall()
        callback?.let {
            it.onBefore(request, id)
            call.enqueue(object: okhttp3.Callback {
                override fun onFailure(call: Call, e: IOException) {
                    error(it, e, id)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (call.isCanceled()) {
                        error(it, IOException("canceled"), id)
                        return
                    }
                    if (!it.validateResp(response, id)) {
                        error(it, IOException("request failed ${response.code}"), id)
                        return
                    }
                    val result: T
                    try {
                        result = it.parseNetworkResp(response, id)
                        it.onResponse(result, id)
                        it.onAfter(id)
                    } catch (e: Exception) {
                        error(it, e, id)
                    }
                }
            })
        }
    }

    internal fun exec() = buildCall().execute()

    private fun buildCall(): Call {
        request = generateRequest(callback)

        val client: OkHttpClient = if (readTimeout > 0L || writeTimeout > 0L || connTimeout > 0L) {

            readTimeout = if (readTimeout > 0L) readTimeout else OkHttpUtils.DEFAULT_MILLS
            writeTimeout = if (writeTimeout > 0L) writeTimeout else OkHttpUtils.DEFAULT_MILLS
            connTimeout = if (connTimeout > 0L) connTimeout else OkHttpUtils.DEFAULT_MILLS

            OkHttpUtils.getInstance().client.newBuilder()
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .connectTimeout(connTimeout, TimeUnit.MILLISECONDS)
                .build()
        } else {
            OkHttpUtils.getInstance().client
        }

        return client.newCall(request)
    }

    private fun initRequestBuilder(): Request.Builder {
        if (params.isNotEmpty()) {
            val uriBuilder = Uri.parse(url).buildUpon()
            params.forEach { uriBuilder.appendQueryParameter(it.key, it.value) }
            url = uriBuilder.build().toString()
        }

        val builder = Request.Builder()
            .url(url)
            .tag(tag)

        if (headers.isNotEmpty()) {
            val headerBuilder = Headers.Builder()
            headers.entries.forEach {
                headerBuilder.add(it.key, it.value)
            }
            builder.headers(headerBuilder.build())
        }
        return builder
    }

    private fun generateRequest(callback: Callback<T>?): Request {
        val requestBody = buildRequestBody()
        val wrappedRequestBody = wrapRequestBody(requestBody, callback)
        return buildRequest(initRequestBuilder(), wrappedRequestBody)
    }

    private fun error(callback: Callback<T>, e: Exception, id: Int) {
        callback.onError(e, id)
        callback.onAfter(id)
    }
}