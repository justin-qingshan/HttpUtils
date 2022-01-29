package com.justin.qingshan.httputils.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.justin.qingshan.httputils.*
import com.justin.qingshan.httputils.callback.*
import com.justin.qingshan.httputils.utils.runTask
import com.justin.qingshan.httputils.utils.toEntity
import com.justin.qingshan.httputils.utils.toJsonString
import com.justin.qingshan.httputils.utils.triple
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.lang.Exception

class SampleViewModel : ViewModel() {

    private val _getResult = MutableLiveData<String>()
    val getResult: LiveData<String> = _getResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _downloadProgress = MutableLiveData<Float>()
    var downloadProgress: LiveData<Float> = _downloadProgress

    private val _picMap = MutableLiveData<MutableMap<String, Bitmap>>()
    var picMap: LiveData<MutableMap<String, Bitmap>> = _picMap

    fun getSample() {
        get(object: StringCallback() {
            override fun onError(e: Exception, id: Int) {
                _error.postValue("search failed: ${e.message}")
            }

            override fun onResponse(response: String, id: Int) {
                _getResult.postValue("getSample success!")
            }
        }) {
            url { "https://github.com/search" }
            addParam { "q" to "HttpUtils" }
            addParam { "a" to "b" }
        }
    }

    fun postSample() {
        post(object: StringCallback() {
            override fun onError(e: Exception, id: Int) {
                _error.postValue("post failed: ${e.message}")
            }

            override fun onResponse(response: String, id: Int) {
                _getResult.postValue("post success!")
            }
        }) {
            url { "https://github.com" }
            content { User(1L, "justin").toJsonString() }
            mediaType { MEDIA_TYPE_JSON }
        }
    }

    fun postFormSample(filePath: String) {
        postForm(object: ErrorCallback() {
            override fun onError(e: Exception, id: Int) {
                _error.postValue("post form failed: ${e.message}")
            }

            override fun inProgress(progress: Float, total: Long, id: Int) {
                _downloadProgress.postValue(progress)
            }
        }) {
            url { "https://www.example.com" }
            addParam { "id" to "1" }
            addFile { "files" to "myFile" triple File(filePath) }
        }
    }

    fun deleteSample() {
        delete(object : StringCallback() {
            override fun onError(e: Exception, id: Int) {
                _error.postValue("delete failed: ${e.message}")
            }

            override fun onResponse(response: String, id: Int) {
                _getResult.postValue("delete success!")
            }
        }) {
            url { "https://www.example.com" }
            addParam { "id" to 1.toString() }
        }
    }

    fun postFileSample(file: File) {
        Log.d("", "postFileSample: ${file.path}")
        postFile(object : StringCallback() {
            override fun onError(e: Exception, id: Int) {
                _error.postValue("post file failed: ${e.message}")
            }

            override fun onResponse(response: String, id: Int) {
                _getResult.postValue("post file success!")
            }

            override fun inProgress(progress: Float, total: Long, id: Int) {
                _downloadProgress.postValue(progress)
            }
        }) {
            url { "https://github.com" }
            addParam { "id" to 1.toString() }
            file { file }
            mediaType { "image/png".toMediaType() }
        }
    }

    fun postFileSample(fd: ParcelFileDescriptor) {
        postFile(object : StringCallback() {
            override fun onError(e: Exception, id: Int) {
                _error.postValue("post file failed: ${e.message}")
            }

            override fun onResponse(response: String, id: Int) {
                _getResult.postValue("post file success!")
            }

            override fun inProgress(progress: Float, total: Long, id: Int) {
                Log.d("TAG", "inProgress: $progress")
                _downloadProgress.postValue(progress)
            }
        }) {
            url { "https://github.com" }
            addParam { "id" to 1.toString() }
            fd { fd }
            mediaType { "image/png".toMediaType() }
        }
    }

    fun headSample() {
        head(EmptyCallback()) {
            url { "https://github.com" }
        }
    }

    fun putSample() {
        put(object: StringCallback() {
            override fun onError(e: Exception, id: Int) {
                _error.postValue("post file failed: ${e.message}")
            }

            override fun onResponse(response: String, id: Int) {
                _getResult.postValue("post file success!")
            }
        }) {
            url { "https://www.example.com" }
            requestBody { User(1L, "justin").toJsonString().toRequestBody(MEDIA_TYPE_JSON) }
        }
    }

    fun downloadSample(context: Context) {
        get(object : FileCallback(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path,
            "a.apk"
        ) {
            override fun onError(e: Exception, id: Int) {
                _error.postValue("download file failed: ${e.message}")
            }

            override fun onResponse(response: File, id: Int) {
                _getResult.postValue("download file success: ${response.path}!")
            }

            override fun inProgress(progress: Float, total: Long, id: Int) {
                _downloadProgress.postValue(progress)
            }
        }) {
            url { "https://www.example.com/a.apk" }
            addParam { "id" to "1" }
        }
    }

    fun genericsSample() {
        get(object : GenericsCallback<User>() {
            override fun onError(e: Exception, id: Int) {
                _error.postValue("get user failed: ${e.message}")
            }

            override fun onResponse(response: User, id: Int) {
                _getResult.postValue("get user success: $response!")
            }

            override fun transform(str: String): User {
                return str.toEntity(User::class.java)
            }
        }) {
            url { "https://www.example.com" }
            addParam { "id" to 1.toString() }
        }
    }

    fun getImage() {
        val picId = "a"
        get(object : BitmapCallback() {
            override fun onError(e: Exception, id: Int) {
                _error.postValue("download image failed: ${e.message}")
            }

            override fun onResponse(response: Bitmap, id: Int) {
                val map = _picMap.value ?: mutableMapOf()
                map[picId] = response
                _picMap.postValue(map)
            }

        }) {
            url { "http://www.example.com" }
            addParam { "picId" to picId }
        }
    }

    fun syncGetSample() {
        runTask {
            val resp = get {
                url { "http://www.example.com/user" }
                addParam { "id" to "1" }
            }
            _getResult.postValue(resp.body?.string() ?: "")
        }
    }

    data class User(
        val id: Long,
        val name: String
    )
}