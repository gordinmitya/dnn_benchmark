package ru.gordinmitya.opencv

import android.content.Context
import ru.gordinmitya.common.*
import ru.gordinmitya.common.classification.ClassificationFramework
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.Classifier

class OpenCVFramework : InferenceFramework("OpenCV DNN", "by OpenCV"), ClassificationFramework {
    private val TYPES = listOf(OPENCV_CPU)

    override fun getInferenceTypes(): List<InferenceType> = TYPES

    override fun getModels(): List<Model> =
        ConvertedModel.all.map { it.model }.toList()

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? OpenCVInfereceType
            ?: throw IllegalArgumentException("not supported inference type")

        return OpenCVClassifier(context, configuration, convertedModel, inferenceType)
    }
}