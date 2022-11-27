package ru.gordinmitya.dnnbenchmark.classification

import android.util.Log
import ru.gordinmitya.dnnbenchmark.benchmark.PrecisionResult
import ru.gordinmitya.dnnbenchmark.benchmark.ResultEvaluator
import kotlin.math.abs

class ClassificationEvaluator :
    ResultEvaluator {
    private var errors = 0
    private var total = 0
    private val losses = ArrayList<Loss>()

    fun addNext(predictions: FloatArray, result: String, gt: GT) {
        if (gt.label != result) {
            errors += 1
            Log.d("classification error", "$result should be ${gt.label}")
        }
        total += 1
        losses.add(compare(predictions, gt.probabilities))
    }

    override fun summarize(): PrecisionResult {
        return ClassificationPrecisionResult(
            1.0f * errors / total,
            losses.map { it.l1 }.average().toFloat(),
            losses.map { it.max }.max()
        )
    }

    private fun compare(actual: FloatArray, expected: FloatArray): Loss {
        require(actual.size == expected.size)
        var sum = 0.0f
        var max = 0.0f
        for (i in actual.indices) {
            val diff = abs(actual[i] - expected[i])
            sum += diff
            max = Math.max(diff, max)
        }
        return Loss(sum / actual.size, max)
    }

    private class Loss(
        val l1: Float,
        val max: Float
    )
}
