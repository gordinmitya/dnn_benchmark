package ru.gordinmitya.dnnbenchmark

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.dnnbenchmark.benchmark.InferenceResult
import ru.gordinmitya.dnnbenchmark.benchmark.NotSupportedResult
import ru.gordinmitya.dnnbenchmark.model.ConfigurationEntity
import ru.gordinmitya.dnnbenchmark.model.DeviceInfo
import ru.gordinmitya.dnnbenchmark.model.Measurement
import ru.gordinmitya.dnnbenchmark.utils.TextLogger
import ru.gordinmitya.dnnbenchmark.worker.WorkerProcess
import ru.gordinmitya.dnnbenchmark.worker.WorkerThread

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    /*
        Then launched on Firebase TestLab will be set to true.
        After executing all benchmarks will finish activity.
        Thanks to "com.google.intent.action.TEST_LOOP" intent action.
     */
    private var isGameLoop = false

    lateinit var logger: TextLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isGameLoop = intent.action == "com.google.intent.action.TEST_LOOP"

        val textView = TextView(this).also {
            it.movementMethod = ScrollingMovementMethod()
            it.isVerticalScrollBarEnabled = true
            it.setHorizontallyScrolling(true)

            it.isEnabled = true
            it.isLongClickable = true
            it.setTextIsSelectable(true)
        }
        setContentView(textView)

        logger = TextLogger {
            textView.post { textView.text = it }
        }

        doit()
    }

    val sleep = 1_000L

    private fun generateConfigurations(): ArrayList<Configuration> {
        val configurations = ArrayList<Configuration>()
        for (model in App.instance.models) {
            for (framework in App.instance.frameworks) {
                if (!framework.getModels().contains(model)) continue
                for (type in framework.getInferenceTypes()) {
                    val configuration = Configuration(framework, type, model)
                    configurations.add(configuration)
                }
            }
        }
        return configurations
    }

    private fun doit() = GlobalScope.launch {
        val configurations = generateConfigurations()

        val activity = this@MainActivity
        val results = ArrayList<InferenceResult>()
        logger.log("${getString(R.string.app_name)} version ${BuildConfig.VERSION_NAME}")
        delay(sleep)
        configurations.forEach { configuration ->
            logger.spoiler("Running with ${configuration.inferenceFramework} on ${configuration.inferenceType}…")
            val result =
                if (!configuration.inferenceType.isSupported) {
                    NotSupportedResult(ConfigurationEntity(configuration))
                } else {
                    // in order to easily debug and see logs
                    if (App.USE_PROCESS)
                        WorkerProcess.execute(activity, configuration, isGameLoop)
                    else
                        WorkerThread.execute(activity, configuration, isGameLoop)
                }
            results.add(result)
            logger.log(result)
            delay(sleep)
        }
        logger.drawLine()

        if (App.DEBUG) return@launch

        logger.spoiler("sending to server…")
        val userUid: String
        try {
            val auth = FirebaseAuth.getInstance()
            val authResult = auth.signInAnonymously().await()
            userUid = authResult.user!!.uid
        } catch (e: Exception) {
            Log.e(TAG, "signInAnonymously:failure", e)
            logger.log("Firebase authentication failed.\nUnable to send data.")
            return@launch
        }

        val device = DeviceInfo.obtain(userUid, activity)
        val measurement = Measurement(device, results)

        try {
            Firebase.firestore
                .collection("measurements")
                .add(measurement)
                .await()
            logger.log("Data has been sent.")
        } catch (e: Exception) {
            Log.e(TAG, "firestore:failure", e)
            logger.log("Failed to send data.\n MSG: ${e.message}")
            return@launch
        }
    }.invokeOnCompletion {
        logger.log("\nThat's all folks!")
        if (isGameLoop)
            finish()
    }
}
