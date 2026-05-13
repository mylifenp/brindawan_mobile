package com.brindawan.mobile.data

data class AuthSession(
    val accessToken: String,
    val refreshToken: String?,
    val idToken: String?,
    val expiresInSeconds: Long?,
    val preferredUsername: String?,
    val email: String?,
)

