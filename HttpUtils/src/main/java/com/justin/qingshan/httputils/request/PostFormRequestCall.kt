package com.justin.qingshan.httputils.request

import com.justin.qingshan.httputils.callback.Callback
import com.justin.qingshan.httputils.request.CountingRequestBody
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URLConnection
import java.net.URLEncoder

/**
 * Request call to post form
 *
 * [Callback.inProgress] can be override to detect the upload progress
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
class PostFormRequestCall<T>: RequestCall<T>() {

    private val files: MutableList<FileInput> = mutableListOf()

    fun files(files: () -> Pair<String, Map<String, File>>) = apply {
        val key = files().first
        val fileMap = files().second
        fileMap.forEach {
            this.files.add(FileInput(key, it.key, it.value))
        }
    }

    fun addFile(file: () -> Triple<String, String, File>) = apply {
        val (key, fileName, currentFile) = file()
        this.files.add(FileInput(key, fileName, currentFile))
    }

    override fun buildRequestBody(): RequestBody {
        return if (files.isEmpty()) {
            val formBuilder = FormBody.Builder()
            if (params.isNotEmpty()) {
                params.entries.forEach {
                    formBuilder.add(it.key, it.value)
                }
            }
            formBuilder.build()
        } else {
            val multipartBuilder = MultipartBody.Builder()
            params.entries.forEach {
                multipartBuilder.addPart(
                    Headers.headersOf("Content-Disposition", "form-data; name=\"${it.key}\""),
                    it.value.toRequestBody()
                )
            }
            files.forEach {
                multipartBuilder.addFormDataPart(
                    it.key,
                    it.fileName,
                    it.file.asRequestBody(guessMimeType(it.fileName).toMediaType())
                )
            }
            multipartBuilder.build()
        }
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
            throw IllegalStateException("request body not found when post form")
        } else {
            requestBuilder.post(requestBody).build()
        }
    }

    private fun guessMimeType(path: String): String {
        val fileNameMap = URLConnection.getFileNameMap()
        return fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"))
            ?: "application/octet-stream"
    }
}

internal data class FileInput(
    val key: String,
    val fileName: String,
    val file: File
)