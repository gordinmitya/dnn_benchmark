package ru.gordinmitya.dnnbenchmark.model

import com.google.firebase.firestore.FieldValue
import ru.gordinmitya.common.FailureResult
import ru.gordinmitya.common.InferenceResult
import ru.gordinmitya.common.SuccessResult
import ru.gordinmitya.common.classification.ClassificationPrecisionResult
import ru.gordinmitya.dnnbenchmark.DeviceInfo

class Measurement private constructor() {
    companion object {
        fun create(device: DeviceInfo, results: List<InferenceResult>): Any =
            mapOf(
                "deviceInfo" to mapOf(
                    "uuid" to device.uuid,
                    "os" to device.os,
                    "model" to device.model,
                    "marketName" to device.marketName,
                    "marketName" to device.manufacturer
                ),
                "timestamp" to FieldValue.serverTimestamp(),
                "results" to results.map {
                    val node: HashMap<String, Any> = hashMapOf(
                        "configuration" to mapOf(
                            "framework" to it.configuration.inferenceFramework.name,
                            "inferenceType" to it.configuration.inferenceType.name,
                            "model" to it.configuration.model.name
                        )
                    )
                    if (it is SuccessResult) {
                        node["benchmarkResult"] = mapOf(
                            "min" to it.benchmarkResult.min,
                            "max" to it.benchmarkResult.max,
                            "avg" to it.benchmarkResult.avg,
                            "firstRun" to it.benchmarkResult.firstRun,
                            "preparation" to it.benchmarkResult.preparation,
                            "loops" to it.benchmarkResult.loops
                        )
                        if (it.precisionResult is ClassificationPrecisionResult) {
                            val res = it.precisionResult as ClassificationPrecisionResult
                            node["precisionResult"] = mapOf(
                                "errors" to res.errors
                            )
                        }
                    } else {
                        node["error"] = (it as FailureResult).message
                    }
                    return@map node
                }.toList()
            )
    }
}