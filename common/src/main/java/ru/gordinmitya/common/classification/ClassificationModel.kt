package ru.gordinmitya.common.classification

import ru.gordinmitya.common.Model
import ru.gordinmitya.common.Task

open class ClassificationModel(
    name: String,
    description: String,
    mean: FloatArray,
    std: FloatArray,
    inputSize: Pair<Int, Int>,
    inputChannels: Int,
    outputShape: IntArray,
    val labelsFile: String,
    val samplesDir: String
) : Model(
    name, description,
    mean, std,
    Task.CLASSIFICATION,
    inputSize, inputChannels, outputShape
) {
    override fun toString(): String = "$name (classification) $description"
}

// https://www.tensorflow.org/lite/guide/hosted_models
// search for Mobilenet_V2_1.0_224
object MobileNetModel : ClassificationModel(
    "mobilenet_v2",
    "From tensorflow hosted models",
    floatArrayOf(0.485f, 0.456f, 0.406f),
    floatArrayOf(0.229f, 0.224f, 0.225f),
    224 to 224,
    3,
    intArrayOf(1, 1000),
    "ImageNet/labels.txt",
    "ImageNet/"
)