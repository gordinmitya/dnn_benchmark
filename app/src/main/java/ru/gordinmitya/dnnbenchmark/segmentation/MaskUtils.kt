package ru.gordinmitya.dnnbenchmark.segmentation

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import ru.gordinmitya.common.DataOrder
import ru.gordinmitya.common.segmentation.SegmentationModel
import kotlin.random.Random

object MaskUtils {
    fun convertMaskToBitmap(
        array: FloatArray,
        model: SegmentationModel,
        dataOrder: DataOrder
    ): Bitmap {
        val (width, height) = model.outputShape
        val classes = model.outputClasses

        require(array.size == width * height * classes)

        val colors = model.colors
        val maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val itemsFound = HashSet<Int>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                var maxVal = 0f
                var bit = 0

                for (c in 0 until classes) {
                    val ind = when (dataOrder) {
                        DataOrder.NHWC -> y * height * classes + x * classes + c
                        DataOrder.NCWH -> width * height * c + y * height + x
                    }
                    val value = array[ind]
                    if (c == 0 || value > maxVal) {
                        maxVal = value
                        bit = c
                    }
                }

                itemsFound.add(bit)
                maskBitmap.setPixel(x, y, colors[bit])
            }
        }

        return maskBitmap
    }

    fun getRandomRGBInt(random: Random) = (255 * random.nextFloat()).toInt()

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
        Log.d("COLORS", colors.contentToString())
        return colors
    }
}