package ru.gordinmitya.dnnbenchmark.benchmark

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class BenchmarkResult(
    // time in milliseconds
    val preparation: Long,
    val times: List<Long>,
) : Parcelable {
    override fun toString(): String {
        val mean = times.average()
        val variance = times.map { it * it }.average()
        val stdev = Math.sqrt(variance)
        return "avg=${"%.2f".format(mean)}ms sd=${"%.2f".format(stdev)}ms"
    }

    companion object {
        fun fromMeasurements(
            preparation: Long,
            times: List<Long>
        ): BenchmarkResult {
            return BenchmarkResult(
                preparation,
                times
            )
        }
    }
}
