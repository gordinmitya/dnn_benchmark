package ru.gordinmitya.dnnbenchmark.segmentation

import android.graphics.Bitmap
import android.graphics.Color
import android.util.SparseArray
import android.util.SparseIntArray
import androidx.core.util.getOrDefault
import ru.gordinmitya.dnnbenchmark.benchmark.PrecisionResult
import ru.gordinmitya.dnnbenchmark.benchmark.ResultEvaluator

class SegmentationEvaluator : ResultEvaluator {
    private var errorsSum = 0.0
    private var count = 0

    fun addNext(predictions: Bitmap, gt: Bitmap) {
        val value = uoi(predictions, gt)

        errorsSum += value
        count += 1
    }

    private fun difference(a: Bitmap, b: Bitmap): Bitmap {
        val diff = Bitmap.createBitmap(a.width, a.height, a.config)
        for (x in 0 until a.width)
            for (y in 0 until a.height)
                if (a.getPixel(x, y) != b.getPixel(x, y))
                    diff.setPixel(x, y, Color.RED)
        return diff
    }

    private fun uoi(prediction: Bitmap, gt: Bitmap): Double {
        require(prediction.width == gt.width && prediction.height == gt.height)

        val keys = HashSet<Int>()
        val first = SparseIntArray()
        val second = SparseIntArray()
        val common = SparseIntArray()
        for (x in 0 until gt.width) {
            for (y in 0 until gt.height) {
                val a = prediction.getPixel(x, y)
                first.inc(a)
                val b = gt.getPixel(x, y)
                second.inc(b)
                if (a == b)
                    common.inc(a)
                keys.add(a)
                keys.add(b)
            }
        }

        val scores = SparseArray<Double>()
        var total = 0.0
        for (key in keys) {
            val intersection = common.getOrDefault(key, 0)
            val union = first.getOrDefault(key, 0) + second.getOrDefault(key, 0) - intersection
            val score = 1.0 * intersection / union
            scores.put(key, score)
            total += score
        }

        return total / keys.size
    }

    override fun summarize(): PrecisionResult {
        return SegmentationPrecisionResult(errorsSum / count)
    }

    fun SparseIntArray.inc(key: Int) {
        this.put(key, this.getOrDefault(key, 0) + 1)
    }
}