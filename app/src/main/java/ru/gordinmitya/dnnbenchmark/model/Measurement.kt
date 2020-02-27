package ru.gordinmitya.dnnbenchmark.model

import com.google.firebase.firestore.FieldValue
import ru.gordinmitya.common.InferenceResult
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
                    InferenceResultEntity.create(it)
                }.toList()
            )
    }
}