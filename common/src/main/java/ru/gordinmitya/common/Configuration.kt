package ru.gordinmitya.common

class Configuration(
    val inferenceFramework: InferenceFramework,
    val inferenceType: InferenceType,
    val model: Model
) {
    init {
        require(inferenceType in inferenceFramework.getInferenceTypes())
        require(model in inferenceFramework.getModels())
    }

    override fun toString(): String {
        return "${model.name} ${inferenceFramework.name} ${inferenceType.name}"
    }
}