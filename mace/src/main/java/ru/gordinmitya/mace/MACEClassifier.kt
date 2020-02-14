package ru.gordinmitya.mace

import android.content.Context
import android.graphics.Bitmap
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.classification.Classifier
import ru.gordinmitya.common.utils.AssetUtil


class MACEClassifier(
    val context: Context,
    configuration: Configuration,
    val convertedModel: ConvertedModel,
    val inferenceType: MACEInferenceType
) : Classifier(configuration) {

    var maceNative: MACENative? = null

    private lateinit var intValues: IntArray
    private lateinit var floatValues: FloatArray

    private var inputChannels = 0

    override fun prepare() {
        val pbFile = AssetUtil.copyFileToCache(context, convertedModel.pbFileName).path
        val dataFile = AssetUtil.copyFileToCache(context, convertedModel.dataFileName).path
        val cacheDir = context.cacheDir.path

        val (width, height) = convertedModel.model.inputSize
        inputChannels = convertedModel.model.inputChannels

        intValues = IntArray(width * height)
        floatValues = FloatArray(intValues.size * inputChannels)

        val modelInfo = ModelInfoNative.fromConvertedModel(convertedModel, pbFile, dataFile)
        maceNative = MACENative(modelInfo, inferenceType, cacheDir)
    }

    override fun predict(bitmap: Bitmap): FloatArray {
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (i in intValues.indices) {
            val value = intValues[i]
            floatValues[i * 3] = (value shr 16 and 0xFF) / 255.0f
            floatValues[i * 3 + 1] = (value shr 8 and 0xFF) / 255.0f
            floatValues[i * 3 + 2] = (value and 0xFF) / 255.0f
        }

        val result = maceNative!!.run(floatValues)

        return result
    }

    override fun release() {
        maceNative?.release()
    }
}