package ru.gordinmitya.dnnbenchmark

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.Task
import ru.gordinmitya.dnnbenchmark.benchmark.InferenceResult
import ru.gordinmitya.dnnbenchmark.benchmark.NotSupportedResult
import ru.gordinmitya.dnnbenchmark.model.ConfigurationEntity
import ru.gordinmitya.dnnbenchmark.model.DeviceInfo
import ru.gordinmitya.dnnbenchmark.model.Measurement

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    /*
        Then launched on Firebase TestLab will be set to true.
        After executing all benchmarks will finish activity.
        Thanks to "com.google.intent.action.TEST_LOOP" intent action.
     */
    private var isGameLoop = false

    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isGameLoop = intent.action == "com.google.intent.action.TEST_LOOP"

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

    val sleep = 1_000L

    @ObsoleteCoroutinesApi
    private fun doit() {
        val configurations = ArrayList<Configuration>()
        for (framework in App.instance.frameworks) {
            for (model in framework.getModels()) {
                if (model.task != Task.CLASSIFICATION) continue
                for (type in framework.getInferenceTypes()) {
                    val configuration = Configuration(framework, type, model)
                    configurations.add(configuration)
                }
            }
        }
        GlobalScope.launch(newSingleThreadContext("WorkerThread")) {
            val activity = this@MainActivity
            val results = ArrayList<InferenceResult>()
            delay(sleep)
            configurations.forEach { configuration ->
                if (!configuration.inferenceType.isSupported) {
                    results.add(NotSupportedResult(ConfigurationEntity(configuration)))
                    return@forEach
                }
                val result = WorkerService.execute(activity, configuration, isGameLoop)
                results.add(result)
                log(result.toString())
                delay(sleep)
            }
            log("\n" + "–".repeat(8) + "\n")

            if (App.DEBUG) return@launch

            log("sending to server…")
            val userUid: String
            try {
                val auth = FirebaseAuth.getInstance()
                val authResult = auth.signInAnonymously().await()
                userUid = authResult.user!!.uid
            } catch (e: Exception) {
                Log.e(TAG, "signInAnonymously:failure", e)
                log("Firebase authentication failed.")
                log("Unable to send data.")
                return@launch
            }

            val device = DeviceInfo.obtain(userUid, activity)
            val measurement = Measurement(device, results)

            try {
                Firebase.firestore
                    .collection("measurements")
                    .add(measurement)
                    .await()
                log("Data has been sent.", true)
            } catch (e: Exception) {
                Log.e(TAG, "firestore:failure", e)
                log("Failed to send data.\n MSG: ${e.message}")
                return@launch
            }
        }.invokeOnCompletion {
            log("\n" + "–".repeat(8) + "\n")
            if (isGameLoop)
                finish()
        }
    }
}
