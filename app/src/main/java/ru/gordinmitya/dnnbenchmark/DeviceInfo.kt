package ru.gordinmitya.dnnbenchmark

import android.content.Context
import com.jaredrummler.android.device.DeviceName

class DeviceInfo private constructor(
    val uuid: String,
    val os: String,
    val manufacturer: String,
    val marketName: String,
    val model: String
) {
    companion object {
        fun obtain(uuid: String, context: Context): DeviceInfo {
            val info = DeviceName.getDeviceInfo(context)
            return DeviceInfo(
                uuid,
                "android",
                info.manufacturer,
                info.marketName,
                info.model
            )
        }
    }
}