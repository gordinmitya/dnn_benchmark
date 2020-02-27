package ru.gordinmitya.dnnbenchmark.model

import com.google.firebase.firestore.FieldValue
import ru.gordinmitya.dnnbenchmark.benchmark.InferenceResult

class Measurement(val device: DeviceInfo, val results: List<InferenceResult>) {
    val timestamp = FieldValue.serverTimestamp()
}