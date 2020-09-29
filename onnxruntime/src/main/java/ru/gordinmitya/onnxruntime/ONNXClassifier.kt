package ru.gordinmitya.onnxruntime

import android.content.Context
import android.graphics.Bitmap
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.Constants
import ru.gordinmitya.common.classification.Classifier
import ru.gordinmitya.common.utils.AssetUtil
import java.lang.RuntimeException

class ONNXClassifier(
    val context: Context,
    configuration: Configuration,
    val convertedModel: ConvertedModel,
    val inferenceType: ONNXInfereceType
) : Classifier(configuration) {

    private var runtime: ONNXNative? = null
    private var output: FloatArray? = null

    override fun prepare() {
        val file = AssetUtil.copyFileToCache(context, convertedModel.fileName)
        val (width, height) = convertedModel.model.inputSize
        val use_nnapi = inferenceType == ONNX_NNAPI
        runtime = ONNXNative(file.absolutePath, use_nnapi, Constants.NUM_THREADS, width, height)
        val outputSize = convertedModel.model.outputShape.reduce { acc, i -> acc * i }
        output = FloatArray(outputSize)
    }

    override fun predict(input: Bitmap): FloatArray {
        val status = runtime!!.run(input, output)
        if (!status)
            throw RuntimeException("Failed to inference with onnxruntime")
        return output!!
    }

    override fun release() {
        runtime?.delete()
    }
}