package ru.gordinmitya.tf_mobile

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.classification.Classifier

class TFMobileClassifier(
    val context: Context,
    configuration: Configuration,
    val convertedModel: ConvertedModel,
    val inferenceType: TFMobileInfereceType
) : Classifier(configuration) {

    private var tensorInterface: TensorFlowInferenceInterface? = null
    private lateinit var intValues: IntArray
    private lateinit var floatValues: FloatArray

    private lateinit var INPUT: String
    private lateinit var OUTPUT: String
    private var inputWidth = 0
    private var inputHeight = 0
    private var inputChannels = 0

    override fun prepare() {
        tensorInterface = TensorFlowInferenceInterface(context.assets, convertedModel.file)
        inputChannels = convertedModel.model.inputChannels
        inputWidth = convertedModel.model.inputSize.first
        inputHeight = convertedModel.model.inputSize.second

        intValues = IntArray(inputWidth * inputHeight)
        floatValues = FloatArray(inputWidth * inputHeight * inputChannels)
        INPUT = convertedModel.inputName
        OUTPUT = convertedModel.outputName
    }

    override fun predict(bitmap: Bitmap): FloatArray {
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (i in intValues.indices) {
            val value = intValues[i]
            floatValues[i * 3] = (value shr 16 and 0xFF) / 255.0f
            floatValues[i * 3 + 1] = (value shr 8 and 0xFF) / 255.0f
            floatValues[i * 3 + 2] = (value and 0xFF) / 255.0f
        }

        tensorInterface!!.feed(
            INPUT,
            floatValues,
            1L,
            inputWidth.toLong(),
            inputHeight.toLong(),
            inputChannels.toLong()
        )
        tensorInterface!!.run(arrayOf(OUTPUT), false)
        val outputSize = convertedModel.model.outputShape
            .reduce { acc, i -> acc * i }
        val prediction = FloatArray(outputSize)
        tensorInterface!!.fetch(OUTPUT, prediction)

        return prediction
    }

    override fun release() {
        tensorInterface?.close()
    }
}