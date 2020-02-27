package ru.gordinmitya.dnnbenchmark.classification

import android.graphics.Bitmap

interface ClassificationProgressCallback {
    fun onPrepared(prepareTime: Long)
    fun onNext(bitmap: Bitmap, loop: Int, total: Int)
    fun onResult(label: String, time: Long)
}