package ru.gordinmitya.dnnbenchmark.model

import ru.gordinmitya.common.FailureResult
import ru.gordinmitya.common.InferenceResult
import ru.gordinmitya.common.SuccessResult
import ru.gordinmitya.common.classification.ClassificationPrecisionResult

class InferenceResultEntity private constructor() {
    companion object {
        fun create(it: InferenceResult): Any {
            val node: HashMap<String, Any> = hashMapOf(
                "configuration" to ConfigurationEntity(it.configuration)
            )
            if (it is SuccessResult) {
                node["benchmarkResult"] = it.benchmarkResult
                if (it.precisionResult is ClassificationPrecisionResult) {
                    val res = it.precisionResult as ClassificationPrecisionResult
                    node["precisionResult"] = mapOf(
                        "errors" to res.errors
                    )
                }
            } else {
                node["error"] = (it as FailureResult).message
            }
            return node
        }
    }
}