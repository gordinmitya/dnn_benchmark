package ru.gordinmitya.dnnbenchmark.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.gordinmitya.dnnbenchmark.benchmark.InferenceResult
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ResultBroadcastReceiver(private val cont: Continuation<InferenceResult>) :
    BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.extras?.containsKey(WorkerProcess.DATA_KEY) == true) {
            val payload = intent.getParcelableExtra<InferenceResult>(WorkerProcess.DATA_KEY)!!
            cont.resume(payload)
        } else {
            val e = intent.getSerializableExtra(WorkerProcess.ERROR_KEY) as Throwable
            throw e
            // TODO deal with coroutines exceptions
            // continuation.resumeWithException(e)
        }

        context.unregisterReceiver(this)
    }
}