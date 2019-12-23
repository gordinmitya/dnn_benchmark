package ru.gordinmitya.tflite

import android.content.Context
import ru.gordinmitya.common.*

object TFLiteFramework : InferenceFramework("TFLite", "by Google") {
    private val TYPES = arrayListOf(
        TFLITE_CPU,
        TFLITE_OPENGL,
        TFLITE_NNAPI
    )

    override val inferenceTypes: List<InferenceType>
        get() = TYPES
    override val models: List<Model>
        get() = ConvertedModel.all.map { it.model }

    override fun benchmark(
        context: Context,
        model: Model,
        inferenceType: InferenceType,
        loops: Int
    ): InferenceResult {
        val convertedModel = ConvertedModel.getByModel(model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = inferenceType as? TFLiteInferenceType
            ?: throw IllegalArgumentException("not supported inference type")

        return try {
            Engine.benchmark(context, convertedModel, inferenceType, loops)
        } catch (e: RuntimeException) {
            FailureResult(
                this,
                inferenceType,
                model,
                e.message ?: ""
            )
        }
    }
}