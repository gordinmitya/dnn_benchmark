package ru.gordinmitya.ncnn

import android.content.Context
import android.graphics.Bitmap
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.Constants
import ru.gordinmitya.common.classification.Classifier


class NCNNClassifier(
    val context: Context,
    configuration: Configuration,
    val convertedModel: ConvertedModel,
    val inferenceType: NCNNInfereceType
) : Classifier(configuration) {

    private var ncnn: NCNNNative? = null

    override fun prepare() {
        ncnn = NCNNNative()
        val created = ncnn!!.init(
            context.assets,
            convertedModel.paramFile,
            convertedModel.binFile,
            Constants.NUM_THREADS,
            inferenceType.gpu
        )
        if (!created)
            throw RuntimeException("Failed to initialize NCNN")
    }

    override fun predict(input: Bitmap): FloatArray {
        val outputSize = convertedModel.model.outputShape
            .reduce { acc, i -> acc * i }
        val prediction = FloatArray(outputSize)
        val status = ncnn!!.run(input, prediction)
        if (!status)
            throw RuntimeException("Failed to inference with NCNN")
        return prediction
    }

    override fun release() {
        ncnn?.release()
    }
}