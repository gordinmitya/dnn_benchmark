package ru.gordinmitya.dnnbenchmark.segmentation

import android.graphics.Bitmap
import ru.gordinmitya.dnnbenchmark.benchmark.PrecisionResult
import ru.gordinmitya.dnnbenchmark.benchmark.ResultEvaluator

class SegmentationEvaluator : ResultEvaluator {

    fun addNext(predictions: Bitmap) {
    }

    override fun summarize(): PrecisionResult {
        return SegmentationPrecisionResult()
    }
}