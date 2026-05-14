package com.brindawan.mobile.screens.locations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brindawan.mobile.data.Dealer

@Composable
actual fun GoogleMapContent(
    modifier: Modifier,
    searchText: String,
    dealers: List<Dealer>,
    nearestDealerId: String?,
    selectedDealerId: String?,
    onUpdateUserLocation: (Double, Double) -> Unit,
) {
    // iOS implementation with MapKit
    // Nepal coordinates: Center ~28.3°N, 84.0°E
    // In a real implementation, you would use UIViewRepresentable with MKMapView

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Nepal Map - ${dealers.size} Dealers",
                fontSize = 18.sp,
                color = Color.Gray,
            )
            Text(
                text = "Center: 28.3°N, 84.0°E",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Show nearest dealer info if available
            if (nearestDealerId != null) {
                val nearest = dealers.find { it.id == nearestDealerId }
                if (nearest != null) {
                    Text(
                        text = "Nearest: ${nearest.shopName}",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            if (searchText.isNotBlank()) {
                Text(
                    text = "Search: $searchText",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
