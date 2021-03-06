package ru.gordinmitya.common

abstract class Model(
    val name: String,
    val description: String,
    val mean: FloatArray,
    val std: FloatArray,
    val task: Task,
    val inputSize: Pair<Int, Int>,
    val inputChannels: Int,
    val outputShape: IntArray
)