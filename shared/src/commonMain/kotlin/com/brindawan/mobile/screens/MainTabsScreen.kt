package com.brindawan.mobile.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.brindawan.mobile.screens.auth.AccountScreen
import com.brindawan.mobile.screens.detail.DetailScreen
import com.brindawan.mobile.screens.list.ListScreen
import com.brindawan.mobile.screens.products.ProductsScreen

enum class BottomTab(
    val title: String,
    val icon: @Composable () -> Unit,
) {
    Home(
        title = "Home",
        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
    ),
    Products(
        title = "Products",
        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Products") },
    ),
    Offers(
        title = "Offers",
        icon = { Icon(Icons.Filled.Star, contentDescription = "Offers") },
    ),
    Account(
        title = "Account",
        icon = { Icon(Icons.Filled.Person, contentDescription = "Account") },
    ),
}

@Composable
fun MainTabsScreen() {
    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.Home) }
    var selectedObjectId by rememberSaveable { mutableStateOf<Int?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = {
                            selectedTab = tab
                            if (tab != BottomTab.Home) selectedObjectId = null
                        },
                        icon = { tab.icon() },
                        label = { Text(tab.title) },
                    )
                }
            }
        },
    ) { innerPadding ->
        when (selectedTab) {
            BottomTab.Home -> {
                if (selectedObjectId == null) {
                    ListScreen(navigateToDetails = { objectId -> selectedObjectId = objectId })
                } else {
                    DetailScreen(
                        objectId = selectedObjectId!!,
                        navigateBack = { selectedObjectId = null },
                        )
                }
            }

            BottomTab.Products -> ProductsScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            BottomTab.Offers -> PlaceholderPage(
                title = "Offers",
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

@Composable
private fun PlaceholderPage(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "$title Page")
    }
}




