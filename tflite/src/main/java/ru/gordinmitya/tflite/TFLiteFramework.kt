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

    override val inferenceTypes: List<InferenceType>
        get() = TYPES
    override val models: List<Model>
        get() = ConvertedModel.all.map { it.model }

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? TFLiteInferenceType
            ?: throw IllegalArgumentException("not supported inference type")

        return TFMobileClassifier(context, configuration, convertedModel, inferenceType)
    }
}