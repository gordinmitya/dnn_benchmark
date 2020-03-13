package ru.gordinmitya.dnnbenchmark

import android.app.Application
import ru.gordinmitya.common.InferenceFramework
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.MobileNetModel
import ru.gordinmitya.common.segmentation.DeepLabModel
import ru.gordinmitya.mace.MACEFramework
import ru.gordinmitya.mnn.MNNFramework
import ru.gordinmitya.ncnn.NCNNFramework
import ru.gordinmitya.opencv.OpenCVFramework
import ru.gordinmitya.pytorch.PytorchFramework
import ru.gordinmitya.snpe.SNPEFramework
import ru.gordinmitya.tf_mobile.TFMobileFramework
import ru.gordinmitya.tflite.TFLiteFramework

class App : Application() {
    lateinit var frameworks: List<InferenceFramework>
    val models = listOf(DeepLabModel, MobileNetModel)

    override fun onCreate() {
        super.onCreate()
        instance = this
        frameworks = listOf(
            MNNFramework(),
            TFLiteFramework(),
            MACEFramework(),
            SNPEFramework(this),
            OpenCVFramework(),
            TFMobileFramework(),
            PytorchFramework(),
            NCNNFramework()
        )
    }

    companion object {
        @Suppress("SimplifyBooleanWithConstants")
        val DEBUG = false && BuildConfig.DEBUG

        lateinit var instance: App
    }
}