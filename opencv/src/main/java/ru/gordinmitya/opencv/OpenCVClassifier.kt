package ru.gordinmitya.opencv

import android.content.Context
import android.graphics.Bitmap
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.dnn.Dnn
import org.opencv.dnn.Net
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.classification.Classifier
import ru.gordinmitya.common.utils.AssetUtil

class OpenCVClassifier(
    val context: Context,
    configuration: Configuration,
    val convertedModel: ConvertedModel,
    val inferenceType: OpenCVInfereceType
) : Classifier(configuration) {


    private var net: Net? = null

    override fun prepare() {
        OpenCVLoader.initDebug()
        val file = AssetUtil.copyFileToCache(context, convertedModel.file)
        net = Dnn.readNetFromTensorflow(file.path)
    }

    override fun predict(bitmap: Bitmap): FloatArray {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        net!!.setInput(mat, convertedModel.inputName)
        val result = net!!.forward(convertedModel.outputName)

        return FloatArray(1000)
    }

    override fun release() {
        // will be automatically destroyed by finalize
    }
}