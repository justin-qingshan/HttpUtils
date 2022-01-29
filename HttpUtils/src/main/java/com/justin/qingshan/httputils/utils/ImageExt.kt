package com.justin.qingshan.httputils.utils

import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import java.io.InputStream
import kotlin.math.max
import kotlin.math.roundToInt

fun InputStream.getImageSize(): ImageSize {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeStream(this, null, options)
    return ImageSize(options.outWidth, options.outHeight)
}

fun calcInSampleSize(srcSize: ImageSize, targetSize: ImageSize): Int {
    return if (srcSize.width > targetSize.width && srcSize.height > targetSize.height) {
        max(
            (srcSize.width.toFloat() / targetSize.width.toFloat()).roundToInt(),
            (srcSize.height.toFloat() / targetSize.height.toFloat()).roundToInt()
        )
    } else {
        1
    }
}

fun View.getImageViewSize(): ImageSize {
    return ImageSize(
        this.getExpectWidth(),
        this.getExpectHeight()
    )
}

fun View.getExpectHeight(): Int {
    val params = this.layoutParams
    var height = 0
    if (params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
        height = this.height
    }
    if (height <= 0 && params != null) {
        height = params.height
    }
    if (height <= 0) {
        height = getImageViewFieldValue(this, "mMaxHeight")
    }
    if (height <= 0) {
        height = this.context.resources.displayMetrics.heightPixels
    }
    return height
}

fun View.getExpectWidth(): Int {
    val params = this.layoutParams
    var width = 0
    if (params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
        width = this.width
    }
    if (width <= 0 && params != null) {
        width = params.width
    }
    if (width <= 0) {
        width = getImageViewFieldValue(this, "mMaxWidth")
    }
    if (width <= 0) {
        width = this.context.resources.displayMetrics.widthPixels
    }
    return width
}

fun getImageViewFieldValue(obj: Any, fieldName: String): Int {
    return try {
        val field = ImageView::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        val fieldValue = field.getInt(obj)
        if (fieldValue > 0 && fieldValue < Int.MAX_VALUE) fieldValue else 0
    } catch (e: Exception) {
        0
    }
}

data class ImageSize(
    val width: Int,
    val height: Int
)

