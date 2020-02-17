package ru.gordinmitya.dnnbenchmark

import android.content.Context
import com.jaredrummler.android.device.DeviceName
import java.util.*

class DeviceInfo private constructor(
    val uuid: String,
    val os: String,
    val manufacturer: String,
    val marketName: String,
    val model: String
) {
    companion object {
        private const val SHARED_PREF = BuildConfig.APPLICATION_ID + ".DEVICE_INFO_PREFERENCE"
        private const val UUID_KEY = "uuid"

        fun generateUUID() = UUID.randomUUID().toString()

        fun obtain(context: Context): DeviceInfo {
            val prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
            if (!prefs.contains(UUID_KEY)) {
                prefs.edit()
                    .putString(UUID_KEY, generateUUID())
                    .apply()
            }
            val uuid = prefs.getString(UUID_KEY, null)
                ?: throw IllegalStateException("impossible to happen")

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