package ru.gordinmitya.dnnbenchmark.benchmark

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.gordinmitya.dnnbenchmark.model.ConfigurationEntity

sealed class InferenceResult : Parcelable {
    abstract val configuration: ConfigurationEntity

    override fun toString(): String = configuration.run {
        return "$framework $inferenceType"
    }
}

@Parcelize
class NotSupportedResult(
    override val configuration: ConfigurationEntity
) : InferenceResult() {
    override fun toString(): String {
        return "${super.toString()} RUNTIME NOT SUPPORTED"
    }
}

@Parcelize
class SuccessResult(
    override val configuration: ConfigurationEntity,
    val benchmarkResult: BenchmarkResult,
    val precisionResult: PrecisionResult
) : InferenceResult() {
    override fun toString(): String {
        return "${super.toString()} $benchmarkResult $precisionResult"
    }
}

@Parcelize
class FailureResult(
    override val configuration: ConfigurationEntity,
    val message: String,
    val error: Boolean = true
) : InferenceResult() {
    override fun toString(): String {
        return "${super.toString()} FAILED $message"
    }
}