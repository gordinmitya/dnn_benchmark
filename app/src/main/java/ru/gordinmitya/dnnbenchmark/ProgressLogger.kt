package ru.gordinmitya.dnnbenchmark

import android.graphics.Bitmap
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.classification.ClassificationProgressCallback

class ProgressLogger(configuration: Configuration, val loggerDelegate: (String, Boolean) -> Unit) :
    ClassificationProgressCallback {
    val framework = configuration.inferenceFramework.name
    val type = configuration.inferenceType.name

    override fun onPrepared(prepareTime: Long) {
        loggerDelegate("$framework $type prepared ${prepareTime}ms", false)
    }

    var progress: String = ""
    override fun onNext(bitmap: Bitmap, loop: Int, total: Int) {
        progress = "$loop/$total"
    }

    override fun onResult(label: String, time: Long) {
        loggerDelegate("$progress ${time}ms $label", true)
    }
}