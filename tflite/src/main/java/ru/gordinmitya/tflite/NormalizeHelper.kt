package ru.gordinmitya.tflite

import org.tensorflow.lite.support.common.ops.NormalizeOp

object NormalizeHelper {
    fun convertMean(means: FloatArray) = means.map { it * 255f }.toFloatArray()
    fun convertStd(std: FloatArray) = std.map { it * 255 }.toFloatArray()
    fun toOp(means: FloatArray, std: FloatArray) = NormalizeOp(convertMean(means), convertStd(std))
}