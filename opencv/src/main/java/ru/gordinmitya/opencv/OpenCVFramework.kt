package ru.gordinmitya.opencv

import android.content.Context
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.InferenceFramework
import ru.gordinmitya.common.InferenceType
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.Classifier

object OpenCVFramework : InferenceFramework("OpenCV DNN", "by OpenCV") {
    private val types = listOf(OPENCV_CPU)

    override val inferenceTypes: List<InferenceType>
        get() = types
    override val models: List<Model>
        get() = ConvertedModel.all.map { it.model }

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? OpenCVInfereceType
            ?: throw IllegalArgumentException("not supported inference type")

        return OpenCVClassifier(context, configuration, convertedModel, inferenceType)
    }
}