package ru.gordinmitya.common

sealed class InferenceResult(
    val inferenceFramework: InferenceFramework,
    val inferenceType: InferenceType,
    val model: Model
) {
    override fun toString(): String {
        return "${model.name} ${inferenceFramework.name} ${inferenceType.name}"
    }
}

class SuccessResult(
    inferenceFramework: InferenceFramework,
    inferenceType: InferenceType,
    model: Model,

    val loops: Int,
    // time in milliseconds
    val min: Long,
    val max: Long,
    val avg: Double,

    val firstRun: Long,
    val preparation: Long
) : InferenceResult(inferenceFramework, inferenceType, model) {
    override fun toString(): String {
        return "${super.toString()} $min $max ${"%.2f".format(avg)}"
    }
}

class FailureResult(
    inferenceFramework: InferenceFramework,
    inferenceType: InferenceType,
    model: Model,

    val message: String
) : InferenceResult(inferenceFramework, inferenceType, model) {
    override fun toString(): String {
        return "${super.toString()} FAILED ${message}"
    }
}