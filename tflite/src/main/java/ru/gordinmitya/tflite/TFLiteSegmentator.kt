package ru.gordinmitya.tflite

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Delegate
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.Constants
import ru.gordinmitya.common.segmentation.SegmentationModel
import ru.gordinmitya.common.segmentation.Segmentator

class TFLiteSegmentator(
    val context: Context,
    configuration: Configuration,
    val convertedModel: ConvertedModel<SegmentationModel>,
    val inferenceType: TFLiteInferenceType
) : Segmentator(configuration) {

    private var interpreter: Interpreter? = null
    private var delegate: Delegate? = null
    private lateinit var inputImageBuffer: TensorImage
    private lateinit var outputBuffer: TensorBuffer

    override fun prepare() {
        val options = Interpreter.Options()
        val byteBuffer = FileUtil.loadMappedFile(context, convertedModel.file)

        delegate = when (inferenceType) {
            TFLITE_OPENGL -> GpuDelegate()
            TFLITE_NNAPI -> NnApiDelegate()
            TFLITE_CPU -> null
        }
        delegate?.let {
            options.addDelegate(it)
        }
        options.setNumThreads(Constants.NUM_THREADS)
        interpreter = Interpreter(byteBuffer, options)
        val imageTensorIndex = 0
        // {1, height, width, 3}
        interpreter!!.getInputTensor(imageTensorIndex).let {
            val shape = it.shape()
            val mis = convertedModel.model.inputSize
            check(shape[1] == mis.first && shape[2] == mis.second)
            val dataType = it.dataType()
            inputImageBuffer = TensorImage(dataType)
        }

        val probabilityTensorIndex = 0
        interpreter!!.getOutputTensor(probabilityTensorIndex).let {
            val shape = it.shape()
            val dataType = it.dataType()
            outputBuffer = TensorBuffer.createFixedSize(shape, dataType)
        }
    }

    override fun predict(input: Bitmap): FloatArray {
        inputImageBuffer.load(input)
        val imageProcessor = ImageProcessor.Builder()
            .add(NormalizeHelper.toOp(convertedModel.model.mean, convertedModel.model.std))
            .build()
        inputImageBuffer = imageProcessor.process(inputImageBuffer)

        interpreter!!.run(inputImageBuffer.buffer, outputBuffer.buffer.rewind())

        return outputBuffer.floatArray
    }

    override fun release() {
        interpreter?.close()
        (delegate as? NnApiDelegate)?.close()
        (delegate as? GpuDelegate)?.close()
    }
}