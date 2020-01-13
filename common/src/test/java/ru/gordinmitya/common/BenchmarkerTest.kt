package ru.gordinmitya.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BenchmarkerTest {
    @Test
    fun creation_isCorrect() {
        val preparation = 0L
        val times = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

        val benchmarker = Benchmarker()
        benchmarker.addPreparation(preparation)
        times.forEach {
            benchmarker.addNext(it.toLong())
        }
        val result = benchmarker.summarize()

        assertEquals(9, result.loops)
        assertEquals(0L, result.preparation)
        assertEquals(1L, result.firstRun)
        assertEquals(1L, result.min)
        assertEquals(9L, result.max)
        assertEquals(5.0, result.avg)
    }
}
