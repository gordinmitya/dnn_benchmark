package ru.gordinmitya.common.segmentation

import android.content.Context
import ru.gordinmitya.common.Configuration
import ru.gordinmitya.common.DataOrder

interface SegmentationFramework {
    fun createSegmentator(
        context: Context,
        configuration: Configuration
    ): Segmentator

    fun getDataOrder(): DataOrder
}