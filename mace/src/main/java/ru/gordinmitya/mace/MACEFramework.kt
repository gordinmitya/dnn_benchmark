package ru.gordinmitya.mace

import android.content.Context
import ru.gordinmitya.common.*
import ru.gordinmitya.common.classification.ClassificationFramework
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.Classifier

class MACEFramework : InferenceFramework("MACE", "by Xiaomi"), ClassificationFramework {
    private val TYPES = arrayListOf(
        CPU,
        OPEN_CL
//        HEXAGON
    )

    override fun getInferenceTypes(): List<InferenceType> = TYPES

    override fun getModels(): List<Model> =
        ConvertedModel.all.map { it.model }.toList()

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? MACEInferenceType
            ?: throw IllegalArgumentException("not supported inference type")

        return MACEClassifier(context, configuration, convertedModel, inferenceType)
    }
}