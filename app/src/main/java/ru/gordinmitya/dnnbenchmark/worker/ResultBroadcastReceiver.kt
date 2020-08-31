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
        val exception = intent.getSerializableExtra(WorkerProcess.ERROR_KEY) as Throwable?
        if (exception != null)
            cont.resumeWithException(exception)

        val payload = intent.getParcelableExtra<InferenceResult>(WorkerProcess.DATA_KEY)!!
        cont.resume(payload)
        context.unregisterReceiver(this)
    }
}