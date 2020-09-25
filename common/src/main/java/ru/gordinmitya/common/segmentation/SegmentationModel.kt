package ru.gordinmitya.common.segmentation

import android.graphics.Color
import android.util.Log
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.Task
import kotlin.random.Random

open class SegmentationModel(
    name: String,
    description: String,
    mean: FloatArray,
    std: FloatArray,
    inputSize: Pair<Int, Int>,
    inputChannels: Int,
    val outputClasses: Int,
    val labels: List<String>,
    val colors: IntArray,
    val samplesDir: String
) : Model(
    name, description,
    mean, std,
    Task.SEGMENTATION,
    inputSize, inputChannels,
    intArrayOf(inputSize.first, inputSize.second, outputClasses)
) {
    override fun toString(): String = "$name (segmentation) $description"
}

// https://www.tensorflow.org/lite/models/segmentation/overview
object DeepLabModel : SegmentationModel(
    "deeplab_v3",
    "From tensorflow hosted models",
    floatArrayOf(0.5f, 0.5f, 0.5f),
    floatArrayOf(0.5f, 0.5f, 0.5f),
    257 to 257,
    3,
    21,
    listOf(
        "background", "aeroplane", "bicycle", "bird", "boat", "bottle", "bus",
        "car", "cat", "chair", "cow", "dining table", "dog", "horse", "motorbike",
        "person", "potted plant", "sheep", "sofa", "train", "tv"
    ),
    intArrayOf(
        0,
        -2133513366,
        -2140500538,
        -2137541320,
        -2138610828,
        -2137235995,
        -2141188538,
        -2146389943,
        -2132438607,
        -2131264292,
        -2140152552,
        -2132529199,
        -2143394679,
        -2135360066,
        -2134373907,
        -2146292326,
        -2141384524,
        -2143296444,
        -2146416403,
        -2139263617,
        -2139156123
    ),
    "PascalVOC"
)