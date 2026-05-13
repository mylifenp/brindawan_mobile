package com.brindawan.mobile.data

import com.brindawan.mobile.di.AppConfig
import klsoft.kotlin.multiplatform.keycloakclient.KeycloakClient
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull

class KeycloakAuthRepository(
    private val appConfig: AppConfig,
) {
    private val keycloakClient: KeycloakClient = KeycloakClient.Builder()
        .apply {
            if (appConfig.keycloakClientSecret.isNotBlank()) {
                setClientSecret(appConfig.keycloakClientSecret)
            }
        }
        .build(
            realmUrl = appConfig.keycloakRealmUrl,
            clientId = appConfig.keycloakClientId,
            redirectUri = appConfig.keycloakRedirectUri,
        )

    fun createAuthorizationLoginUrl(): String {
        return keycloakClient.createAuthorizationCodeLoginUrl(scope = appConfig.keycloakScope)
    }

    suspend fun loginWithAuthorizationCode(code: String): AuthSession {
        val tokenResponse = keycloakClient.fetchTokenByAuthorizationCode(code)
        if (tokenResponse.responseStatusCode !in 200..299) {
            val errorDescription = tokenResponse.data.string("error_description")
            val error = tokenResponse.data.string("error")
            val detail = listOfNotNull(error, errorDescription).joinToString(": ")
            throw IllegalStateException(
                "Token exchange failed (${tokenResponse.responseStatusCode})" +
                    if (detail.isNotBlank()) " — $detail" else ""
            )
        }

        val accessToken = tokenResponse.data.string("access_token")
            ?: throw IllegalStateException("Missing access_token in token response")

        val refreshToken = tokenResponse.data.string("refresh_token")
        val idToken = tokenResponse.data.string("id_token")
        val expiresIn = tokenResponse.data.long("expires_in")

        val userInfo = keycloakClient.fetchUserInfo(accessToken)
        val preferredUsername = if (userInfo.responseStatusCode in 200..299) {
            userInfo.data.string("preferred_username")
        } else {
            null
        }
        val email = if (userInfo.responseStatusCode in 200..299) {
            userInfo.data.string("email")
        } else {
            null
        }

        return AuthSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            idToken = idToken,
            expiresInSeconds = expiresIn,
            preferredUsername = preferredUsername,
            email = email,
        )
    }

    suspend fun logout(refreshToken: String?) {
        if (refreshToken.isNullOrBlank()) return
        keycloakClient.logout(refreshToken)
    }

    private fun JsonObject.string(key: String): String? {
        val primitive = this[key] as? JsonPrimitive ?: return null
        return primitive.content.takeUnless { it == "null" }
    }

    private fun JsonObject.long(key: String): Long? {
        return (this[key] as? JsonPrimitive)?.longOrNull
    }
}


