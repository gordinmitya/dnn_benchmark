package ru.gordinmitya.common.classification

import ru.gordinmitya.common.Benchmarker
import ru.gordinmitya.common.InferenceResult
import ru.gordinmitya.common.SuccessResult
import ru.gordinmitya.common.utils.Timeit

object ClassificationRunner {
    fun benchmark(
        classifier: Classifier,
        sampler: GTSampler,
        benchmarker: Benchmarker,
        evaluator: ClassificationEvaluator,
        loops: Int,
        progressCallback: ClassificationProgressCallback? = null
    ): InferenceResult {
        return try {
            val model = classifier.configuration.model as ClassificationModel
            val prepareTime = Timeit.measure {
                classifier.prepare()
            }
            progressCallback?.onPrepared(prepareTime)
            benchmarker.addPreparation((prepareTime))
            for (i in 0 until loops) {
                val sample = sampler.next()
                progressCallback?.onNext(sample.image, i, loops)
                var prediction: FloatArray = floatArrayOf()
                val time = Timeit.measure {
                    prediction = classifier.predict(sample.image)
                }
                // TODO decrease loops count in case of too long execution
                val label = model.getLabelByPrediction(prediction)
                progressCallback?.onResult(label, time)
                benchmarker.addNext(time)
                evaluator.addNext(prediction, sample)
                if (Thread.interrupted())
                    throw InterruptedException()
            }
            SuccessResult(
                classifier.configuration,
                benchmarker.summarize(),
                evaluator.summarize()
            )
        }
//        } catch (e: RuntimeException) {
//            FailureResult(
//                classifier.configuration,
//                e.message ?: ""
//            )
//        }
        finally {
            classifier.release()
        }
    }
}