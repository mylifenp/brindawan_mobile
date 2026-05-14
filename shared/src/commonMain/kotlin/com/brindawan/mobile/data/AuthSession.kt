package com.brindawan.mobile.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthSession(
    val accessToken: String,
    val refreshToken: String?,
    val idToken: String?,
    val expiresInSeconds: Long?,
    val preferredUsername: String?,
    val email: String?,
)

