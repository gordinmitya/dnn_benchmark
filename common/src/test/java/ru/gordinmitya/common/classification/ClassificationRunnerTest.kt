package ru.gordinmitya.common.classification

import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.gordinmitya.common.BenchmarkResult
import ru.gordinmitya.common.Benchmarker
import ru.gordinmitya.common.FailureResult
import ru.gordinmitya.common.SuccessResult
import ru.gordinmitya.common.utils.Timeit

internal class ClassificationRunnerTest {
    val LOOPS = 2

    @Test
    fun successResult() {
        val SPEED = 10L
        mockkObject(Timeit)
        every { Timeit.measure(any()) } returns SPEED

        val predictions = FloatArray(0)
        val classifier = mockk<Classifier>(relaxed = true)
        every { classifier.predict(any()) } returns predictions

        val gt = mockk<GT>()
        every { gt.probabilities } returns predictions
        val sampler = GTSampler(listOf(gt))

        val benchmarkResult = mockk<BenchmarkResult>()
        val benchmarker = mockk<Benchmarker>(relaxUnitFun = true)
        every { benchmarker.summarize() } returns benchmarkResult

        val precisionResult = mockk<ClassificationPrecisionResult>()
        val evaluator = mockk<ClassificationEvaluator>(relaxUnitFun = true)
        every { evaluator.summarize() } returns precisionResult

        val result = ClassificationRunner.benchmark(
            classifier,
            sampler,
            benchmarker,
            evaluator,
            LOOPS
        )

        assertTrue(result is SuccessResult)
        assertSame((result as SuccessResult).benchmarkResult, benchmarkResult)
        assertSame(result.precisionResult, precisionResult)

        verify(exactly = 1) { benchmarker.addPreparation(SPEED) }
        verify(exactly = LOOPS) { benchmarker.addNext(SPEED) }

        verify(exactly = LOOPS) { evaluator.addNext(predictions, gt) }

        unmockkAll()
    }

    @Test
    fun failureResult_whenException() {
        val MESSAGE = "SOME ERROR"

        val classifier = mockk<Classifier>(relaxed = true)
        every { classifier.predict(any()) } throws RuntimeException(MESSAGE)

        val result = ClassificationRunner.benchmark(
            classifier,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            LOOPS
        )

        assertTrue(result is FailureResult)
        assertEquals(MESSAGE, (result as FailureResult).message)
    }

    @Test
    fun callCallback() {
        val SPEED = 10L
        mockkObject(Timeit)
        every { Timeit.measure(any()) } returns SPEED
        val callback = mockk<ClassificationProgressCallback>(relaxed = true)

        val result = ClassificationRunner.benchmark(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            LOOPS,
            callback
        )

        assertTrue(result is SuccessResult)
        verify(exactly = 1) { callback.onPrepared(SPEED) }
        verify(exactly = LOOPS) { callback.onNext(any(), match { it in 0 until LOOPS }, LOOPS) }
        verify(exactly = LOOPS) { callback.onResult(any(), SPEED) }

        unmockkAll()
    }
}