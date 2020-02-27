package ru.gordinmitya.dnnbenchmark.classification

import android.util.Log
import ru.gordinmitya.dnnbenchmark.benchmark.PrecisionResult
import ru.gordinmitya.dnnbenchmark.benchmark.ResultEvaluator

class ClassificationEvaluator :
    ResultEvaluator {
    private val errors = ArrayList<Pair<String, String>>()
    private var total = 0

    fun addNext(predictions: FloatArray, result: String, gt: GT) {
        total += 1
        if (gt.label != result)
            errors.add(gt.label to result)
        Log.d("ClassificationEvaluator", "$result vs ${gt.label}")
    }

    override fun summarize(): PrecisionResult {
        return ClassificationPrecisionResult(
            1.0 * errors.size / total
        )
    }
}