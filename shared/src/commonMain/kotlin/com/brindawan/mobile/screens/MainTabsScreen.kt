package com.brindawan.mobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.brindawan.mobile.screens.auth.AccountScreen
import com.brindawan.mobile.screens.locations.LocationsScreen
import com.brindawan.mobile.screens.products.ProductsScreen
import com.brindawan.mobile.ui.defaultIceCreamPainter
import com.brindawan.mobile.ui.theme.brindawanBottomNavContainerColor

enum class BottomTab(
    val title: String,
    val icon: @Composable () -> Unit,
) {
    Offers(
        title = "Home",
        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
    ),
    Products(
        title = "Products",
        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Products") },
    ),
    Locations(
        title = "Locations",
        icon = { Icon(Icons.Filled.LocationOn, contentDescription = "Locations") },
    ),
    Account(
        title = "Account",
        icon = { Icon(Icons.Filled.Person, contentDescription = "Account") },
    ),
}

@Composable
fun MainTabsScreen() {
    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.Offers) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(22.dp)),
                containerColor = brindawanBottomNavContainerColor(),
                tonalElevation = 8.dp,
            ) {
                BottomTab.entries.forEach { tab ->
                    val isSelected = tab == selectedTab
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.16f else 1f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 520f),
                        label = "tabIconScale",
                    )
                    val iconPop by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0f,
                        animationSpec = spring(dampingRatio = 0.75f, stiffness = 420f),
                        label = "tabIconPop",
                    )

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            selectedTab = tab
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(iconScale)
                                    .graphicsLayer {
                                        translationY = -2f * iconPop
                                        rotationZ = 4f * iconPop
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                tab.icon()
                            }
                        },
                        label = { Text(tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.tertiary,
                            selectedTextColor = MaterialTheme.colorScheme.tertiary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        when (selectedTab) {
            BottomTab.Offers -> OffersHomePage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            BottomTab.Products -> ProductsScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            BottomTab.Locations -> LocationsScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            BottomTab.Account -> AccountScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}

private data class HouseSpecialOffer(
    val title: String,
    val description: String,
    val imageUrl: String? = null,
)

private val houseSpecialOffers = listOf(
    HouseSpecialOffer(
        title = "Keshar Pista",
        description = "Delicious pista folded with saffron threads and creamy milk solids.",
    ),
    HouseSpecialOffer(
        title = "Twenty One Love",
        description = "A vibrant blend of 21 seasonal fruits, bursting with fresh and tangy flavors.",
    ),
    HouseSpecialOffer(
        title = "Butterscotch",
        description = "Rich butterscotch flavor with a hint of caramel and a creamy texture.",
    ),
)

@Composable
private fun OffersHomePage(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "House Special Scoops",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        items(houseSpecialOffers) { offer ->
            OfferCard(offer = offer)
        }
    }
}

@Composable
private fun OfferCard(offer: HouseSpecialOffer) {
    val defaultPainter = defaultIceCreamPainter()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            AsyncImage(
                model = offer.imageUrl?.takeIf { it.isNotBlank() },
                contentDescription = offer.title,
                placeholder = defaultPainter,
                error = defaultPainter,
                fallback = defaultPainter,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = offer.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = offer.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
