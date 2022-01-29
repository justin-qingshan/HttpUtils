package com.justin.qingshan.httputils.callback

import okhttp3.Response
import java.lang.Exception

/**
 * Empty callback after http request.
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
open class EmptyCallback: ErrorCallback() {
    override fun onError(e: Exception, id: Int) {

    }
}

/**
 * Abstract class of callback which focuses on [onError] after http request.
 *
 * Classes implement [ErrorCallback] need override [onError] method.
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
abstract class ErrorCallback: Callback<Any>() {
    override fun parseNetworkResp(response: Response, id: Int): Any {
        response.body?.close()
        return Any()
    }

    override fun onResponse(response: Any, id: Int) {

    }
}