package com.brindawan.mobile.data

import com.apollographql.apollo.ApolloClient
import com.brindawan.mobile.ProductsQuery

private const val DEFAULT_PRODUCT_IMAGE_URL = "https://brindawan.com/ice-cream-hero.svg"

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
                    description = item.product.description,
                    variations = item.variations.map { variation ->
                        val resolvedImage = variation.imageUrls
                            .firstOrNull { it.isNotBlank() }
                            ?: DEFAULT_PRODUCT_IMAGE_URL
                        ProductVariation(
                            label = variation.label,
                            price = variation.price.toString(),
                            imageUrl = resolvedImage,
                        )
                    },
                )
            }
    }
}

