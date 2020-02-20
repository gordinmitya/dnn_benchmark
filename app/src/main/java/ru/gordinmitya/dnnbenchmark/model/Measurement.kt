package ru.gordinmitya.dnnbenchmark.model

import com.google.firebase.firestore.FieldValue
import ru.gordinmitya.common.FailureResult
import ru.gordinmitya.common.InferenceResult
import ru.gordinmitya.common.SuccessResult
import ru.gordinmitya.dnnbenchmark.DeviceInfo

class Measurement private constructor() {
    companion object {
        fun create(device: DeviceInfo, results: List<InferenceResult>): Any =
            mapOf(
                "deviceInfo" to device,
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
                        node["benchmarkResult"] = it.benchmarkResult
                        node["precisionResult"] = it.precisionResult
                    } else {
                        node["error"] = (it as FailureResult).message
                    }
                    return@map node
                }.toList()
            )
    }
}