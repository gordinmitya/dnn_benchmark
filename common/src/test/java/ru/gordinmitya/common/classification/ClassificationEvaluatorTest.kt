package ru.gordinmitya.common.classification

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ClassificationEvaluatorTest {

    @Test
    fun summarize() {
        val gtPrediction = floatArrayOf(0f, .4f, 1.2f)
        val gt = GT(mockk(), gtPrediction)
        val evaluator = ClassificationEvaluator()

        evaluator.addNext(floatArrayOf(0f, .5f, 1.2f), gt)
        evaluator.addNext(floatArrayOf(0f, .4f, .8f), gt)
        val result = evaluator.summarize() as ClassificationPrecisionResult

        assertEquals(1.2f - .8f, result.diff)
    }
}