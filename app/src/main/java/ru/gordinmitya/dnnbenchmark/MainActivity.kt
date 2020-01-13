package ru.gordinmitya.dnnbenchmark

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.gordinmitya.common.Benchmarker
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.Task
import ru.gordinmitya.common.classification.ClassificationEvaluator
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.common.classification.ClassificationRunner
import ru.gordinmitya.common.classification.GTSampler
import ru.gordinmitya.mnn.MNNFramework
import ru.gordinmitya.ncnn.NCNNFramework
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
    private fun log(msg: String) = textView.post {
        logBuilder.append(msg)
        logBuilder.append("\n")
        textView.text = logBuilder
    }

    val loops = 48
    val sleep = 1_000L

    private fun doit() {
        val frameworks = listOf(
//            TFMobileFramework,
//            TFLiteFramework,
//            MNNFramework,
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
            for (i in 0 until 3) {
                configurations
                    .map { configuration ->
                        val model = configuration.model as ClassificationModel
                        val classifier =
                            configuration.inferenceFramework.createClassifier(this, configuration)
                        ClassificationRunner.benchmark(
                            classifier,
                            GTSampler(model.truths),
                            Benchmarker(),
                            ClassificationEvaluator(),
                            loops
                        )
                    }
                    .forEach {
                        log(it.toString())
                        Thread.sleep(sleep)
                    }
                log("\n" + "–".repeat(8) + "\n")
            }
        }.start()
    }
}
