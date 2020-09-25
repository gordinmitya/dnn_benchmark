package ru.gordinmitya.dnnbenchmark.classification

import kotlinx.android.parcel.Parcelize
import ru.gordinmitya.dnnbenchmark.benchmark.PrecisionResult

@Parcelize
class ClassificationPrecisionResult(
    val errorClass: Float,
    val l1_avg: Float,
    val max: Float,
) : PrecisionResult() {
    override fun toString(): String {
        return "cls ${errorClass.compact()}%, l1 ${l1_avg.compact()}, max ${max.compact()}"
    }
    private fun Float.compact(): String = "%.1f".format(this * 100)
}