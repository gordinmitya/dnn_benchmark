package ru.gordinmitya.common.segmentation

import android.content.Context
import ru.gordinmitya.common.Configuration

interface SegmentationFramework {
    fun createSegmentator(
        context: Context,
        configuration: Configuration
    ): Segmentator
}