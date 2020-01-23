package ru.gordinmitya.mnn

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import com.taobao.android.mnn.MNNImageProcess
import com.taobao.android.mnn.MNNNetInstance
import com.taobao.android.mnn.MNNNetInstance.Session.Tensor
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.classification.Classifier
import java.io.File


class MNNClassifier(
    val context: Context,
    configuration: Configuration,
    val convertedModel: ConvertedModel,
    val inferenceType: MNNInferenceType
) : Classifier(configuration) {

    private var net: MNNNetInstance? = null
    private lateinit var session: MNNNetInstance.Session
    private lateinit var inputTensor: Tensor
    private lateinit var outputTensor: Tensor
    private lateinit var inputSize: IntArray

    private fun copyFileToCache(fileName: String): File {
        val file = File(context.cacheDir, fileName)
        if (file.exists())
            return file
        context.assets.open(fileName).use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    override fun prepare() {
        val file = copyFileToCache(convertedModel.fileName)
        net = MNNNetInstance.createFromFile(file.absolutePath)
        val config = MNNNetInstance.Config().also {
            it.forwardType = inferenceType.type
            it.numThread = 4
            it.outputTensors = arrayOf(convertedModel.outputName)
        }
        session = net!!.createSession(config)
        inputTensor = session.getInput(null)
        outputTensor = session.getOutput(null)
        inputSize = inputTensor.dimensions
    }

    override fun predict(bitmap: Bitmap): FloatArray {
        require(bitmap.width == inputSize[2])
        require(bitmap.height == inputSize[3])

        val config = MNNImageProcess.Config().also {
            it.mean = floatArrayOf(103.94f, 116.78f, 123.68f)
            it.normal = floatArrayOf(0.017f, 0.017f, 0.017f)
            it.source = MNNImageProcess.Format.RGBA
            it.source = MNNImageProcess.Format.RGB
        }
        MNNImageProcess.convertBitmap(bitmap, inputTensor, config, Matrix())
        session.run()
        return outputTensor.floatData
    }

    override fun release() {
        net?.release()
    }
}