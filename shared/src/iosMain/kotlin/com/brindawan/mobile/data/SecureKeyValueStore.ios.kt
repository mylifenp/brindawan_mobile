package com.brindawan.mobile.data

import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.ExperimentalSettingsImplementation

private const val KEYCHAIN_SERVICE = "com.brindawan.mobile.auth"

@OptIn(ExperimentalSettingsImplementation::class)
actual class SecureKeyValueStore {
    private val keychainSettings = KeychainSettings(KEYCHAIN_SERVICE)

    actual fun putString(key: String, value: String) {
        keychainSettings.putString(key, value)
    }

    actual fun getStringOrNull(key: String): String? {
        return keychainSettings.getStringOrNull(key)
    }

    actual fun remove(key: String) {
        keychainSettings.remove(key)
    }
}

actual fun createSecureKeyValueStore(): SecureKeyValueStore = SecureKeyValueStore()



