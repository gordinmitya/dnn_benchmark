package ru.gordinmitya.common.classification

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.Task

open class ClassificationModel(
    name: String,
    description: String,
    inputSize: Pair<Int, Int>,
    inputChannels: Int,
    outputShape: IntArray,
    val labelsFile: String,
    val samplesDir: String
) : Model(name, description, Task.CLASSIFICATION, inputSize, inputChannels, outputShape) {
    override fun toString(): String = "$name (classification) $description"
}

object MobileNet_v2 : ClassificationModel(
    "mobilenet_v2",
    "made by Google",
    224 to 224,
    3,
    intArrayOf(1, 1001),
    "ImageNet/labels.txt",
    "ImageNet/"
)