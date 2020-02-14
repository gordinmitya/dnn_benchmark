package ru.gordinmitya.dnnbenchmark

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.gordinmitya.common.Benchmarker
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.Task
import ru.gordinmitya.common.classification.ClassificationEvaluator
import ru.gordinmitya.common.classification.ClassificationRunner
import ru.gordinmitya.common.classification.MobileNet_v2
import ru.gordinmitya.common.classification.ModelAssets
import ru.gordinmitya.mace.MACEFramework
import ru.gordinmitya.mnn.MNNFramework
import ru.gordinmitya.ncnn.NCNNFramework
import ru.gordinmitya.opencv.OpenCVFramework
import ru.gordinmitya.pytorch.PytorchFramework
import ru.gordinmitya.tf_mobile.TFMobileFramework
import ru.gordinmitya.tflite.TFLiteFramework

class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textView = TextView(this).also {
            it.movementMethod = ScrollingMovementMethod()
            it.isVerticalScrollBarEnabled = true
            it.setHorizontallyScrolling(true)

            it.isEnabled = true
            it.isLongClickable = true
            it.setTextIsSelectable(true)
        }
        setContentView(textView)

        doit()
    }

    private val logBuilder = StringBuilder()
    private fun log(msg: String, replace: Boolean = false) {
        if (replace) {
            val start = logBuilder.lastIndexOf("\n", logBuilder.length - 2)
            if (start > 0)
                logBuilder.delete(start + 1, logBuilder.length)
            else
                logBuilder.clear()
        }
        logBuilder.append(msg)
        logBuilder.append("\n")
        textView.post {
            textView.text = logBuilder
        }
    }

    val loops = 48
    val sleep = 1_000L

    private fun doit() {
        val frameworks = listOf(
            MACEFramework,
            MNNFramework,
            TFLiteFramework,
            OpenCVFramework,
            TFMobileFramework,
            PytorchFramework,
            NCNNFramework
        )
        val configurations = ArrayList<Configuration>()
        for (framework in frameworks) {
            for (model in framework.models) {
                if (model.task != Task.CLASSIFICATION) continue
                for (type in framework.inferenceTypes) {
                    val configuration = Configuration(framework, type, model)
                    configurations.add(configuration)
                }
            }
        }
        Thread {
            val assets = ModelAssets(this, MobileNet_v2)
            configurations.forEach { configuration ->
                val classifier =
                    configuration.inferenceFramework.createClassifier(this, configuration)
                val progressLogger = ProgressLogger(configuration, this::log)
                val result = ClassificationRunner.benchmark(
                    classifier,
                    assets,
                    Benchmarker(),
                    ClassificationEvaluator(),
                    loops,
                    progressLogger,
                    App.DEBUG
                )
                log(result.toString(), true)
                Thread.sleep(sleep)
            }
            log("\n" + "–".repeat(8) + "\n")
        }.start()
    }
}
