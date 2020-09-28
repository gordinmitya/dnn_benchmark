package ru.gordinmitya.dnnbenchmark

import android.app.Application
import ru.gordinmitya.common.InferenceFramework
import ru.gordinmitya.common.Model
import ru.gordinmitya.common.classification.MobileNetModel
import ru.gordinmitya.common.segmentation.DeepLabModel
import ru.gordinmitya.mace.MACEFramework
import ru.gordinmitya.mnn.MNNFramework
import ru.gordinmitya.ncnn.NCNNFramework
import ru.gordinmitya.onnxruntime.ONNXFramework
import ru.gordinmitya.opencv.OpenCVFramework
import ru.gordinmitya.pytorch.PytorchFramework
import ru.gordinmitya.snpe.SNPEFramework
import ru.gordinmitya.tf_mobile.TFMobileFramework
import ru.gordinmitya.tflite.TFLiteFramework
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class App : Application() {
    private lateinit var frameworkClassess: List<KClass<out InferenceFramework>>
    lateinit var frameworks: List<String>
    val models = listOf(
        MobileNetModel,
        DeepLabModel
    )

    fun createFrameworkInstance(name: String): InferenceFramework {
        val kclass = frameworkClassess.first { describeFramework(it) == name }
        return kclass.createInstance()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        frameworkClassess = listOf(
            MNNFramework::class,
            NCNNFramework::class,
            TFLiteFramework::class,
            PytorchFramework::class,
            OpenCVFramework::class,
            ONNXFramework::class,
//            MACEFramework(),
//            SNPEFramework(),
//            TFMobileFramework(),
        )
        frameworks = frameworkClassess.map { describeFramework(it) }
    }

    @Suppress("SimplifyBooleanWithConstants")
    companion object {
        val DEBUG = true && BuildConfig.DEBUG
        val USE_PROCESS = true || !DEBUG

        lateinit var instance: App

        fun describeFramework(kclass: KClass<out InferenceFramework>): String {
            return kclass.simpleName!!
        }
    }
}