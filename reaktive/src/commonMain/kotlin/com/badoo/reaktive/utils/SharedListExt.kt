package com.badoo.reaktive.utils

@Suppress("FunctionNaming")
internal inline fun <T> SharedList(size: Int, init: (Int) -> T): SharedList<T> =
    SharedList<T>(size).apply {
        repeat(size) { add(init(it)) }
    }