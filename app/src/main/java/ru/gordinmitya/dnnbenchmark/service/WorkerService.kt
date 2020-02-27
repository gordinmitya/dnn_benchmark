package ru.gordinmitya.dnnbenchmark.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.content.ContextCompat
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.classification.ClassificationModel
import ru.gordinmitya.dnnbenchmark.App
import ru.gordinmitya.dnnbenchmark.BuildConfig
import ru.gordinmitya.dnnbenchmark.ProgressLogger
import ru.gordinmitya.dnnbenchmark.benchmark.Benchmarker
import ru.gordinmitya.dnnbenchmark.benchmark.InferenceResult
import ru.gordinmitya.dnnbenchmark.classification.ClassificationEvaluator
import ru.gordinmitya.dnnbenchmark.classification.ClassificationRunner
import ru.gordinmitya.dnnbenchmark.model.ConfigurationEntity
import ru.gordinmitya.dnnbenchmark.utils.ModelAssets
import kotlin.coroutines.suspendCoroutine

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

        NotificationHelper.createNotificationChannel(this)
        val body = configuration.run {
            "${configuration.model.name} ${inferenceFramework.name} ${inferenceType.name}"
        }
        val notification = NotificationHelper.createNotification(this, body)
        startForeground(FOREGROUND_ID, notification)

        val result = execute(configuration, isGameLoop)

        val resIntent = Intent(RESULT_ACTION).also {
            it.putExtra(DATA_KEY, result)
        }
        sendBroadcast(resIntent)

        stopSelf()

        return START_NOT_STICKY
    }

    fun execute(configuration: Configuration, isGameLoop: Boolean): InferenceResult {
        val assets = ModelAssets(
            this,
            configuration.model as ClassificationModel
        )
        val classifier =
            configuration.inferenceFramework.createClassifier(this, configuration)
        val progressLogger =
            ProgressLogger(configuration) { str, replace ->

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

        return result
    }

    companion object {
        const val FOREGROUND_ID = 1
        private const val RESULT_ACTION = BuildConfig.APPLICATION_ID + ".WORKER_SERVICE_RESULT"
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
                val receiver =
                    ResultBroadcastReceiver(
                        continuation
                    )
                context.registerReceiver(
                    receiver,
                    resultIntentFilter
                )

                val intent = Intent(context, WorkerService::class.java).also {
                    it.putExtra(CONFIGURATION_KEY, ConfigurationEntity(configuration))
                    it.putExtra(GAME_LOOP_KEY, isGameLoop)
                }
                ContextCompat.startForegroundService(context, intent)
            }
        }
    }
}