package com.justin.qingshan.httputils.callback

import okhttp3.Response
import java.io.File

/**
 * Callback of file downloading.
 *
 * the response will be parsed as stream and store in [destFileName]
 *
 * @param destFileDir destination file dir to store
 * @param destFileName file name to store
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
abstract class FileCallback(
    private val destFileDir: String,
    private val destFileName: String
): Callback<File>() {

    override fun parseNetworkResp(response: Response, id: Int): File {
        return saveFile(response, id)
    }

    private fun saveFile(response: Response, id: Int): File {
        val dir = File(destFileDir)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, destFileName)
        val total = response.body?.contentLength() ?: 0L
        if (total == 0L) {
            onError(RuntimeException("not file downloaded"), id)
            return file
        }

        val ins = response.body?.byteStream()
        ins?.use { _ins ->
            val fos = file.outputStream()

            fos.use { _fos ->
                var length: Int
                var finalSum = 0L
                val buf = ByteArray(2048)
                while(true) {
                    length = _ins.read(buf)
                    if (length == -1) {
                        break
                    }
                    finalSum += length
                    fos.write(buf, 0, length)
                    inProgress(finalSum * 1.0f / total, total, id)
                }

                _fos.flush()
            }
        }
        return file
    }
}