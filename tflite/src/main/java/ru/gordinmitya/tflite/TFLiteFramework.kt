package ru.gordinmitya.tflite

import android.content.Context
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.InferenceFramework
import ru.gordinmitya.common.InferenceType
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.ClassificationFramework
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.Classifier
import ru.gordinmitya.common.segmentation.SegmentationFramework
import ru.gordinmitya.common.segmentation.SegmentationModel
import ru.gordinmitya.common.segmentation.Segmentator

class TFLiteFramework : InferenceFramework("TFLite", "by Google"),
    ClassificationFramework,
    SegmentationFramework {

    private val TYPES = listOf(
        TFLITE_CPU,
        TFLITE_OPENGL,
        TFLITE_NNAPI
    )

    override fun getInferenceTypes(): List<InferenceType> = TYPES

    override fun getModels(): List<Model> =
        ConvertedModel.all.map { it.model }.toList()

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel<ClassificationModel>(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? TFLiteInferenceType
            ?: throw IllegalArgumentException("not supported inference type")

        return TFLiteClassifier(context, configuration, convertedModel, inferenceType)
    }

    override fun createSegmentator(
        context: Context,
        configuration: Configuration
    ): Segmentator {
        val convertedModel = ConvertedModel.getByModel<SegmentationModel>(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? TFLiteInferenceType
            ?: throw IllegalArgumentException("not supported inference type")

        return TFLiteSegmentator(context, configuration, convertedModel, inferenceType)
    }
}