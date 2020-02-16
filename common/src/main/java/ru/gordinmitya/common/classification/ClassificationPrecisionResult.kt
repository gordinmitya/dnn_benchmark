package ru.gordinmitya.common.classification

import ru.gordinmitya.common.PrecisionResult

class ClassificationPrecisionResult(
    val errors: Double
) : PrecisionResult() {
    override fun toString(): String {
        return "error ${errors * 100}%"
    }
}