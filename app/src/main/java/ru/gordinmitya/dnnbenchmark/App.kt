package ru.gordinmitya.dnnbenchmark

import android.app.Application
import ru.gordinmitya.common.classification.MobileNet_v2
import ru.gordinmitya.mace.MACEFramework
import ru.gordinmitya.mnn.MNNFramework
import ru.gordinmitya.ncnn.NCNNFramework
import ru.gordinmitya.opencv.OpenCVFramework
import ru.gordinmitya.pytorch.PytorchFramework
import ru.gordinmitya.snpe.SNPEFramework
import ru.gordinmitya.tf_mobile.TFMobileFramework
import ru.gordinmitya.tflite.TFLiteFramework
import kotlin.time.MonoClock

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        @Suppress("SimplifyBooleanWithConstants")
        val DEBUG = false && BuildConfig.DEBUG

        lateinit var instance: Application

        val frameworks = listOf(
            MACEFramework,
            SNPEFramework,
            MNNFramework,
            TFLiteFramework,
            OpenCVFramework,
            TFMobileFramework,
            PytorchFramework,
            NCNNFramework
        )
        val models = listOf(
            MobileNet_v2
        )
    }
}