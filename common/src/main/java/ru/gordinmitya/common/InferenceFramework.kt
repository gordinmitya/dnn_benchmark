package ru.gordinmitya.common

import android.content.Context
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.Classifier
import ru.gordinmitya.common.segmentation.SegmentationModel
import ru.gordinmitya.common.segmentation.Segmentator

abstract class InferenceFramework(
    val name: String,
    val description: String
) {
    abstract fun getModels(): List<Model>
    abstract fun getInferenceTypes(): List<InferenceType>
}
