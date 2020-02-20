package ru.gordinmitya.mnn

import android.content.Context
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.InferenceFramework
import ru.gordinmitya.common.InferenceType
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.Classifier

object MNNFramework : InferenceFramework("MNN", "by Alibaba") {
    private val TYPES = arrayListOf(
        CPU,
        OPEN_CL,
        VULKAN,
        OPEN_GL
    )
    override fun getInferenceTypes(): List<InferenceType> = TYPES

    override fun getModels(): List<Model> =
        ConvertedModel.all.map { it.model }.toList()

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? MNNInferenceType
            ?: throw IllegalArgumentException("not supported inference type")

        return MNNClassifier(context, configuration, convertedModel, inferenceType)
    }
}