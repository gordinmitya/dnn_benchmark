package ru.gordinmitya.dnnbenchmark.benchmark

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class BenchmarkResult(
    // time in milliseconds
    val preparation: Long,
    val times: List<Long>,
) : Parcelable {
    override fun toString(): String {
        return "avg=${"%.2f".format(times.average())}ms min=${times.min()}ms max=${times.max()}ms"
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