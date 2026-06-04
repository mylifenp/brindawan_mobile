package com.brindawan.mobile.data

import kotlinx.serialization.Serializable

@Serializable
data class Dealer(
    val id: String,
    val shopName: String,
    val phones: List<String>,
    val address: DealerAddress,
)

@Serializable
data class DealerAddress(
    val city: String,
    val municipality: String,
    val plusCode: String? = null,
    val coordinate: Coordinate? = null,
)

@Serializable
data class Coordinate(
    val latitude: Double,
    val longitude: Double,
)

