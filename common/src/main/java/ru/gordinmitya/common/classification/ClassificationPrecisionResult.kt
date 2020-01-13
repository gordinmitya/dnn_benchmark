package ru.gordinmitya.common.classification

import ru.gordinmitya.common.PrecisionResult

class ClassificationPrecisionResult(
    val diff: Float
) : PrecisionResult() {
    override fun toString(): String {
        return "max diff=$diff"
    }
}