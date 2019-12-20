package ru.gordinmitya.common

import java.util.concurrent.TimeUnit

object Timeit {
    fun measure(code: ()->Unit): Long {
        val start = System.nanoTime()
        code()
        val end = System.nanoTime()
        return TimeUnit.MILLISECONDS.convert(end - start, TimeUnit.NANOSECONDS)
    }
}