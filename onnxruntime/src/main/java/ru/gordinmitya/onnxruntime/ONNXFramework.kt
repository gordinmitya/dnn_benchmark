package ru.gordinmitya.onnxruntime

import android.content.Context
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.InferenceFramework
import ru.gordinmitya.common.InferenceType
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.ClassificationFramework
import ru.gordinmitya.common.classification.Classifier

class ONNXFramework : InferenceFramework("onnxruntime", "by Microsoft"), ClassificationFramework {
    private val types = arrayListOf(
        ONNX_CPU,
        ONNX_NNAPI
    )

    override fun getInferenceTypes(): List<InferenceType> = types

    override fun getModels(): List<Model> =
        ConvertedModel.all.map { it.model }.toList()

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? ONNXInfereceType
            ?: throw IllegalArgumentException("not supported inference type")

        return ONNXClassifier(context, configuration, convertedModel, inferenceType)
    }
}