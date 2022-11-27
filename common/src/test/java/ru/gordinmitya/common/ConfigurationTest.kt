package ru.gordinmitya.common

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class ConfigurationTest {
    private fun createFramework(): InferenceFramework {
        val models = listOf<Model>(
            mockk(),
            mockk()
        )
        val types = listOf<InferenceType>(
            mockk(),
            mockk()
        )
        return object : InferenceFramework("", Version("?")) {
            override fun getInferenceTypes(): List<InferenceType> = types

            override fun getModels(): List<Model> = models
        }
    }

    @Test
    fun canCreateValid() {
        val framework = createFramework()

        assertDoesNotThrow {
            framework.getInferenceTypes()
                .zip(framework.getModels())
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
            Configuration(framework, framework.getInferenceTypes()[0], otherModel)
        }
        assertThrows(AssertionError::class.java) {
            Configuration(framework, otherInferenceType, framework.getModels()[0])
        }
    }
}
