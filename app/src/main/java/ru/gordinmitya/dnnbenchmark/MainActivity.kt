package ru.gordinmitya.dnnbenchmark

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.gordinmitya.common.Model
import ru.gordinmitya.mnn.MNNFramework
import ru.gordinmitya.ncnn.NCNNFramework
import ru.gordinmitya.tflite.TFLiteFramework


class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textView = TextView(this).also {
            it.movementMethod = ScrollingMovementMethod()
            it.isVerticalScrollBarEnabled = true
            it.setHorizontallyScrolling(true)
        }
        setContentView(textView)

        doit()
    }

    val logBuilder = StringBuilder()
    private fun log(msg: String) = textView.post {
        logBuilder.append(msg)
        logBuilder.append("\n\n")
        textView.text = logBuilder
    }

    val loops = 48
    val sleep = 10_000L
    private fun doit() {
        Thread {
            while (true) {
                val tflite = TFLiteFramework.inferenceTypes.map { type ->
                    TFLiteFramework.benchmark(
                        App.instance,
                        Model.mobilenet_v2,
                        type,
                        loops
                    )
                }.joinToString("\n")

                log(tflite)
                Thread.sleep(sleep)

                val ncnn = NCNNFramework.inferenceTypes.map { type ->
                    NCNNFramework.benchmark(
                        App.instance,
                        Model.mobilenet_v2,
                        type,
                        loops
                    )
                }.joinToString("\n")

                log(ncnn)
                Thread.sleep(sleep)

                val mnn = MNNFramework.inferenceTypes.map { type ->
                    MNNFramework.benchmark(
                        App.instance,
                        Model.mobilenet_v2,
                        type,
                        loops
                    )
                }.joinToString("\n")

                log(mnn)
                log("–".repeat(8))
                Thread.sleep(sleep)
            }
        }.start()
    }
}
