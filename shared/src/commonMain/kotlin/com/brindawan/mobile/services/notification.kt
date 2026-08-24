package com.brindawan.mobile.services

import com.tweener.alarmee.LocalNotificationService
import com.tweener.alarmee.configuration.AlarmeePlatformConfiguration

expect fun createAlarmeePlatformConfiguration(): AlarmeePlatformConfiguration

expect fun LocalNotificationService.registerActionCallback()