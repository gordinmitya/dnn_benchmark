package ru.gordinmitya.common

import android.content.Context
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import ru.gordinmitya.common.classification.Classifier

internal class ConfigurationTest {
    private fun createFramework(): InferenceFramework {
        val models = listOf(
            object : Model("model 1", "") {},
            object : Model("model 2", "") {}
        )
        val types = listOf(
            InferenceType("type 1"),
            InferenceType("type 2")
        )
        return object : InferenceFramework("", "") {
            override val models: List<Model>
                get() = models
            override val inferenceTypes: List<InferenceType>
                get() = types

            override fun createClassifier(
                context: Context,
                configuration: Configuration
            ): Classifier = mockk()
        }
    }

    @Test
    fun canCreateValid() {
        val framework = createFramework()

        assertDoesNotThrow {
            framework.inferenceTypes
                .zip(framework.models)
                .forEach {
                    Configuration(framework, it.first, it.second)
                }
        }
    }

    @Test
    fun cantCreateInvalid() {
        val otherModel = mockk<Model>()
        val otherInferenceType = mockk<InferenceType>()
        val framework = createFramework()

        assertThrows(AssertionError::class.java) {
            Configuration(framework, framework.inferenceTypes[0], otherModel)
        }
        assertThrows(AssertionError::class.java) {
            Configuration(framework, otherInferenceType, framework.models[0])
        }
    }
}