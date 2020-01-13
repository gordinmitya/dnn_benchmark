package ru.gordinmitya.common.classification

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ClassificationModelTest {

    @Test
    fun getLabelByPrediction() {
        val labels = listOf("one", "two", "three")
        val prediction = floatArrayOf(.4f, .5f, .1f)

        val model = object : ClassificationModel("", "", labels, emptyList()) {}

        val result = model.getLabelByPrediction(prediction)

        assertEquals("two", result)
    }
}