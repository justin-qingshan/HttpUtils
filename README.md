# HttpUtils

A kotlin implementation of [okhttp-utils](https://github.com/hongyangAndroid/okhttputils).

Simple tool to invoke [OkHttp](https://github.com/square/okhttp) easily to launch http request in Android.

# Usage

__Step1. Add Jetpack repository__

Add it in your root build.gradle at the end of repositories.

```groovy
repositories {
    //...
    maven { url 'https://jitpack.io' }
}
```

__Step2. Add the dependency__

```groovy
implementation 'com.squareup.okhttp3:okhttp:4.9.0'
implementation 'com.github.justin-qingshan:HttpUtils:0.0.1'
```

# Config OkHttpClient

A default config by OkHttp of `OkHttpClient` while be used.

You also can invoke `OkHttpUtils.initClient` to config `OkHttpClient` in `Application.onCreate`(
before any http request).

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val client = OkHttpClient.Builder()
            .connectTimeout(10_000L, TimeUnit.MILLISECONDS)
            .readTimeout(10_1000L, TimeUnit.MILLISECONDS)
            .build()
        OkHttpUtils.initClient(client)
    }
}
```

# Log

You can set a log interceptor to do log things when init `OkHttpClient` config.

`LogInterceptor` is provided in this project, you can use it like this example. You can also
implement `Interceptor` by yourself.

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val client = OkHttpClient.Builder()
            .addInterceptor(LoggerInterceptor("HttpUtilsSample", true))
            .build()
        OkHttpUtils.initClient(client)
    }
}
```

# Example

The examples are provided in the file `SampleViewModel` of app module.

## Get

A simple example to launch GET request.

```kotlin
get(object : StringCallback() {
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
```

## Post

```kotlin
post(object : StringCallback() {
    override fun onError(e: Exception, id: Int) {
        _error.postValue("post failed: ${e.message}")
    }

    override fun onResponse(response: String, id: Int) {
        _getResult.postValue("post success!")
    }
}) {
    url { "https://www.example.com" }
    content { User(1L, "justin").toJsonString() }
    mediaType { MEDIA_TYPE_JSON }
}
```

## Post file

Using `postFile` to send files to server.

```kotlin
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
    url { "https://www.example.com" }
    addParam { "id" to 1.toString() }
    file { File(path) }
    mediaType { "image/png".toMediaType() }
}
```

## Post file in form

```kotlin
postForm(object : ErrorCallback() {
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
```

## HEAD/PUT/PATCH/DELETE

A sample example to use `HEAD`/`PUT`/`PATCH`/`DELETE` method.

```kotlin
put(object : StringCallback() { // also can use head(), patch(), delete()
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
```

# Custom callback

## Callback of parsing entity

`GenericsCallback` is used to get custom entity after http request launched.

Need to override the method `transform(String)`.

```kotlin
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
```

## Download file

`FileCallback` is used to download file.

When using `FileCallback`, the parameters of filePath and fileName is required.

override `inProgress(Float, Long, Int)` method can be used to get download progress or upload
progress.

```kotlin
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
```

## Download image

`BitmapCallback` is used to receive image.

```kotlin
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
```

# Sync request

Every method mentioned before supports sync requesting. Here is a SYNC GET request:

```kotlin
val resp = get {
    url { "http://www.example.com/user" }
    addParam { "id" to "1" }
}
_getResult.postValue(resp.body?.string() ?: "")
```

Notice: this invoke will run in current thread.

# Cancel request

```kotlin
get(EmptyCallback()) {
    url { "http://www.example.com/user" }
    addParam { "id" to id }
    tag { "getUser" }
}
OkHttpUtils.getInstance().cancel("getUser")
```

# License

> Copyright (C) 2022 justin.qingshan@gmail.com
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
> http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.