package com.brindawan.mobile.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<AuthViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val session = state.session
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Account", style = MaterialTheme.typography.headlineSmall)

        if (session != null) {
            Text("Logged in")
            session.preferredUsername?.let { Text("User: $it") }
            session.email?.let { Text("Email: $it") }
            Button(onClick = viewModel::logout) {
                Text("Logout")
            }
            return@Column
        }

        Text("Login with Keycloak")
        Text(
            "1) Tap Generate Login URL and open it in browser\n" +
                "2) Login on Keycloak; callback deep-link should auto-complete sign in\n" +
                "3) If callback is blocked, paste the authorization code below"
        )

        val loginUrl = state.loginUrl
        if (loginUrl == null) {
            Button(onClick = viewModel::prepareLogin, enabled = !state.isLoading) {
                Text("Generate Login URL")
            }
        } else {
            Button(
                onClick = { uriHandler.openUri(loginUrl) },
                enabled = !state.isLoading,
            ) {
                Text("Open Login URL")
            }
            Text("Login URL (valid until used once):")
            SelectionContainer {
                Text(
                    text = loginUrl,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            // Let the user start over with a fresh URL + PKCE verifier pair
            Button(onClick = viewModel::resetLogin, enabled = !state.isLoading) {
                Text("Reset / Generate New URL")
            }
        }

        OutlinedTextField(
            value = state.authorizationCode,
            onValueChange = viewModel::onAuthorizationCodeChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Authorization Code") },
            placeholder = { Text("Paste 'code' query parameter here") },
            singleLine = true,
        )

        Button(
            onClick = viewModel::login,
            enabled = !state.isLoading,
        ) {
            Text("Exchange Code")
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}



