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
import ru.gordinmitya.common.InferenceResult
import ru.gordinmitya.common.SuccessResult
import ru.gordinmitya.common.Timeit


class Engine private constructor(val context: Context) {

    val NUM_THREADS = 4

    private lateinit var bitmap: Bitmap

    private lateinit var interpreter: Interpreter
    private var delegate: Delegate? = null
    private lateinit var inputImageBuffer: TensorImage
    private lateinit var outputProbabilityBuffer: TensorBuffer

    private fun prepare(model: ConvertedModel, inferenceType: TFLiteInferenceType) {
        val options = Interpreter.Options()
        val byteBuffer = FileUtil.loadMappedFile(context, model.file)

        delegate = when (inferenceType) {
            TFLITE_OPENGL -> GpuDelegate()
            TFLITE_NNAPI -> NnApiDelegate()
            TFLITE_CPU -> null
        }
        delegate?.let {
            options.addDelegate(it)
        }
        options.setNumThreads(NUM_THREADS)
        interpreter = Interpreter(byteBuffer, options)
        val imageTensorIndex = 0
        // {1, height, width, 3}
        interpreter.getInputTensor(imageTensorIndex).let {
            val shape = it.shape()
            val dataType = it.dataType()
            inputImageBuffer = TensorImage(dataType)
            bitmap = Bitmap.createBitmap(
                shape[1],
                shape[2],
                Bitmap.Config.ARGB_8888
            )
        }

        val probabilityTensorIndex = 0
        interpreter.getOutputTensor(probabilityTensorIndex).let {
            val shape = it.shape() // {1, NUM_CLASSES}
            val dataType = it.dataType()
            outputProbabilityBuffer = TensorBuffer.createFixedSize(shape, dataType)
        }
    }

    private fun fire() {
        inputImageBuffer.load(bitmap)
        val imageProcessor = ImageProcessor.Builder()
            .add(NormalizeOp(127.5f, 127.5f))
            .build()
        inputImageBuffer = imageProcessor.process(inputImageBuffer)

        interpreter.run(inputImageBuffer.buffer, outputProbabilityBuffer.buffer.rewind())
    }

    private fun release() {
        interpreter.close()
        (delegate as? NnApiDelegate)?.close()
        (delegate as? GpuDelegate)?.close()
    }

    companion object {
        fun benchmark(
            context: Context,
            model: ConvertedModel,
            inferenceType: TFLiteInferenceType,
            loops: Int
        ): InferenceResult {
            val engine = Engine(context)
            val prepareTime = Timeit.measure {
                engine.prepare(model, inferenceType)
            }
            val times = LongArray(loops)
            for (i in 0 until loops) {
                times[i] = Timeit.measure {
                    engine.fire()
                }
            }
            engine.release()
            return SuccessResult(
                TFLiteFramework,
                inferenceType,
                model.model,
                loops,

                times.min()!!,
                times.max()!!,
                times.average(),
                times[0],
                prepareTime
            )
        }
    }
}