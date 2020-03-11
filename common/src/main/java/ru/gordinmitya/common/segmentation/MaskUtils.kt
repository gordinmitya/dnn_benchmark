package ru.gordinmitya.common.segmentation

import android.graphics.Bitmap

object MaskUtils {
    fun convertMaskToBitmap(
        array: FloatArray,
        model: SegmentationModel
    ): Bitmap {
        val (width, height) = model.inputSize
        val numClasses = model.outputClasses
        val colors = model.colors
        val maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val itemsFound = HashSet<Int>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                var maxVal = 0f
                var bit = 0

                for (c in 0 until numClasses) {
                    val value = array[y * width * numClasses + x * numClasses + c]
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
}