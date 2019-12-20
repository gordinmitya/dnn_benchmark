package ru.gordinmitya.ncnn

import android.content.Context
import ru.gordinmitya.common.*

object NCNNFramework : InferenceFramework("NCNN", "by Tencent") {

    val types = arrayListOf(
        NCNN_CPU,
        NCNN_VULKAN
    )

    override val inferenceTypes: List<InferenceType>
        get() = types
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
        val inferenceType = inferenceType as? NCNNInfereceType
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