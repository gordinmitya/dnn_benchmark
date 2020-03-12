package ru.gordinmitya.mnn

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import com.taobao.android.mnn.MNNImageProcess
import com.taobao.android.mnn.MNNNetInstance
import com.taobao.android.mnn.MNNNetInstance.Session.Tensor
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.Constants
import ru.gordinmitya.common.segmentation.SegmentationModel
import ru.gordinmitya.common.segmentation.Segmentator
import ru.gordinmitya.common.utils.AssetUtil


class MNNSegmentator(
    val context: Context,
    configuration: Configuration,
    val convertedModel: ConvertedModel<SegmentationModel>,
    val inferenceType: MNNInferenceType
) : Segmentator(configuration) {

    private var net: MNNNetInstance? = null
    private lateinit var session: MNNNetInstance.Session
    private lateinit var inputTensor: Tensor
    private lateinit var outputTensor: Tensor
    private lateinit var inputSize: IntArray

    override fun prepare() {
        val file = AssetUtil.copyFileToCache(context, convertedModel.fileName)
        net = MNNNetInstance.createFromFile(file.absolutePath)
        val config = MNNNetInstance.Config().also {
            it.forwardType = inferenceType.type
            it.numThread = Constants.NUM_THREADS
        }
        session = net!!.createSession(config)
        inputTensor = session.getInput(null)
        outputTensor = session.getOutput(null)
        inputSize = inputTensor.dimensions
    }

    override fun predict(input: Bitmap): FloatArray {
        require(input.width == inputSize[2])
        require(input.height == inputSize[3])

        val config = MNNImageProcess.Config().also {
            it.mean = floatArrayOf(127.5f, 127.5f, 127.5f)
            it.normal = floatArrayOf(1 / 127.5f, 1 / 127.5f, 1 / 127.5f)
            it.source = MNNImageProcess.Format.RGBA
            it.dest = MNNImageProcess.Format.RGB
        }
        MNNImageProcess.convertBitmap(input, inputTensor, config, Matrix())
        session.run()
        return outputTensor.floatData
    }

    override fun release() {
        net?.release()
    }
}