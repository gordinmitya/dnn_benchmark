package ru.gordinmitya.dnnbenchmark

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.gordinmitya.common.Model
import ru.gordinmitya.mnn.MNNFramework
import ru.gordinmitya.ncnn.NCNNFramework


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

    private fun doit() {
        Thread {
            val loops = 48

            textView.post {
                textView.text = "benchmarking…"
            }

            val ncnn = NCNNFramework.inferenceTypes.map { type ->
                NCNNFramework.benchmark(
                    App.instance,
                    Model.mobilenet_v2,
                    type,
                    loops
                )
            }.joinToString("\n")

            val mnn = MNNFramework.inferenceTypes.map { type ->
                MNNFramework.benchmark(
                    App.instance,
                    Model.mobilenet_v2,
                    type,
                    loops
                )
            }.joinToString("\n")

            textView.post {
                textView.text = "$ncnn\n\n$mnn"
            }
        }.start()
    }
}
