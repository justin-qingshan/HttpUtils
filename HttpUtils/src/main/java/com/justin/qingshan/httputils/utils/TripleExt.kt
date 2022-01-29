package com.justin.qingshan.httputils.utils

/**
 * Syntactic sugar to create [Triple]
 *
 * @author justin.qingshan
 * @since  2022/1/28
 */
infix fun <A, B, C> Pair<A, B>.triple(that: C): Triple<A, B, C> {
    return Triple(this.first, this.second, that)
}