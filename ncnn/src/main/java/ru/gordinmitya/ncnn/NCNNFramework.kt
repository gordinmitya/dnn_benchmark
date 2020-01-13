package ru.gordinmitya.ncnn

import android.content.Context
import ru.gordinmitya.common.*
import ru.gordinmitya.common.classification.Classifier

object NCNNFramework : InferenceFramework("NCNN", "by Tencent") {

    val types = arrayListOf(
        NCNN_CPU,
        NCNN_VULKAN
    )

    override val inferenceTypes: List<InferenceType>
        get() = types
    override val models: List<Model>
        get() = ConvertedModel.all.map { it.model }

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? NCNNInfereceType
            ?: throw IllegalArgumentException("not supported inference type")

        return NCNNClassifier(context, configuration, convertedModel, inferenceType)
    }
}