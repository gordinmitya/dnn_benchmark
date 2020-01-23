package ru.gordinmitya.common.classification

import android.util.Log
import ru.gordinmitya.common.PrecisionResult
import ru.gordinmitya.common.ResultEvaluator

class ClassificationEvaluator : ResultEvaluator {
    private val errors = ArrayList<Pair<String, String>>()
    private var total = 0

    fun addNext(predictions: FloatArray, result: String, gt: GT) {
        total += 1
        if (gt.label != result)
            errors.add(gt.label to result)
        Log.d("ClassificationEvaluator", "$result vs ${gt.label}")
    }

    override fun summarize(): PrecisionResult {
        return ClassificationPrecisionResult(1.0 * errors.size / total)
    }
}