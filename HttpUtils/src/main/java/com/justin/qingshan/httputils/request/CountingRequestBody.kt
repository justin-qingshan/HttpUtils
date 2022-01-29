package com.justin.qingshan.httputils.request

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*

/**
 * Implementation of [RequestBody] used to calculate upload progress.
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
class CountingRequestBody(
    private var delegate: RequestBody,
    private var listener: ((Long, Long) -> Unit)
): RequestBody() {

    private var countingSink: CountingSink? = null

    override fun contentType(): MediaType? {
        return delegate.contentType()
    }

    override fun contentLength(): Long {
        return try {
            delegate.contentLength()
        } catch (e: Exception) {
            -1
        }
    }

    override fun writeTo(sink: BufferedSink) {
        countingSink = CountingSink(sink).also {
            val bufferedSink = it.buffer()
            delegate.writeTo(bufferedSink)
            bufferedSink.flush()
        }
    }

    inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {

        private var written = 0L

        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            written += byteCount
            listener(written, contentLength())
        }
    }
}

