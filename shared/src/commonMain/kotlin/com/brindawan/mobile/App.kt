package com.brindawan.mobile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.brindawan.mobile.screens.MainTabsScreen
import com.brindawan.mobile.services.createAlarmeePlatformConfiguration
import com.brindawan.mobile.ui.theme.BrindawanTheme
import com.brindawan.mobile.ui.theme.BrindawanThemeVariant
import com.tweener.alarmee.rememberAlarmeeService

@Composable
fun App() {
    val alarmService = rememberAlarmeeService(
        platformConfiguration = createAlarmeePlatformConfiguration()
    )
    BrindawanTheme(darkTheme = isSystemInDarkTheme(), variant = BrindawanThemeVariant.Soft) {
        Surface {
            MainTabsScreen()
        }
    }
}
