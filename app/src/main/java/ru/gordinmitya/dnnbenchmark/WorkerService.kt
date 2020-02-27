package ru.gordinmitya.dnnbenchmark

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.dnnbenchmark.benchmark.Benchmarker
import ru.gordinmitya.dnnbenchmark.benchmark.InferenceResult
import ru.gordinmitya.dnnbenchmark.classification.ClassificationEvaluator
import ru.gordinmitya.dnnbenchmark.classification.ClassificationRunner
import ru.gordinmitya.dnnbenchmark.model.ConfigurationEntity
import ru.gordinmitya.dnnbenchmark.utils.ModelAssets
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ResultBroadcastReceiver(private val cont: Continuation<InferenceResult>) :
    BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val payload = intent.getParcelableExtra<InferenceResult>(WorkerService.DATA_KEY)
        cont.resume(payload)
        context.unregisterReceiver(this)
    }
}


class WorkerService : Service() {
    val loops = 64

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val configuration = intent
            .getParcelableExtra<ConfigurationEntity>(CONFIGURATION_KEY)!!
            .toConfiguration()
        val isGameLoop = intent.getBooleanExtra(GAME_LOOP_KEY, false)

        val assets = ModelAssets(
            this,
            configuration.model as ClassificationModel
        )
        val classifier =
            configuration.inferenceFramework.createClassifier(this, configuration)
        val progressLogger = ProgressLogger(configuration) { str, replace ->

        }

        val result = ClassificationRunner.benchmark(
            classifier,
            assets,
            Benchmarker(),
            ClassificationEvaluator(),
            loops,
            progressLogger,
            App.DEBUG && !isGameLoop
        )

        val resIntent = Intent(RESULT_ACTION).also {
            it.putExtra(DATA_KEY, result)
        }
        sendBroadcast(resIntent)
        stopSelf()

        return START_NOT_STICKY
    }

    companion object {
        private const val RESULT_ACTION = BuildConfig.APPLICATION_ID + "WORKER_SERVICE_RESULT"
        const val CONFIGURATION_KEY = "CONFIGURATION_KEY"
        const val DATA_KEY = "DATA_KEY"
        const val GAME_LOOP_KEY = "GAME_LOOP_KEY"

        val resultIntentFilter = IntentFilter(RESULT_ACTION)

        suspend fun execute(
            context: Context,
            configuration: Configuration,
            isGameLoop: Boolean
        ): InferenceResult {
            return suspendCoroutine { continuation ->
                val receiver = ResultBroadcastReceiver(continuation)
                context.registerReceiver(receiver, resultIntentFilter)

                val intent = Intent(context, WorkerService::class.java).also {
                    it.putExtra(CONFIGURATION_KEY, ConfigurationEntity(configuration))
                    it.putExtra(GAME_LOOP_KEY, isGameLoop)
                }
                context.startService(intent)
            }
        }
    }
}