package com.justin.qingshan.httputils.request

import android.os.ParcelFileDescriptor
import com.justin.qingshan.httputils.MEDIA_TYPE_STREAM
import com.justin.qingshan.httputils.callback.Callback
import com.justin.qingshan.httputils.utils.asRequestBody
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.io.InputStream

/**
 * Request call to post file
 *
 * [Callback.inProgress] can be override to detect the upload progress
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
class PostFileRequestCall<T>: RequestCall<T>() {

    private var ins: InputStream? = null

    private var length: Long = -1L

    private var mediaType: MediaType = MEDIA_TYPE_STREAM

    fun file(file: () -> File) = apply {
        val currentFile = file()
        this.ins = currentFile.inputStream()
        this.length = currentFile.length()
    }

    fun fd(fd: () -> ParcelFileDescriptor) = apply {
        val currentFd = fd()
        this.ins = ParcelFileDescriptor.AutoCloseInputStream(currentFd)
        this.length = currentFd.statSize
    }

    fun mediaType(mediaType: () -> MediaType) = apply { this.mediaType = mediaType() }

    override fun buildRequestBody(): RequestBody? {
        return ins?.asRequestBody(length, mediaType)
    }

    override fun wrapRequestBody(requestBody: RequestBody?, callback: Callback<T>?): RequestBody? {
        return requestBody?.let {
            CountingRequestBody(it) { bytesWritten, contentLength ->
                callback?.inProgress(bytesWritten.toFloat() / contentLength, contentLength, id)
            }
        }
    }

    override fun buildRequest(requestBuilder: Request.Builder, requestBody: RequestBody?): Request {
        return if (requestBody == null) {
            throw IllegalStateException("no file set when use post file request")
        } else {
            requestBuilder.post(requestBody).build()
        }
    }
}