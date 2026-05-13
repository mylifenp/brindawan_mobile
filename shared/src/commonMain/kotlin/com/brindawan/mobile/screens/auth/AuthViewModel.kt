package com.brindawan.mobile.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brindawan.mobile.data.AuthSession
import com.brindawan.mobile.data.KeycloakAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val authorizationCode: String = "",
    val loginUrl: String? = null,
    val session: AuthSession? = null,
    val errorMessage: String? = null,
)

class AuthViewModel(
    private val keycloakAuthRepository: KeycloakAuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            AuthDeepLinkHandler.authorizationCodeEvents().collect { code ->
                _uiState.update { it.copy(authorizationCode = code) }
                exchangeAuthorizationCode(code)
            }
        }
    }

    fun prepareLogin() {
        // Always generate a fresh URL + PKCE code_verifier pair together.
        // To avoid a verifier/code mismatch, clear any previously-generated URL
        // so the user knows to use the new one.
        val freshUrl = keycloakAuthRepository.createAuthorizationLoginUrl()
        _uiState.update {
            it.copy(
                loginUrl = freshUrl,
                authorizationCode = "",   // discard any stale code from the old flow
                errorMessage = null,
            )
        }
    }

    /** Resets the login flow so the user can start over without creating a new PKCE pair. */
    fun resetLogin() {
        _uiState.update {
            it.copy(
                loginUrl = null,
                authorizationCode = "",
                errorMessage = null,
            )
        }
    }

    fun onAuthorizationCodeChange(value: String) {
        _uiState.update { it.copy(authorizationCode = value) }
    }

    fun login() {
        val code = _uiState.value.authorizationCode.trim()
        if (code.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Authorization code is required") }
            return
        }

        viewModelScope.launch {
            exchangeAuthorizationCode(code)
        }
    }

    private suspend fun exchangeAuthorizationCode(code: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        runCatching { keycloakAuthRepository.loginWithAuthorizationCode(code) }
            .onSuccess { session ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        session = session,
                        loginUrl = null,
                        errorMessage = null,
                        authorizationCode = "",
                    )
                }
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Login failed",
                    )
                }
            }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { keycloakAuthRepository.logout(_uiState.value.session?.refreshToken) }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    session = null,
                    loginUrl = null,
                    authorizationCode = "",
                    errorMessage = null,
                )
            }
        }
    }
}



