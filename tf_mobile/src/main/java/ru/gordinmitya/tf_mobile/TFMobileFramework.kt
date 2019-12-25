package ru.gordinmitya.tf_mobile

import android.content.Context
import ru.gordinmitya.common.*

object TFMobileFramework : InferenceFramework("TFMobile", "by Google (Deprecated)") {
    val types = arrayListOf(TF_MOBILE_CPU)

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
        val inferenceType = inferenceType as? TFMobileInfereceType
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