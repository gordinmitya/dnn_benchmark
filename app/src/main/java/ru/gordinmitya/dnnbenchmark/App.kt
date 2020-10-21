package ru.gordinmitya.dnnbenchmark

import android.app.Application
import ru.gordinmitya.common.InferenceFramework
import ru.gordinmitya.common.classification.MobileNetModel
import ru.gordinmitya.common.segmentation.DeepLabModel
import kotlin.reflect.KClass

class App : Application() {
    lateinit var frameworks: List<String>
    val models = listOf(
        MobileNetModel,
//        DeepLabModel
    )

    fun createFrameworkInstance(name: String): InferenceFramework {
        return Class.forName(name).newInstance() as InferenceFramework
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
//        val frameworkClassess = listOf(
//            NCNNFramework::class,
//            MNNFramework::class,
//            TFLiteFramework::class,
//            PytorchFramework::class,
//            OpenCVFramework::class,
//            ONNXFramework::class,
//            MACEFramework()::class,
//            TFMobileFramework()::class,
//        )
//        frameworks = frameworkClassess.map { it.simpleName!! to it.qualifiedName!! }
        frameworks = arrayListOf(
            "ru.gordinmitya.mnn.MNNFramework",
            "ru.gordinmitya.ncnn.NCNNFramework",
            "ru.gordinmitya.tflite.TFLiteFramework",
            "ru.gordinmitya.onnxruntime.ONNXFramework",
            "ru.gordinmitya.pytorch.PytorchFramework",
            "ru.gordinmitya.opencv.OpenCVFramework",
//            "ru.gordinmitya.mace.MACEFramework",
//            "ru.gordinmitya.tf_mobile.TFMobileFramework"
        )
    }

    @Suppress("SimplifyBooleanWithConstants")
    companion object {
        val DEBUG = true && BuildConfig.DEBUG
        val USE_PROCESS = true || !DEBUG
        val SEND_STATS = false || !DEBUG

        lateinit var instance: App

        fun describeFramework(kclass: KClass<out InferenceFramework>): String {
            return kclass.qualifiedName!!
        }
    }
}