package ru.gordinmitya.dnnbenchmark

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.gordinmitya.common.InferenceFramework
import ru.gordinmitya.common.Model
import ru.gordinmitya.tflite.TFLiteFramework
import ru.gordinmitya.ncnn.NCNNFramework
import ru.gordinmitya.tf_mobile.TFMobileFramework
import ru.gordinmitya.mnn.MNNFramework

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

    val logBuilder = StringBuilder()
    private fun log(msg: String) = textView.post {
        logBuilder.append(msg)
        logBuilder.append("\n")
        textView.text = logBuilder
    }

    val loops = 48
    val sleep = 1_000L

    private fun <T : InferenceFramework> run(f: T) {
        f.inferenceTypes.map { type ->
            f.benchmark(
                App.instance,
                Model.mobilenet_v2,
                type,
                loops
            )
        }.forEach {
            log(it.toString())
            Thread.sleep(sleep)
        }
    }

    private fun doit() {
        Thread {
            while (true) {
                run(TFLiteFramework)
                run(TFMobileFramework)
                run(NCNNFramework)
                run(MNNFramework)
                log("\n" + "–".repeat(8) + "\n")
            }
        }.start()
    }
}
