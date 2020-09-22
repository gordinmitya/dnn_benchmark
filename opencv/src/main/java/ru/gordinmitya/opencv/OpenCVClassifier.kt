package ru.gordinmitya.opencv

import android.content.Context
import android.graphics.Bitmap
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.dnn.Dnn
import org.opencv.dnn.Net
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.cvtColor
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
        net = Dnn.readNetFromONNX(file.path)
    }

    override fun predict(input: Bitmap): FloatArray {
        val mat = Mat()
        Utils.bitmapToMat(input, mat)
        cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB)
        val blob = Dnn.blobFromImage(
            mat,
            0.017,
            mat.size(),
            Scalar(103.94, 116.78, 123.68)
        )
        net!!.setInput(blob, convertedModel.inputName)
        val output = net!!.forward(convertedModel.outputName)
        val result = FloatArray(output.size(1))
        for (i in result.indices) {
            result[i] = output.get(0, i)[0].toFloat()
        }
        return result
    }

    override fun release() {
        // will be automatically destroyed by finalize
    }
}