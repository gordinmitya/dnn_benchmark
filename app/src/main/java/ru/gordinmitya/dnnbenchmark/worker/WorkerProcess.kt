package ru.gordinmitya.dnnbenchmark.worker

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.dnnbenchmark.BuildConfig
import ru.gordinmitya.dnnbenchmark.benchmark.InferenceResult
import ru.gordinmitya.dnnbenchmark.model.ConfigurationEntity
import kotlin.coroutines.suspendCoroutine

class WorkerProcess : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val resIntent = Intent(RESULT_ACTION)
        try {
            val configuration = intent
                .getParcelableExtra<ConfigurationEntity>(CONFIGURATION_KEY)!!
                .toConfiguration()
            val isGameLoop = intent.getBooleanExtra(GAME_LOOP_KEY, false)

            val result = Worker.execute(this, configuration, isGameLoop)

            resIntent.putExtra(DATA_KEY, result)
        } catch (e: Throwable) {
            resIntent.putExtra(ERROR_KEY, e)
        }

        sendBroadcast(resIntent)
        stopSelf()

        return START_NOT_STICKY
    }

    companion object {
        private const val RESULT_ACTION = BuildConfig.APPLICATION_ID + "WORKER_SERVICE_RESULT"
        const val CONFIGURATION_KEY = "CONFIGURATION_KEY"
        const val DATA_KEY = "DATA_KEY"
        const val ERROR_KEY = "ERROR_KEY"
        const val GAME_LOOP_KEY = "GAME_LOOP_KEY"

        private val resultIntentFilter = IntentFilter(RESULT_ACTION)

        suspend fun execute(
            context: Context,
            configuration: Configuration,
            isGameLoop: Boolean
        ): InferenceResult = suspendCoroutine { continuation ->
            val receiver =
                ResultBroadcastReceiver(
                    continuation
                )
            context.registerReceiver(
                receiver,
                resultIntentFilter
            )

            val intent = Intent(context, WorkerProcess::class.java).also {
                it.putExtra(CONFIGURATION_KEY, ConfigurationEntity(configuration))
                it.putExtra(GAME_LOOP_KEY, isGameLoop)
            }
            context.startService(intent)
        }
    }
}