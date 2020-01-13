package ru.gordinmitya.ncnn

import android.content.Context
import android.graphics.Bitmap
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.classification.Classifier


class NCNNClassifier(
    val context: Context,
    configuration: Configuration,
    val convertedModel: ConvertedModel,
    val inferenceType: NCNNInfereceType
) : Classifier(configuration) {

    private lateinit var ncnn: NCNNNative

    override fun prepare() {
        ncnn = NCNNNative()
        val created = ncnn.init(
            context.assets,
            convertedModel.paramFile,
            convertedModel.binFile,
            inferenceType.gpu
        )
        if (!created)
            throw RuntimeException("Failed to initialize NCNN")
    }

    override fun predict(bitmap: Bitmap): FloatArray {
        val prediction = FloatArray(convertedModel.model.outputSize)
        val status = ncnn.run(bitmap, prediction)
        if (!status)
            throw RuntimeException("Failed to inference with NCNN")
        return prediction
    }

    override fun release() {
        ncnn.release()
    }
}