package com.justin.qingshan.httputils

import android.app.Application
import com.justin.qingshan.httputils.log.LoggerInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        val client = OkHttpClient.Builder()
            .addInterceptor(LoggerInterceptor("HttpUtilsSample", true))
            .connectTimeout(10_000L, TimeUnit.MILLISECONDS)
            .readTimeout(10_1000L, TimeUnit.MILLISECONDS)
            .build()
        OkHttpUtils.initClient(client)
    }
}