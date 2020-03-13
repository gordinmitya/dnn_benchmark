package ru.gordinmitya.dnnbenchmark.segmentation

import android.content.Context
import android.graphics.Bitmap
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.segmentation.SegmentationFramework
import ru.gordinmitya.common.segmentation.SegmentationModel
import ru.gordinmitya.common.segmentation.Segmentator
import ru.gordinmitya.dnnbenchmark.benchmark.Benchmarker
import ru.gordinmitya.dnnbenchmark.benchmark.FailureResult
import ru.gordinmitya.dnnbenchmark.benchmark.InferenceResult
import ru.gordinmitya.dnnbenchmark.benchmark.SuccessResult
import ru.gordinmitya.dnnbenchmark.model.ConfigurationEntity
import ru.gordinmitya.dnnbenchmark.utils.CyclicIterator
import ru.gordinmitya.dnnbenchmark.utils.ModelAssets
import ru.gordinmitya.dnnbenchmark.utils.Timeit
import ru.gordinmitya.dnnbenchmark.utils.toCyclicIterator

class SegmentationRunner(
    val context: Context,
    val configuration: Configuration,
    val loops: Int = 32,
    val failHard: Boolean = false
) {
    val KIKOFF_TIMEOUT = 10_000L
    val DECREASE_LOOPS_TIMEOUT = 5_000L
    val DECREASE_LOOPS_COUNT = 5

    private val framework: SegmentationFramework
    private val model: SegmentationModel
    private val benchmarker: Benchmarker
    private val evaluator: SegmentationEvaluator
    private val segmentator: Segmentator
    private val samples: CyclicIterator<String>

    init {
        framework = configuration.inferenceFramework as SegmentationFramework
        model = configuration.model as SegmentationModel

        benchmarker = Benchmarker()
        evaluator = SegmentationEvaluator()
        samples = ModelAssets.getImagesInSubFolders(context, model.samplesDir)
            .map {
                it.second
            }
            .toCyclicIterator()

        segmentator = framework.createSegmentator(context, configuration)
    }

    fun benchmark(): InferenceResult {
        return try {
            val prepareTime = Timeit.measure {
                segmentator.prepare()
            }
//            progressCallback?.onPrepared(prepareTime)
            benchmarker.addPreparation((prepareTime))
            for (i in 0 until loops) {
                // load sample
                val imagePath = samples.next()
                var image = ModelAssets.loadImage(context, imagePath)
                val originalImageSize = image.width to image.height
                image = Bitmap.createScaledBitmap(image, 257, 257, true)

//                progressCallback?.onNext(sample.image, i + 1, loops)
                var output: FloatArray = floatArrayOf()
                val time = Timeit.measure {
                    output = segmentator.predict(image)
                }

                // postprocessing
                var segmentation =
                    MaskUtils.convertMaskToBitmap(output, model, framework.getDataOrder())
                segmentation = Bitmap.createScaledBitmap(
                    segmentation,
                    originalImageSize.first,
                    originalImageSize.second,
                    true
                )

                // FIXME refactor
                val gtPath = imagePath
                    .replace("VOC", "VOC_GT")
                    .replace(".jpg", ".png")
                val gt = ModelAssets.loadImage(context, gtPath)
                evaluator.addNext(segmentation, gt)

//                progressCallback?.onResult(label, time)
                benchmarker.addNext(time)
//                evaluator.addNext(prediction, label, sample)
                if (Thread.interrupted())
                    throw InterruptedException()
                if (time >= KIKOFF_TIMEOUT)
                    break
                if (time >= DECREASE_LOOPS_TIMEOUT && i > DECREASE_LOOPS_COUNT)
                    break
            }
            SuccessResult(
                ConfigurationEntity(configuration),
                benchmarker.summarize(),
                evaluator.summarize()
            )
        } catch (e: RuntimeException) {
            if (failHard)
                throw e
            FailureResult(
                ConfigurationEntity(configuration),
                e.message ?: ""
            )
        } finally {
            segmentator.release()
        }
    }
}