package ru.gordinmitya.snpe

import android.app.Application
import android.content.Context
import ru.gordinmitya.common.*
import ru.gordinmitya.common.classification.ClassificationFramework
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.Classifier

class SNPEFramework(context: Context) : InferenceFramework("SNPE", "by Qualcomm"),
    ClassificationFramework {
    private val types: List<SNPEInferenceType>

    init {
        val app = context.applicationContext as Application
        types = listOf(
            SNPE_CPU(app),
            SNPE_GPU(app),
            SNPE_GPU16(app),
            SNPE_DSP(app)
//            SNPE_AIP(app)
        )
    }

    override fun getInferenceTypes(): List<InferenceType> = types

    override fun getModels(): List<Model> =
        ConvertedModel.all.map { it.model }.toList()

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? SNPEInferenceType
            ?: throw IllegalArgumentException("not supported inference type")

        return SNPEClassifier(context, configuration, convertedModel, inferenceType)
    }
}