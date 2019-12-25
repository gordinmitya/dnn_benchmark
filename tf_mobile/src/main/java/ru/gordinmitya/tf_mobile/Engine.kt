package ru.gordinmitya.tf_mobile

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import ru.gordinmitya.common.InferenceResult
import ru.gordinmitya.common.SuccessResult
import ru.gordinmitya.common.Timeit

class Engine(val context: Context) {

    private val CHANNELS = 3

    private lateinit var tensorInterface: TensorFlowInferenceInterface
    private lateinit var bitmap: Bitmap
    private var intValues: IntArray = IntArray(0)
    private var floatValues: FloatArray = FloatArray(0)
    private lateinit var INPUT: String
    private lateinit var OUTPUT: String

    private fun prepare(model: ConvertedModel) {
        tensorInterface = TensorFlowInferenceInterface(context.assets, model.file)
        val size = model.inputSize
        bitmap = Bitmap.createBitmap(size.first, size.second, Bitmap.Config.ARGB_8888)
        val flattenSize = bitmap.width * bitmap.height
        intValues = IntArray(flattenSize)
        floatValues = FloatArray(flattenSize * CHANNELS)
        INPUT = model.inputName
        OUTPUT = model.outputName
    }

    private fun fire() {
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (i in intValues.indices) {
            val value = intValues[i]
            floatValues[i * 3] = (value shr 16 and 0xFF) / 255.0f
            floatValues[i * 3 + 1] = (value shr 8 and 0xFF) / 255.0f
            floatValues[i * 3 + 2] = (value and 0xFF) / 255.0f
        }

        tensorInterface.feed(
            INPUT,
            floatValues,
            1L,
            bitmap.height.toLong(),
            bitmap.width.toLong(),
            CHANNELS.toLong()
        )
        tensorInterface.run(arrayOf(OUTPUT), false)
        tensorInterface.fetch(OUTPUT, floatValues)
    }

    private fun release() {
        tensorInterface.close()
    }

    companion object {
        fun benchmark(
            context: Context,
            model: ConvertedModel,
            inferenceType: TFMobileInfereceType,
            loops: Int
        ): InferenceResult {
            val engine = Engine(context)
            val prepareTime = Timeit.measure {
                engine.prepare(model)
            }
            val times = LongArray(loops)
            for (i in 0 until loops) {
                times[i] = Timeit.measure {
                    engine.fire()
                }
            }
            engine.release()
            return SuccessResult(
                TFMobileFramework,
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