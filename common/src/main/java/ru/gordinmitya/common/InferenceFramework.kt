package ru.gordinmitya.common

import android.content.Context
import ru.gordinmitya.common.classification.Classifier

abstract class InferenceFramework(
    val name: String,
    val description: String
) {
    abstract fun getModels(): List<Model>
    abstract fun getInferenceTypes(): List<InferenceType>
    abstract fun createClassifier(context: Context, configuration: Configuration): Classifier
}