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

    private val CHANNELS = 3

    private var tensorInterface: TensorFlowInferenceInterface? = null
    private var intValues: IntArray = IntArray(0)
    private var floatValues: FloatArray = FloatArray(0)
    private lateinit var INPUT: String
    private lateinit var OUTPUT: String

    override fun prepare() {
        tensorInterface = TensorFlowInferenceInterface(context.assets, convertedModel.file)
        val (width, height) = convertedModel.model.inputSize
        val flattenSize = width * height
        intValues = IntArray(flattenSize)
        floatValues = FloatArray(flattenSize * CHANNELS)
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
            bitmap.height.toLong(),
            bitmap.width.toLong(),
            CHANNELS.toLong()
        )
        tensorInterface!!.run(arrayOf(OUTPUT), false)
        val prediction = FloatArray(convertedModel.model.outputSize)
        tensorInterface!!.fetch(OUTPUT, prediction)

        return prediction
    }

    override fun release() {
        tensorInterface?.close()
    }
}