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
import ru.gordinmitya.common.classification.Classifier


class TFLiteClassifier(
    val context: Context,
    configuration: Configuration,
    val convertedModel: ConvertedModel,
    val inferenceType: TFLiteInferenceType
) : Classifier(configuration) {

    private lateinit var interpreter: Interpreter
    private var delegate: Delegate? = null
    private lateinit var inputImageBuffer: TensorImage
    private lateinit var outputProbabilityBuffer: TensorBuffer

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
        interpreter.getInputTensor(imageTensorIndex).let {
            val shape = it.shape()
            val mis = convertedModel.model.inputSize
            check(shape[1] == mis.first && shape[2] == mis.second)
            val dataType = it.dataType()
            inputImageBuffer = TensorImage(dataType)
        }

        val probabilityTensorIndex = 0
        interpreter.getOutputTensor(probabilityTensorIndex).let {
            val shape = it.shape() // {1, NUM_CLASSES}
            val dataType = it.dataType()
            outputProbabilityBuffer = TensorBuffer.createFixedSize(shape, dataType)
        }
    }

    override fun predict(bitmap: Bitmap): FloatArray {
        inputImageBuffer.load(bitmap)
        val imageProcessor = ImageProcessor.Builder()
            .add(NormalizeOp(127.5f, 127.5f))
            .build()
        inputImageBuffer = imageProcessor.process(inputImageBuffer)

        interpreter.run(inputImageBuffer.buffer, outputProbabilityBuffer.buffer.rewind())

        val outputSize = convertedModel.model.outputShape
            .reduce { acc, i -> acc * i }
        check(outputProbabilityBuffer.flatSize == outputSize)
        return outputProbabilityBuffer.floatArray
    }

    override fun release() {
        interpreter.close()
        (delegate as? NnApiDelegate)?.close()
        (delegate as? GpuDelegate)?.close()
    }
}