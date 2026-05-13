package com.brindawan.mobile.data

import com.apollographql.apollo.ApolloClient
import com.brindawan.mobile.ProductsQuery

class ProductRepository(
    private val apolloClient: ApolloClient,
) {
    suspend fun getProducts(): List<Product> {
        val response = apolloClient.query(ProductsQuery()).execute()

        if (response.hasErrors()) {
            val errorMessage = response.errors?.joinToString { it.message } ?: "Unknown GraphQL error"
            throw IllegalStateException(errorMessage)
        }

        return response.data
            ?.products
            .orEmpty()
            .map { item ->
                Product(
                    id = item.product.id,
                    name = item.product.name,
                )
            }
    }
}

