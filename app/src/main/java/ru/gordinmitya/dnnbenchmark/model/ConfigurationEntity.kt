package ru.gordinmitya.dnnbenchmark.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.dnnbenchmark.App

@Parcelize
class ConfigurationEntity(
    val framework: String,
    val inferenceType: String,
    val isSupported: Boolean,
    val model: String
) : Parcelable {

    constructor(configuration: Configuration) : this(
        framework = configuration.inferenceFramework.name,
        inferenceType = configuration.inferenceType.name,
        isSupported = configuration.inferenceType.isSupported,
        model = configuration.model.name
    )

    fun toConfiguration(): Configuration {
        val framework = App.instance.frameworks.first {
            it.name == framework
        }
        val type = framework.getInferenceTypes().first {
            it.name == inferenceType
        }
        val model = App.instance.models.first {
            it.name == model
        }
        return Configuration(framework, type, model)
    }
}