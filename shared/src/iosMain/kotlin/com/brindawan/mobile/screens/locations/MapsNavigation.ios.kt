package com.brindawan.mobile.screens.locations

import com.brindawan.mobile.data.Dealer

actual fun openMapsForDealer(dealer: Dealer) {
    val destination = when {
        dealer.address.coordinate != null ->
            "${dealer.address.coordinate.latitude},${dealer.address.coordinate.longitude}"
        else -> listOf(dealer.address.city, dealer.address.municipality)
            .filter { it.isNotBlank() }
            .joinToString(", ", postfix = ", Nepal")
    }

    if (destination.isBlank()) return

    try {
        val mapsUrl = "https://maps.apple.com/?q=${dealer.shopName.replace(" ", "%20")}&daddr=${destination.replace(" ", "%20")}"
        println("Open Maps URL: $mapsUrl")
    } catch (e: Exception) {
        println("Failed to open maps: ${e.message}")
    }
}
