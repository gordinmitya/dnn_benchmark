package ru.gordinmitya.mnn

import android.content.Context
import ru.gordinmitya.common.*
import ru.gordinmitya.common.classification.ClassificationFramework
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.Classifier
import ru.gordinmitya.common.segmentation.SegmentationFramework
import ru.gordinmitya.common.segmentation.SegmentationModel
import ru.gordinmitya.common.segmentation.Segmentator

class MNNFramework : InferenceFramework("MNN", Version("?")), ClassificationFramework,
    SegmentationFramework {
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
        val convertedModel = ConvertedModel.getByModel<ClassificationModel>(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? MNNInferenceType
            ?: throw IllegalArgumentException("not supported inference type")

        return MNNClassifier(context, configuration, convertedModel, inferenceType)
    }

    override fun createSegmentator(context: Context, configuration: Configuration): Segmentator {
        val convertedModel = ConvertedModel.getByModel<SegmentationModel>(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? MNNInferenceType
            ?: throw IllegalArgumentException("not supported inference type")

        return MNNSegmentator(context, configuration, convertedModel, inferenceType)
    }

    override fun getDataOrder(): DataOrder = DataOrder.NCWH
}
