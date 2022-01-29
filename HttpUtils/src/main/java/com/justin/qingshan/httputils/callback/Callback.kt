package com.justin.qingshan.httputils.callback

import okhttp3.Request
import okhttp3.Response
import java.lang.Exception

/**
 * Abstract class of all callback which will be invoked to handle http request.
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
abstract class Callback<T> {

    /**
     * Invoked before http request launched.
     *
     * @param request the request to be launched
     * @param id id of current http request
     */
    open fun onBefore(request: Request, id: Int) {

    }

    /**
     * Invoked after http response handled.
     *
     * @param id id of current request
     */
    open fun onAfter(id: Int) {

    }

    /**
     * Invoked during download or upload files.
     *
     * @param progress download/upload progress
     * @param total total length of download/upload content
     * @param id id of current request
     */
    open fun inProgress(progress: Float, total: Long, id: Int) {

    }

    /**
     * Invoked when validating [Response].
     *
     * @param response the [Response] of http request
     * @param id id of current request
     */
    open fun validateResp(response: Response, id: Int): Boolean {
        return response.isSuccessful
    }

    /**
     * Invoked when parsing [Response.body].
     *
     * @param response the [Response] of current request
     * @param id id of current request
     */
    abstract fun parseNetworkResp(response: Response, id: Int): T

    /**
     * Invoked when request/validate/parse failed
     *
     * @param e the [Exception] occurred during request/validate/parse
     * @param id id of current request
     */
    abstract fun onError(e: Exception, id: Int)

    /**
     * Callback of successful request
     *
     * @param response the result parsed of current request
     * @param id id of current request
     */
    abstract fun onResponse(response: T, id: Int)


}