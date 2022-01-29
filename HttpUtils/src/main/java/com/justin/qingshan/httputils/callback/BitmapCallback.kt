package com.justin.qingshan.httputils.callback

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.Response
import java.lang.IllegalStateException

/**
 * Callback of [Bitmap] after http request.
 *
 * [Bitmap] will be used in [onResponse].
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
abstract class BitmapCallback: Callback<Bitmap>() {

    override fun parseNetworkResp(response: Response, id: Int): Bitmap {
        return response.body?.let {
            BitmapFactory.decodeStream(it.byteStream())
        } ?: throw IllegalStateException("no response body found")
    }
}