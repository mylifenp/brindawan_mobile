package com.brindawan.mobile

import com.brindawan.mobile.di.initKoin
import com.brindawan.mobile.di.AppConfig
import com.brindawan.mobile.data.AndroidSecureStoreInitializer

class BrindawanApp : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidSecureStoreInitializer.init(this)
        initKoin(
            AppConfig(
                graphqlUrl = BuildConfig.GRAPHQL_SERVER_URL,
                keycloakRealmUrl = BuildConfig.KEYCLOAK_REALM_URL,
                keycloakClientId = BuildConfig.KEYCLOAK_CLIENT_ID,
                keycloakRedirectUri = BuildConfig.KEYCLOAK_REDIRECT_URI,
                keycloakScope = BuildConfig.KEYCLOAK_SCOPE,
                keycloakClientSecret = BuildConfig.KEYCLOAK_CLIENT_SECRET,
            )
        )
    }
}
