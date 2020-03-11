package ru.gordinmitya.common.segmentation

import android.graphics.Bitmap
import java.nio.ByteBuffer

object MaskUtils {
    fun convertBytebufferMaskToBitmap(
        inputBuffer: ByteBuffer,
        model: SegmentationModel
    ): Bitmap {
        val (width, height) = model.inputSize
        val numClasses = model.outputClasses
        val colors = model.colors
        val maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val itemsFound = HashSet<Int>()
        inputBuffer.rewind()

        for (y in 0 until height) {
            for (x in 0 until width) {
                var maxVal = 0f
                var bit = 0

                for (c in 0 until numClasses) {
                    val value = inputBuffer
                        .getFloat((y * width * numClasses + x * numClasses + c) * 4)
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