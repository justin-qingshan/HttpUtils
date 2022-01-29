package com.justin.qingshan.httputils.utils

import java.util.*

/**
 * Transform [String] of hex to [ByteArray]
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
fun String.hexBytes(): ByteArray {
    val byteIterator = chunkedSequence(2)
        .map { it.toInt(16).toByte() }
        .iterator()
    return ByteArray(length / 2) { byteIterator.next() }
}

/**
 * Transform [ByteArray] to hex [String]
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
fun ByteArray.hexString() = this.joinToString {
    (it.toInt() and 0xFF).toString(16)
        .padStart(2, '0')
        .uppercase(Locale.getDefault())
}