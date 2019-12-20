package ru.gordinmitya.mnn

import android.content.Context
import ru.gordinmitya.common.*

object MNNFramework : InferenceFramework("MNN", "by Alibaba") {
    private val TYPES = arrayListOf(
        CPU,
        OPEN_CL,
        VULKAN,
        OPEN_GL
    )
    override val inferenceTypes: List<InferenceType>
        get() = TYPES

    override val models: List<Model>
        get() = ConvertedModel.all.map { it.model }.toList()

    override fun benchmark(
        context: Context,
        model: Model,
        inferenceType: InferenceType,
        loops: Int
    ): InferenceResult {
        val convertedModel = ConvertedModel.getByModel(model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = inferenceType as? MNNInferenceType
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