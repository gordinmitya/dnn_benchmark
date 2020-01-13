package ru.gordinmitya.common

class Configuration(
    val inferenceFramework: InferenceFramework,
    val inferenceType: InferenceType,
    val model: Model
) {
    init {
        require(inferenceType in inferenceFramework.inferenceTypes)
        require(model in inferenceFramework.models)
    }
}