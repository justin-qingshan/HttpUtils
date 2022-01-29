package com.justin.qingshan.httputils.utils

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.InputStream

/**
 * @author justin.qingshan
 * @since  2022/1/28
 */
@JvmName("create")
fun InputStream.asRequestBody(length: Long, contentType: MediaType? = null): RequestBody {
    return object : RequestBody() {
        override fun contentType() = contentType

        override fun contentLength() = length

        override fun writeTo(sink: BufferedSink) {
            source().use { source -> sink.writeAll(source) }
        }
    }
}