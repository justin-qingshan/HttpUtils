package com.justin.qingshan.httputils.app.utils

import com.google.common.util.concurrent.ThreadFactoryBuilder
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author justin.qingshan
 * @since  2022/1/28
 */
private val threadPoolExecutor = ThreadPoolExecutor(
    5,
    5,
    0,
    TimeUnit.SECONDS,
    LinkedBlockingQueue(10),
    ThreadFactoryBuilder().setNameFormat("db-%d").build(),
    ThreadPoolExecutor.AbortPolicy()
)

fun runTask(action: () -> Unit) {
    threadPoolExecutor.submit { action() }
}