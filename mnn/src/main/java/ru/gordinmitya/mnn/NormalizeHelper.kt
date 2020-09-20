package ru.gordinmitya.mnn

object NormalizeHelper {
    fun convertMean(means: FloatArray) = means.map { it * 255f }.toFloatArray()
    fun convertStd2Normal(std: FloatArray) = std.map { 1 / 255f / it }.toFloatArray()
}