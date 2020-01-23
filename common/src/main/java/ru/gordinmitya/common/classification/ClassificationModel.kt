package ru.gordinmitya.common.classification

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.Task

open class ClassificationModel(
    name: String,
    description: String,
    val inputSize: Pair<Int, Int>,
    val outputSize: Int,
    val labelsFile: String,
    val samplesDir: String
) : Model(name, description, Task.CLASSIFICATION) {
    override fun toString(): String = "$name (classification) $description"
}

object MobileNet_v2 : ClassificationModel(
    "mobilenet_v2",
    "made by Google",
    224 to 224,
    1001,
    "ImageNet/labels.txt",
    "ImageNet/"
)