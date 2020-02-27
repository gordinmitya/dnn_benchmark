package ru.gordinmitya.dnnbenchmark.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.gordinmitya.dnnbenchmark.benchmark.InferenceResult
import ru.gordinmitya.dnnbenchmark.service.WorkerService
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class ResultBroadcastReceiver(private val cont: Continuation<InferenceResult>) :
    BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val payload = intent.getParcelableExtra<InferenceResult>(WorkerService.DATA_KEY)
        cont.resume(payload)
        context.unregisterReceiver(this)
    }
}