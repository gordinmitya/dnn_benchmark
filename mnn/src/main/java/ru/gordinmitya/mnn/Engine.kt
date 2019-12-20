package ru.gordinmitya.mnn

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import com.taobao.android.mnn.MNNImageProcess
import com.taobao.android.mnn.MNNNetInstance
import com.taobao.android.mnn.MNNNetInstance.Session.Tensor
import ru.gordinmitya.common.InferenceResult
import ru.gordinmitya.common.SuccessResult
import ru.gordinmitya.common.Timeit
import java.io.File


class Engine private constructor(val context: Context) {

    private lateinit var net: MNNNetInstance
    private lateinit var session: MNNNetInstance.Session
    private lateinit var inputTensor: Tensor
    private lateinit var outputTensor: Tensor
    private lateinit var bitmap: Bitmap

    private fun copyFileToCache(model: ConvertedModel): File {
        val file = File(context.cacheDir, model.fileName)
        if (file.exists())
            return file
        context.assets.open(model.fileName).use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun prepare(file: File, model: ConvertedModel, inferenceType: MNNInferenceType) {
        net = MNNNetInstance.createFromFile(file.absolutePath)
        val config = MNNNetInstance.Config().also {
            it.forwardType = inferenceType.type
            it.numThread = 4
            it.outputTensors = arrayOf(model.outputName)
        }
        session = net.createSession(config)
        inputTensor = session.getInput(null)
        outputTensor = session.getOutput(null)
        val inputSize = inputTensor.dimensions

        bitmap = Bitmap.createBitmap(inputSize[0], inputSize[1], Bitmap.Config.ARGB_8888)
    }

    private fun fire() {
        val config = MNNImageProcess.Config().also {
            it.mean = floatArrayOf(103.94f, 116.78f, 123.68f)
            it.normal = floatArrayOf(0.017f, 0.017f, 0.017f)
            it.source = MNNImageProcess.Format.RGBA
            it.source = MNNImageProcess.Format.RGB
        }
        MNNImageProcess.convertBitmap(bitmap, inputTensor, config, Matrix())
        session.run()
        val result: FloatArray = outputTensor.floatData
    }

    private fun release() {
        net.release()
    }

    companion object {
        fun benchmark(
            context: Context,
            model: ConvertedModel,
            inferenceType: MNNInferenceType,
            loops: Int
        ): InferenceResult {
            val engine = Engine(context)
            val file = engine.copyFileToCache(model)
            val prepareTime = Timeit.measure {
                engine.prepare(file, model, inferenceType)
            }
            val times = LongArray(loops)
            for (i in 0 until loops) {
                times[i] = Timeit.measure {
                    engine.fire()
                }
            }
            engine.release()
            return SuccessResult(
                MNNFramework,
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