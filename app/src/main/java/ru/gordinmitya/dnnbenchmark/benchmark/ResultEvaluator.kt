package ru.gordinmitya.dnnbenchmark.benchmark

import ru.gordinmitya.dnnbenchmark.benchmark.PrecisionResult


interface ResultEvaluator {
    /**
     * Describe result.
     */
    fun summarize(): PrecisionResult
}