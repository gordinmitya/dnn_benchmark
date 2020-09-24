package ru.gordinmitya.dnnbenchmark.worker

import android.content.Context
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.dnnbenchmark.benchmark.InferenceResult
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class WorkerThread {
    companion object {
        suspend fun execute(
            context: Context,
            configuration: Configuration,
            isGameLoop: Boolean
        ): InferenceResult = suspendCoroutine { continuation ->
            Thread {
                try {
                    val result = Worker.execute(context, configuration, isGameLoop)
                    continuation.resume(result)
                } catch (e: Throwable) {
                    throw e
                    // TODO deal with coroutines exceptions
//                    continuation.resumeWithException(e)
                }
            }.start()
        }
    }
}