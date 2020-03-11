package ru.gordinmitya.ncnn

import android.content.Context
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.InferenceFramework
import ru.gordinmitya.common.InferenceType
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.ClassificationFramework
import ru.gordinmitya.common.classification.Classifier

class NCNNFramework : InferenceFramework("NCNN", "by Tencent"), ClassificationFramework {

    val TYPES = arrayListOf(
        NCNN_CPU,
        NCNN_VULKAN
    )

    override fun getInferenceTypes(): List<InferenceType> = TYPES

    override fun getModels(): List<Model> =
        ConvertedModel.all.map { it.model }.toList()

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? NCNNInfereceType
            ?: throw IllegalArgumentException("not supported inference type")

        return NCNNClassifier(context, configuration, convertedModel, inferenceType)
    }
}