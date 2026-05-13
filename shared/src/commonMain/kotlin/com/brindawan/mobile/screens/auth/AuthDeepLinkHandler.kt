package com.brindawan.mobile.screens.auth

import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object AuthDeepLinkHandler {
    private val authorizationCodes = MutableSharedFlow<String>(extraBufferCapacity = 1)

    fun onRedirectUrl(url: String): Boolean {
        val parsedUrl = runCatching { Url(url) }.getOrNull() ?: return false
        val code = parsedUrl.parameters["code"] ?: return false
        return authorizationCodes.tryEmit(code)
    }

    fun authorizationCodeEvents(): SharedFlow<String> = authorizationCodes
}

fun handleAuthRedirect(url: String): Boolean {
    return AuthDeepLinkHandler.onRedirectUrl(url)
}

