package com.justin.qingshan.httputils

import com.justin.qingshan.httputils.callback.Callback
import com.justin.qingshan.httputils.request.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response

/**
 * Utils of ok http
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
class OkHttpUtils(val client: OkHttpClient) {

    companion object {
        const val DEFAULT_MILLS = 10_000L

        private var INSTANCE: OkHttpUtils? = null

        fun initClient(client: OkHttpClient?): OkHttpUtils {
            if (INSTANCE == null) {
                synchronized(OkHttpUtils::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = OkHttpUtils(client ?: OkHttpClient())
                    }
                }
            }
            return INSTANCE!!
        }

        fun getInstance(): OkHttpUtils {
            return initClient(null)
        }
    }

    /**
     * To cancel specific request call.
     *
     * To use this ability, [RequestCall.tag] should be called when launch http request
     *
     * @param tag the tag used to find request to cancel
     */
    fun cancel(tag: Any) {
        client.dispatcher.queuedCalls()
            .filter { tag == it.request().tag() }
            .forEach { it.cancel() }

        client.dispatcher.runningCalls()
            .filter { tag == it.request().tag() }
            .forEach { it.cancel() }
    }
}

/**
 * Async GET
 *
 * [callback] will be invoked in request thread (not in main thread).
 */
fun <T> get(callback: Callback<T>, requestCall: GetRequestCall<T>.() -> Unit) {
    GetRequestCall<T>().apply(requestCall).callback(callback).execute()
}

/**
 * Sync GET
 *
 * Invoked in current thread. So you should not invoke this method in main thread.
 */
fun get(requestCall: GetRequestCall<Any>.() -> Unit): Response {
    return GetRequestCall<Any>().apply(requestCall).exec()
}

/**
 * Async POST
 *
 * [callback] will be invoked in request thread (not in main thread).
 */
fun <T> post(callback: Callback<T>, requestCall: PostRequestCall<T>.() -> Unit) {
    PostRequestCall<T>().apply(requestCall).callback(callback).execute()
}

/**
 * Sync POST
 *
 * Invoked in current thread. So you should not invoke this method in main thread.
 */
fun post(requestCall: PostRequestCall<Any>.() -> Unit): Response {
    return PostRequestCall<Any>().apply(requestCall).exec()
}

/**
 * Async POST file
 *
 * [callback] will be invoked in request thread (not in main thread).
 */
fun <T> postFile(callback: Callback<T>, requestCall: PostFileRequestCall<T>.() -> Unit) {
    PostFileRequestCall<T>().apply(requestCall).callback(callback).execute()
}

/**
 * Sync POST file
 *
 * Invoked in current thread. So you should not invoke this method in main thread.
 */
fun postFile(requestCall: PostFileRequestCall<Any>.() -> Unit): Response {
    return PostFileRequestCall<Any>().apply(requestCall).exec()
}

/**
 * Async POST form
 *
 * [callback] will be invoked in request thread (not in main thread).
 */
fun <T> postForm(callback: Callback<T>, requestCall: PostFormRequestCall<T>.() -> Unit) {
    PostFormRequestCall<T>().apply(requestCall).callback(callback).execute()
}

/**
 * Sync POST form
 *
 * Invoked in current thread. So you should not invoke this method in main thread.
 */
fun postForm(requestCall: PostFormRequestCall<Any>.() -> Unit): Response {
    return PostFormRequestCall<Any>().apply(requestCall).exec()
}

/**
 * Async PUT
 *
 * [callback] will be invoked in request thread (not in main thread).
 */
fun <T> put(callback: Callback<T>, requestCall: OtherRequestCall<T>.() -> Unit) {
    return OtherRequestCall<T>(METHOD_PUT).apply(requestCall).callback(callback).execute()
}

/**
 * Async HEAD
 *
 * [callback] will be invoked in request thread (not in main thread).
 */
fun <T> head(callback: Callback<T>, requestCall: OtherRequestCall<T>.() -> Unit) {
    return OtherRequestCall<T>(METHOD_HEAD).apply(requestCall).callback(callback).execute()
}

/**
 * Async DELETE
 *
 * [callback] will be invoked in request thread (not in main thread).
 */
fun <T> delete(callback: Callback<T>, requestCall: OtherRequestCall<T>.() -> Unit) {
    return OtherRequestCall<T>(METHOD_DELETE).apply(requestCall).callback(callback).execute()
}

/**
 * Async PATCH
 *
 * [callback] will be invoked in request thread (not in main thread).
 */
fun <T> patch(callback: Callback<T>, requestCall: OtherRequestCall<T>.() -> Unit) {
    return OtherRequestCall<T>(METHOD_PATCH).apply(requestCall).callback(callback).execute()
}

/**
 * SYNC PUT
 *
 * Invoked in current thread. So you should not invoke this method in main thread.
 */
fun put(builder: OtherRequestCall<Any>.() -> Unit): Response {
    return OtherRequestCall<Any>(METHOD_PUT).apply(builder).exec()
}

/**
 * SYNC HEAD
 *
 * Invoked in current thread. So you should not invoke this method in main thread.
 */
fun head(builder: OtherRequestCall<Any>.() -> Unit): Response {
    return OtherRequestCall<Any>(METHOD_HEAD).apply(builder).exec()
}

/**
 * SYNC DELETE
 *
 * Invoked in current thread. So you should not invoke this method in main thread.
 */
fun delete(builder: OtherRequestCall<Any>.() -> Unit): Response {
    return OtherRequestCall<Any>(METHOD_DELETE).apply(builder).exec()
}

/**
 * SYNC PATCH
 *
 * Invoked in current thread. So you should not invoke this method in main thread.
 */
fun patch(builder: OtherRequestCall<Any>.() -> Unit): Response {
    return OtherRequestCall<Any>(METHOD_PATCH).apply(builder).exec()
}

/**
 * Media type of plain text.
 */
val MEDIA_TYPE_PLAIN = "text/plain;charset=utf-8".toMediaType()

/**
 * Media type of stream
 */
val MEDIA_TYPE_STREAM = "application/octet-stream".toMediaType()

/**
 * Media type of json
 */
val MEDIA_TYPE_JSON = "application/json".toMediaType()


internal const val METHOD_HEAD = "HEAD"
internal const val METHOD_DELETE = "DELETE"
internal const val METHOD_PUT = "PUT"
internal const val METHOD_PATCH = "PATCH"