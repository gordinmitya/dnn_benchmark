package ru.gordinmitya.common.classification

import ru.gordinmitya.common.Benchmarker
import ru.gordinmitya.common.FailureResult
import ru.gordinmitya.common.InferenceResult
import ru.gordinmitya.common.SuccessResult
import ru.gordinmitya.common.utils.Timeit

object ClassificationRunner {
    val KIKOFF_TIMEOUT = 10_000L
    val DECREASE_LOOPS_TIMEOUT = 5_000L
    val DECREASE_LOOPS_COUNT = 5

    fun benchmark(
        classifier: Classifier,
        modelAssets: ModelAssets,
        benchmarker: Benchmarker,
        evaluator: ClassificationEvaluator,
        loops: Int,
        progressCallback: ClassificationProgressCallback? = null,
        failHard: Boolean = false
    ): InferenceResult {
        return try {
            val prepareTime = Timeit.measure {
                classifier.prepare()
            }
            progressCallback?.onPrepared(prepareTime)
            benchmarker.addPreparation((prepareTime))
            for (i in 0 until loops) {
                val sample = modelAssets.getGT()
                progressCallback?.onNext(sample.image, i + 1, loops)
                var prediction: FloatArray = floatArrayOf()
                val time = Timeit.measure {
                    prediction = classifier.predict(sample.image)
                }
                val label = modelAssets.getLabelForPrediction(prediction)
                progressCallback?.onResult(label, time)
                benchmarker.addNext(time)
                evaluator.addNext(prediction, label, sample)
                if (Thread.interrupted())
                    throw InterruptedException()
                if (time >= KIKOFF_TIMEOUT)
                    break
                if (time >= DECREASE_LOOPS_TIMEOUT && i > DECREASE_LOOPS_COUNT)
                    break
            }
            SuccessResult(
                classifier.configuration,
                benchmarker.summarize(),
                evaluator.summarize()
            )
        } catch (e: RuntimeException) {
            if (failHard)
                throw e
            FailureResult(
                classifier.configuration,
                e.message ?: ""
            )
        } finally {
            classifier.release()
        }
    }
}