package ru.gordinmitya.snpe

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import com.qualcomm.qti.snpe.FloatTensor
import com.qualcomm.qti.snpe.NeuralNetwork
import com.qualcomm.qti.snpe.SNPE
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.classification.Classifier


class SNPEClassifier(
    val context: Context,
    configuration: Configuration,
    val convertedModel: ConvertedModel,
    val inferenceType: SNPEInferenceType
) : Classifier(configuration) {

    private var network: NeuralNetwork? = null
    private lateinit var intValues: IntArray
    private lateinit var floatValues: FloatArray
    private lateinit var INPUT: String
    private lateinit var OUTPUT: String
    private lateinit var inputsMap: HashMap<String, FloatTensor>

    private var inputWidth = 0
    private var inputHeight = 0
    private var inputChannels = 0

    override fun prepare() {
        val application = context.applicationContext as Application
        context.assets.open(convertedModel.file).use { inStream ->
            network = SNPE.NeuralNetworkBuilder(application)
                .setRuntimeOrder(inferenceType.runtime)
                .setModel(inStream, inStream.available())
                .build()
        }
        inputChannels = convertedModel.model.inputChannels
        inputWidth = convertedModel.model.inputSize.first
        inputHeight = convertedModel.model.inputSize.second

        intValues = IntArray(inputWidth * inputHeight)
        floatValues = FloatArray(inputWidth * inputHeight * inputChannels)
        INPUT = convertedModel.inputName
        OUTPUT = convertedModel.outputName

        val inputTensor = network!!.createFloatTensor(1, inputHeight, inputWidth, 3)
        inputsMap = hashMapOf(INPUT to inputTensor)
    }

    override fun predict(bitmap: Bitmap): FloatArray {
        require(inputWidth == bitmap.width && inputHeight == bitmap.height)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (i in intValues.indices) {
            val value = intValues[i]
            floatValues[i * 3] = (value shr 16 and 0xFF) / 255.0f
            floatValues[i * 3 + 1] = (value shr 8 and 0xFF) / 255.0f
            floatValues[i * 3 + 2] = (value and 0xFF) / 255.0f
        }

        inputsMap[INPUT]!!.write(floatValues, 0, floatValues.size)

        val outputMap = network!!.execute(inputsMap)

        val outputTensor = outputMap[OUTPUT]!!
        val prediction = FloatArray(outputTensor.size)
        outputTensor.read(prediction, 0, outputTensor.size)

        val modelOutputSize = convertedModel.model.outputShape
            .reduce { acc, i -> acc * i }
        require(prediction.size == modelOutputSize)

        return prediction
    }

    override fun release() {
        network?.release()
    }
}