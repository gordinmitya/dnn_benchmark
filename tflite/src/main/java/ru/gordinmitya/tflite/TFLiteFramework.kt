package ru.gordinmitya.tflite

import android.content.Context
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.InferenceFramework
import ru.gordinmitya.common.InferenceType
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.Classifier

object TFLiteFramework : InferenceFramework("TFLite", "by Google") {
    private val TYPES = listOf(
        TFLITE_CPU,
        TFLITE_OPENGL,
        TFLITE_NNAPI
    )

    override fun getInferenceTypes(): List<InferenceType> = TYPES

    override fun getModels(): List<Model> =
        ConvertedModel.all.map { it.model }.toList()

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? TFLiteInferenceType
            ?: throw IllegalArgumentException("not supported inference type")

        return TFLiteClassifier(context, configuration, convertedModel, inferenceType)
    }
}