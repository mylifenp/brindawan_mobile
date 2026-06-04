package com.brindawan.mobile.screens.locations

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.brindawan.mobile.data.Dealer
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private data class DealerMarker(
    val dealer: Dealer,
    val position: LatLng,
)

private val nepalBounds = LatLngBounds(
    LatLng(26.3, 80.0),
    LatLng(30.5, 88.3),
)


@Composable
actual fun GoogleMapContent(
    modifier: Modifier,
    searchText: String,
    dealers: List<Dealer>,
    nearestDealerId: String?,
    selectedDealerId: String?,
    onUpdateUserLocation: (Double, Double) -> Unit,
) {
    val context = LocalContext.current
    val fusedClient = remember(context) { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.Builder()
            .target(LatLng(28.3, 84.0))
            .zoom(7f)
            .build()
    }

    var locationPermissionGranted by remember { mutableStateOf(hasLocationPermission(context)) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var dealerMarkers by remember { mutableStateOf<List<DealerMarker>>(emptyList()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        locationPermissionGranted = granted
    }

    LaunchedEffect(locationPermissionGranted) {
        if (!locationPermissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return@LaunchedEffect
        }

        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    userLocation = latLng
                    onUpdateUserLocation(location.latitude, location.longitude)
                }
            }
    }

    LaunchedEffect(dealers) {
        dealerMarkers = dealers.mapNotNull { dealer ->
            dealer.address.coordinate?.let { coord ->
                DealerMarker(dealer = dealer, position = LatLng(coord.latitude, coord.longitude))
            }
        }
    }

    val nearestMarker = remember(dealerMarkers, nearestDealerId, userLocation) {
        dealerMarkers.firstOrNull { it.dealer.id == nearestDealerId }
            ?: userLocation?.let { currentUser ->
                dealerMarkers.minByOrNull { marker ->
                    distanceInKm(
                        currentUser.latitude,
                        currentUser.longitude,
                        marker.position.latitude,
                        marker.position.longitude,
                    )
                }
            }
    }

    val selectedMarker = remember(dealerMarkers, selectedDealerId) {
        dealerMarkers.firstOrNull { it.dealer.id == selectedDealerId }
    }

    LaunchedEffect(selectedMarker) {
        val marker = selectedMarker ?: return@LaunchedEffect
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(marker.position, 13.5f),
        )
    }

    LaunchedEffect(userLocation, nearestMarker) {
        val currentUser = userLocation ?: return@LaunchedEffect
        if (!nepalBounds.contains(currentUser)) return@LaunchedEffect

        if (nearestMarker != null) {
            val visibleBounds = LatLngBounds.Builder()
                .include(currentUser)
                .include(nearestMarker.position)
                .build()
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(visibleBounds, 180))
        } else {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(currentUser, 10.5f))
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = locationPermissionGranted,
            latLngBoundsForCameraTarget = nepalBounds,
        ),
    ) {
        dealerMarkers.forEach { marker ->
            val isNearest = marker.dealer.id == nearestDealerId
            val isSelected = marker.dealer.id == selectedDealerId
            val markerHue = when {
                isSelected -> BitmapDescriptorFactory.HUE_AZURE
                isNearest -> BitmapDescriptorFactory.HUE_ORANGE
                else -> BitmapDescriptorFactory.HUE_RED
            }
            Marker(
                state = MarkerState(position = marker.position),
                title = marker.dealer.shopName,
                snippet = "${marker.dealer.address.city}, ${marker.dealer.address.municipality}",
                alpha = when {
                    isSelected -> 1f
                    isNearest -> 0.95f
                    else -> 0.8f
                },
                zIndex = when {
                    isSelected -> 2f
                    isNearest -> 1f
                    else -> 0f
                },
                icon = BitmapDescriptorFactory.defaultMarker(markerHue),
                onClick = {
                    if (isSelected) {
                        openMapsForDealer(marker.dealer)
                        true
                    } else {
                        false
                    }
                },
            )
        }

        userLocation?.let { currentUser ->
            Marker(
                state = MarkerState(position = currentUser),
                title = "You are here",
                snippet = "Current location",
            )
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
}


private fun distanceInKm(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double,
): Double {
    val dLat = toRadians(lat2 - lat1)
    val dLon = toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(toRadians(lat1)) * cos(toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return 6371.0 * c
}

private fun toRadians(degrees: Double): Double = degrees * PI / 180.0
