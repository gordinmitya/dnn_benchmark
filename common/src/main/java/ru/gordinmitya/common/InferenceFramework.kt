package ru.gordinmitya.common

import android.content.Context

abstract class InferenceFramework(
    val name: String,
    val description: String
) {
    abstract val inferenceTypes: List<InferenceType>
    abstract val models: List<Model>

    abstract fun benchmark(
        context: Context,
        model: Model,
        inferenceType: InferenceType,
        loops: Int
    ): InferenceResult
}