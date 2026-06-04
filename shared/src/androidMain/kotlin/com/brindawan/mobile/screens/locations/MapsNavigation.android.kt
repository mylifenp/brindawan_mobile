package com.brindawan.mobile.screens.locations

import android.app.Application
import android.content.Intent
import androidx.core.net.toUri
import com.brindawan.mobile.data.Dealer
import org.koin.core.context.GlobalContext

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
        val app = GlobalContext.get().get<Application>()
        val mapsUrl = "https://www.google.com/maps/dir/?api=1&destination=${destination.replace(" ", "%20")}&destination_name=${dealer.shopName.replace(" ", "%20")}"
        val intent = Intent(Intent.ACTION_VIEW, mapsUrl.toUri()).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val packageManager = app.packageManager
        if (intent.resolveActivity(packageManager) != null) {
            app.startActivity(intent)
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, mapsUrl.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            app.startActivity(browserIntent)
        }
    } catch (e: Exception) {
        println("Failed to open maps: ${e.message}")
    }
}
