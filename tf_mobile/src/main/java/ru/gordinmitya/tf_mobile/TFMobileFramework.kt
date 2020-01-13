package ru.gordinmitya.tf_mobile

import android.content.Context
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.InferenceFramework
import ru.gordinmitya.common.InferenceType
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.Classifier

object TFMobileFramework : InferenceFramework("TFMobile", "by Google (Deprecated)") {
    val types = listOf(TF_MOBILE_CPU)

    override val inferenceTypes: List<InferenceType>
        get() = types
    override val models: List<Model>
        get() = ConvertedModel.all.map { it.model }

    override fun createClassifier(context: Context, configuration: Configuration): Classifier {
        val convertedModel = ConvertedModel.getByModel(configuration.model)
            ?: throw IllegalArgumentException("not supported model")
        val inferenceType = configuration.inferenceType as? TFMobileInfereceType
            ?: throw IllegalArgumentException("not supported inference type")

        return TFMobileClassifier(context, configuration, convertedModel, inferenceType)
    }
}