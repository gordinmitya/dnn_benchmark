package ru.gordinmitya.dnnbenchmark.segmentation

import kotlinx.android.parcel.Parcelize
import ru.gordinmitya.dnnbenchmark.benchmark.PrecisionResult

@Parcelize
class SegmentationPrecisionResult(
    val avgIoU: Double
) : PrecisionResult() {

    // TODO compare segmentation maps
    override fun toString(): String {
        return "IoU=${"%.1f".format(avgIoU * 100)}%"
    }
}