package com.brindawan.mobile.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private const val SECURE_PREFS_FILE = "secure_auth_session"

object AndroidSecureStoreInitializer {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    internal fun requireContext(): Context {
        return checkNotNull(appContext) {
            "AndroidSecureStoreInitializer.init(context) must be called before using secure storage"
        }
    }
}

actual class SecureKeyValueStore internal constructor(
    private val sharedPreferences: SharedPreferences,
) {
    actual fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    actual fun getStringOrNull(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    actual fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}

actual fun createSecureKeyValueStore(): SecureKeyValueStore {
    val context = AndroidSecureStoreInitializer.requireContext()
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    val prefs = EncryptedSharedPreferences.create(
        context,
        SECURE_PREFS_FILE,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )
    return SecureKeyValueStore(prefs)
}

