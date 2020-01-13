package ru.gordinmitya.common


interface ResultEvaluator {
    /**
     * Describe result.
     */
    fun summarize(): PrecisionResult
}