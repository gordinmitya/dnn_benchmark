package ru.gordinmitya.dnnbenchmark.worker

import android.content.Context
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.Task
import ru.gordinmitya.dnnbenchmark.App
import ru.gordinmitya.dnnbenchmark.benchmark.InferenceResult
import ru.gordinmitya.dnnbenchmark.classification.ClassificationRunner
import ru.gordinmitya.dnnbenchmark.segmentation.SegmentationRunner

class Worker {
    companion object {
        fun execute(
            context: Context,
            configuration: Configuration,
            isGameLoop: Boolean
        ): InferenceResult {
            val failHard = App.DEBUG && !isGameLoop

            return when (configuration.model.task) {
                Task.CLASSIFICATION -> ClassificationRunner(
                    context,
                    configuration,
                    failHard = failHard
                ).benchmark()
                Task.SEGMENTATION -> SegmentationRunner(
                    context,
                    configuration,
                    failHard = failHard
                ).benchmark()
            }
        }
    }
}