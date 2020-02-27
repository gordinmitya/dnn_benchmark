package ru.gordinmitya.dnnbenchmark.benchmark

class Benchmarker {
    private var prepareTime = 0L
    private val history = ArrayList<Long>()

    fun addPreparation(time: Long) {
        prepareTime = time
    }

    fun addNext(time: Long) {
        history.add(time)
    }

    fun summarize(): BenchmarkResult {
        if (history.isEmpty())
            throw IllegalStateException("No benchmarks were performed")
        return BenchmarkResult.fromMeasurements(
            prepareTime,
            history
        )
    }
}