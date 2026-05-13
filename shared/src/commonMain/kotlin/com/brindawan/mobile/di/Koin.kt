package com.brindawan.mobile.di

import com.brindawan.mobile.data.InMemoryMuseumStorage
import com.brindawan.mobile.data.KtorMuseumApi
import com.brindawan.mobile.data.MuseumRepository
import com.brindawan.mobile.data.ProductRepository
import com.brindawan.mobile.screens.detail.DetailViewModel
import com.brindawan.mobile.screens.list.ListViewModel
import com.brindawan.mobile.screens.products.ProductsViewModel
import com.apollographql.apollo.ApolloClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

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
    single {
        ApolloClient.Builder()
            .serverUrl("https://api.brindawan.com/graphql")
            .build()
    }
    single<com.brindawan.mobile.data.MuseumStorage> { InMemoryMuseumStorage() }
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
}

fun initKoin() {
    startKoin {
        modules(
            dataModule,
            viewModelModule,
        )
    }
}
