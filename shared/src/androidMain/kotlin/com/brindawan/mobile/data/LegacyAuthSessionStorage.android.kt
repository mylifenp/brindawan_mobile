package com.brindawan.mobile.data

import android.content.Context

actual fun readLegacySessionRaw(key: String): String? {
    val context = AndroidSecureStoreInitializer.requireContext()
    val legacyPrefs = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
    return legacyPrefs.getString(key, null)
}

actual fun clearLegacySessionRaw(key: String) {
    val context = AndroidSecureStoreInitializer.requireContext()
    val legacyPrefs = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
    legacyPrefs.edit().remove(key).apply()
}

