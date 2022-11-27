package ru.gordinmitya.dnnbenchmark.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.dnnbenchmark.App
import java.util.*

@Parcelize
class ConfigurationEntity(
    val frameworkName: String,
    val frameworkVersion: String,
    val frameworkClassName: String,
    val inferenceType: String,
    val isSupported: Boolean,
    val task: String,
    val model: String
) : Parcelable {

    constructor(configuration: Configuration) : this(
        frameworkName = configuration.inferenceFramework.name,
        frameworkVersion = configuration.inferenceFramework.version.toString(),
        frameworkClassName = App.describeFramework(configuration.inferenceFramework.javaClass.kotlin),
        inferenceType = configuration.inferenceType.name,
        isSupported = configuration.inferenceType.isSupported,
        task = configuration.model.task.name.lowercase(Locale.ROOT),
        model = configuration.model.name
    )

    fun toConfiguration(): Configuration {
        val frameworkInstance = App.instance.createFrameworkInstance(frameworkClassName)
        val type = frameworkInstance.getInferenceTypes().first {
            it.name == inferenceType
        }
        val model = App.instance.models.first {
            it.name == model
        }
        return Configuration(frameworkInstance, type, model)
    }
}
