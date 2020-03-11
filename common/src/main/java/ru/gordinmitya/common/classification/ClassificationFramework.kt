package ru.gordinmitya.common.classification

import android.content.Context
import ru.gordinmitya.common.Configuration

interface ClassificationFramework {
    fun createClassifier(
        context: Context,
        configuration: Configuration
    ): Classifier
}