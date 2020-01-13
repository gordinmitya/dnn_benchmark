package ru.gordinmitya.common

import android.content.Context
import ru.gordinmitya.common.classification.Classifier

abstract class InferenceFramework(
    val name: String,
    val description: String
) {
    abstract val models: List<Model>
    abstract val inferenceTypes: List<InferenceType>
    abstract fun createClassifier(context: Context, configuration: Configuration): Classifier
}