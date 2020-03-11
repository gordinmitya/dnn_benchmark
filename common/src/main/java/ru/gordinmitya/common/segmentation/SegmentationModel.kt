package ru.gordinmitya.common.segmentation

import android.graphics.Color
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.Task
import kotlin.random.Random

open class SegmentationModel(
    name: String,
    description: String,
    inputSize: Pair<Int, Int>,
    inputChannels: Int,
    val outputClasses: Int,
    val labels: List<String>,
    val colors: IntArray,
    val samplesDir: String
) : Model(
    name, description, Task.SEGMENTATION,
    inputSize, inputChannels,
    intArrayOf(inputSize.first, inputSize.second, outputClasses)
) {
    override fun toString(): String = "$name (classification) $description"

    companion object {
        private fun getRandomRGBInt(random: Random) = (255 * random.nextFloat()).toInt()
        fun generateColors(size: Int): IntArray {
            val random = Random(System.currentTimeMillis())
            val colors = IntArray(size)
            colors[0] = Color.TRANSPARENT
            for (i in 1 until size) {
                colors[i] = Color.argb(
                    (128),
                    getRandomRGBInt(random),
                    getRandomRGBInt(random),
                    getRandomRGBInt(random)
                )
            }
            return colors
        }
    }
}

// https://www.tensorflow.org/lite/models/segmentation/overview
object DeepLabModel : SegmentationModel(
    "deeplab_v3",
    "From tensorflow hosted models",
    257 to 257,
    3,
    21,
    listOf(
        "background", "aeroplane", "bicycle", "bird", "boat", "bottle", "bus",
        "car", "cat", "chair", "cow", "dining table", "dog", "horse", "motorbike",
        "person", "potted plant", "sheep", "sofa", "train", "tv"
    ),
    generateColors(21),
    "ImageNet/"
)