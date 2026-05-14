package com.brindawan.mobile.data

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

expect class SecureKeyValueStore {
    fun putString(key: String, value: String)
    fun getStringOrNull(key: String): String?
    fun remove(key: String)
}

expect fun createSecureKeyValueStore(): SecureKeyValueStore

expect fun readLegacySessionRaw(key: String): String?

expect fun clearLegacySessionRaw(key: String)

class AuthSessionStore(
    private val secureStore: SecureKeyValueStore = createSecureKeyValueStore(),
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val storageKey = "auth.session"

    fun save(session: AuthSession) {
        secureStore.putString(storageKey, json.encodeToString(session))
    }

    fun load(): AuthSession? {
        val secureRaw = secureStore.getStringOrNull(storageKey)
        if (secureRaw != null) {
            return runCatching { json.decodeFromString<AuthSession>(secureRaw) }.getOrNull()
        }

        val legacyRaw = readLegacySessionRaw(storageKey) ?: return null
        val migrated = runCatching { json.decodeFromString<AuthSession>(legacyRaw) }.getOrNull() ?: return null

        // One-time migration from legacy plain storage into secure storage.
        save(migrated)
        clearLegacySessionRaw(storageKey)
        return migrated
    }

    fun clear() {
        secureStore.remove(storageKey)
    }
}


