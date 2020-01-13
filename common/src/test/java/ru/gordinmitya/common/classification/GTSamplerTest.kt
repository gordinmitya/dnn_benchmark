package ru.gordinmitya.common.classification

import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GTSamplerTest {
    val samples = listOf<GT>(mockk(), mockk())

    @Test
    fun throws_withEmpty() {
        assertThrows(RuntimeException::class.java) {
            GTSampler(emptyList())
        }
    }

    @Test
    fun next_alwaysReturns() {
        val sampler = GTSampler(samples)
        for (i in 0..samples.size * 2)
            assertNotNull(sampler.next())
    }

    @Test
    fun hasNext_alwaysTrue() {
        val sampler = GTSampler(samples)
        for (i in 0..samples.size * 2)
            assertEquals(true, sampler.hasNext())
    }
}