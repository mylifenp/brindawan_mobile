package com.brindawan.mobile.di

import com.brindawan.mobile.data.InMemoryMuseumStorage
import com.brindawan.mobile.data.KeycloakAuthRepository
import com.brindawan.mobile.data.KtorMuseumApi
import com.brindawan.mobile.data.MuseumRepository
import com.brindawan.mobile.data.ProductRepository
import com.brindawan.mobile.screens.auth.AuthViewModel
import com.brindawan.mobile.screens.detail.DetailViewModel
import com.brindawan.mobile.screens.list.ListViewModel
import com.brindawan.mobile.screens.products.ProductsViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.cache.normalized.memory.MemoryCacheFactory
import com.brindawan.mobile.cache.Cache.cache
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

private const val DEFAULT_GRAPHQL_URL = "https://api.brindawan.com/graphql"
private const val DEFAULT_KEYCLOAK_REALM_URL = "https://auth.brindawan.com/realms/brindawan"
private const val DEFAULT_KEYCLOAK_CLIENT_ID = "brindawan_id"
private const val DEFAULT_KEYCLOAK_REDIRECT_URI = "com.brindawan.mobile://auth/callback"
private const val DEFAULT_KEYCLOAK_SCOPE = "openid profile email"
private const val DEFAULT_KEYCLOAK_CLIENT_SECRET = ""

val dataModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) {
                // TODO Fix API so it serves application/json
                json(json, contentType = ContentType.Any)
            }
        }
    }

    single<com.brindawan.mobile.data.MuseumApi> {
        KtorMuseumApi(get())
    }
    single<com.brindawan.mobile.data.MuseumStorage> { InMemoryMuseumStorage() }
    single { KeycloakAuthRepository(get()) }
    single { ProductRepository(get<ApolloClient>()) }
    single {
        MuseumRepository(get(), get()).apply {
            initialize()
        }
    }
}

val viewModelModule = module {
    factoryOf(::ListViewModel)
    factoryOf(::DetailViewModel)
    factoryOf(::ProductsViewModel)
    factoryOf(::AuthViewModel)
}

data class AppConfig(
    val graphqlUrl: String = DEFAULT_GRAPHQL_URL,
    val keycloakRealmUrl: String = DEFAULT_KEYCLOAK_REALM_URL,
    val keycloakClientId: String = DEFAULT_KEYCLOAK_CLIENT_ID,
    val keycloakRedirectUri: String = DEFAULT_KEYCLOAK_REDIRECT_URI,
    val keycloakScope: String = DEFAULT_KEYCLOAK_SCOPE,
    val keycloakClientSecret: String = DEFAULT_KEYCLOAK_CLIENT_SECRET,
)


fun initApolloClient(config: AppConfig): ApolloClient {
    val cacheFactory = MemoryCacheFactory(10 * 1024 * 1024)
    return ApolloClient.Builder()
        .serverUrl(config.graphqlUrl)
        .cache(cacheFactory)
        .build()
}

fun initKoin(config: AppConfig) {
    startKoin {
        modules(
            module {
                single { config }
                single {
                    initApolloClient(get<AppConfig>())
                }
            },
            dataModule,
            viewModelModule,
        )
    }
}

fun doInitKoin() {
    initKoin(
        AppConfig(
            graphqlUrl = DEFAULT_GRAPHQL_URL,
            keycloakRealmUrl = DEFAULT_KEYCLOAK_REALM_URL,
            keycloakClientId = DEFAULT_KEYCLOAK_CLIENT_ID,
            keycloakRedirectUri = DEFAULT_KEYCLOAK_REDIRECT_URI,
            keycloakScope = DEFAULT_KEYCLOAK_SCOPE,
            keycloakClientSecret = DEFAULT_KEYCLOAK_CLIENT_SECRET,
        )
    )
}

fun doInitKoin(graphqlUrl: String) {
    initKoin(
        AppConfig(
            graphqlUrl = graphqlUrl,
            keycloakRealmUrl = DEFAULT_KEYCLOAK_REALM_URL,
            keycloakClientId = DEFAULT_KEYCLOAK_CLIENT_ID,
            keycloakRedirectUri = DEFAULT_KEYCLOAK_REDIRECT_URI,
            keycloakScope = DEFAULT_KEYCLOAK_SCOPE,
            keycloakClientSecret = DEFAULT_KEYCLOAK_CLIENT_SECRET,
        )
    )
}

fun doInitKoin(
    graphqlUrl: String,
    keycloakRealmUrl: String,
    keycloakClientId: String,
    keycloakRedirectUri: String,
    keycloakScope: String,
    keycloakClientSecret: String = DEFAULT_KEYCLOAK_CLIENT_SECRET,
) {
    initKoin(
        AppConfig(
            graphqlUrl = graphqlUrl,
            keycloakRealmUrl = keycloakRealmUrl,
            keycloakClientId = keycloakClientId,
            keycloakRedirectUri = keycloakRedirectUri,
            keycloakScope = keycloakScope,
            keycloakClientSecret = keycloakClientSecret,
        )
    )
}

