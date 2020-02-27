package ru.gordinmitya.dnnbenchmark.classification

import kotlinx.android.parcel.Parcelize
import ru.gordinmitya.dnnbenchmark.benchmark.PrecisionResult

@Parcelize
class ClassificationPrecisionResult(
    val errors: Double
) : PrecisionResult() {
    override fun toString(): String {
        return "error ${errors * 100}%"
    }
}