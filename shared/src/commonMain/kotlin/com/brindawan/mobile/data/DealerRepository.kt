package com.brindawan.mobile.data

import com.apollographql.apollo.ApolloClient
import com.brindawan.mobile.DealersQuery

class DealerRepository(
    private val apolloClient: ApolloClient,
) {
    suspend fun getDealers(): List<Dealer> {
        val response = apolloClient.query(DealersQuery()).execute()

        if (response.hasErrors()) {
            val errorMessage = response.errors?.joinToString { it.message } ?: "Unknown GraphQL error"
            throw IllegalStateException(errorMessage)
        }

        return response.data
            ?.dealers
            .orEmpty()
            .map { item ->
                Dealer(
                    id = item.id,
                    shopName = item.shopName,
                    phones = item.phones.orEmpty(),
                    address = DealerAddress(
                        city = item.address.city,
                        municipality = item.address.municipality,
                        plusCode = item.address.plusCode,
                        coordinate = item.address.coordinate?.let {
                            Coordinate(
                                latitude = it.latitude,
                                longitude = it.longitude,
                            )
                        },
                    ),
                )
            }
    }
}

