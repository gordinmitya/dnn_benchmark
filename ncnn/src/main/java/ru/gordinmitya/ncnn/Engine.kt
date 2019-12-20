package ru.gordinmitya.ncnn

import android.content.Context
import android.graphics.Bitmap
import ru.gordinmitya.common.InferenceResult
import ru.gordinmitya.common.SuccessResult
import ru.gordinmitya.common.Timeit


class Engine private constructor(val context: Context) {

    private lateinit var ncnn: NCNNNative
    private lateinit var bitmap: Bitmap

    private fun prepare(model: ConvertedModel, inferenceType: NCNNInfereceType) {
        ncnn = NCNNNative()
        val created = ncnn.init(context.assets, model.paramFile, model.binFile, inferenceType.gpu)
        if (!created)
            throw RuntimeException("Failed to initialize NCNN")
        bitmap = Bitmap.createBitmap(
            model.inputSize.first,
            model.inputSize.second,
            Bitmap.Config.ARGB_8888
        )
    }

    private fun fire() {
        val status = ncnn.run(bitmap)
        if (!status)
            throw RuntimeException("Failed to inference with NCNN")
    }

    private fun release() {
        ncnn.release()
    }

    companion object {
        fun benchmark(
            context: Context,
            model: ConvertedModel,
            inferenceType: NCNNInfereceType,
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
                NCNNFramework,
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