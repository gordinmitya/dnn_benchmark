package ru.gordinmitya.snpe

import android.content.Context
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.InferenceFramework
import ru.gordinmitya.common.InferenceType
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.Classifier

object SNPEFramework : InferenceFramework("SNPE", "by Qualcomm") {
    val types = listOf(
        SNPE_CPU,
        SNPE_GPU,
        SNPE_DSP,
        SNPE_GPU16
    )

    override val inferenceTypes: List<InferenceType>
        get() = types
    override val models: List<Model>
        get() = ConvertedModel.all.map { it.model }

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? SNPEInfereceType
            ?: throw IllegalArgumentException("not supported inference type")

        return SNPEClassifier(context, configuration, convertedModel, inferenceType)
    }
}