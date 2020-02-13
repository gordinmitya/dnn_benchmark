package ru.gordinmitya.dnnbenchmark

import android.content.Context

class DeviceInfo private constructor(
    val uuid: String,
    val os: String,
    val manufacturer: String,
    val marketName: String,
    val model: String
){

    companion object {
        fun obtain(context: Context): DeviceInfo {
            return DeviceInfo(
                "1",
                "android",
                "a",
                "b",
                "c"
            )
        }
    }
}