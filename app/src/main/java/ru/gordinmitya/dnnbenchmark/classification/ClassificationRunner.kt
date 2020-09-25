package ru.gordinmitya.dnnbenchmark.classification

import android.content.Context
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.classification.ClassificationFramework
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.Classifier
import ru.gordinmitya.dnnbenchmark.benchmark.Benchmarker
import ru.gordinmitya.dnnbenchmark.benchmark.FailureResult
import ru.gordinmitya.dnnbenchmark.benchmark.InferenceResult
import ru.gordinmitya.dnnbenchmark.benchmark.SuccessResult
import ru.gordinmitya.dnnbenchmark.model.ConfigurationEntity
import ru.gordinmitya.dnnbenchmark.utils.Timeit

class ClassificationRunner(
    context: Context,
    val configuration: Configuration,
    val loops: Int = 32,
    val progressCallback: ClassificationProgressCallback? = null,
    val failHard: Boolean = false
) {
    val KIKOFF_TIMEOUT = 10_000L
    val DECREASE_LOOPS_TIMEOUT = 5_000L
    val DECREASE_LOOPS_COUNT = 5

    private val benchmarker: Benchmarker
    private val evaluator: ClassificationEvaluator
    private val modelAssets: ModelAssets
    private val classifier: Classifier

    init {
        benchmarker = Benchmarker()
        evaluator = ClassificationEvaluator()
        modelAssets = ModelAssets(context, configuration.model as ClassificationModel)

        val framework = configuration.inferenceFramework as ClassificationFramework
        classifier = framework.createClassifier(context, configuration)
    }

    fun benchmark(): InferenceResult {
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
                ConfigurationEntity(classifier.configuration),
                benchmarker.summarize(),
                evaluator.summarize()
            )
        } catch (e: RuntimeException) {
            if (failHard)
                throw e
            FailureResult(
                ConfigurationEntity(classifier.configuration),
                e.message ?: ""
            )
        } finally {
            classifier.release()
        }
    }
}