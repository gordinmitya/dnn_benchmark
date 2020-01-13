package ru.gordinmitya.common.classification

import ru.gordinmitya.common.PrecisionResult
import ru.gordinmitya.common.ResultEvaluator
import kotlin.math.abs
import kotlin.math.max

class ClassificationEvaluator : ResultEvaluator {
    private var maxDiff = 0f

    fun addNext(predictions: FloatArray, gt: GT) {
        require(predictions.size == gt.probabilities.size)
        for (i in predictions.indices) {
            val diff = abs(predictions[i] - gt.probabilities[i])
            maxDiff = max(maxDiff, diff)
        }
    }

    override fun summarize(): PrecisionResult {
        return ClassificationPrecisionResult(maxDiff)
    }
}