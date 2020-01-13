package ru.gordinmitya.common

sealed class InferenceResult(val configuration: Configuration) {
    override fun toString(): String = configuration.run {
        return "${inferenceFramework.name} ${inferenceType.name}"
    }
}

class SuccessResult(
    configuration: Configuration,
    val benchmarkResult: BenchmarkResult,
    val precisionResult: PrecisionResult
) : InferenceResult(configuration) {
    override fun toString(): String {
        return "${super.toString()} $benchmarkResult $precisionResult"
    }
}

class FailureResult(
    configuration: Configuration,
    val message: String
) : InferenceResult(configuration) {
    override fun toString(): String {
        return "${super.toString()} FAILED $message"
    }
}