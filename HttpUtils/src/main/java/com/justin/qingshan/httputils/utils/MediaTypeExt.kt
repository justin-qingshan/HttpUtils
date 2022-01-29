package com.justin.qingshan.httputils.utils

import okhttp3.MediaType

/**
 * To judge a media type is text or not.
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
fun MediaType.isText(): Boolean {
    if (type == "text") {
        return true
    }

    return subtype == "json"
            || subtype == "xml"
            || subtype == "html"
            || subtype == "webviewhtml"
}