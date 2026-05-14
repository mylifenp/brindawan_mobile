package com.brindawan.mobile.data

import platform.Foundation.NSUserDefaults

actual fun readLegacySessionRaw(key: String): String? {
    return NSUserDefaults.standardUserDefaults.stringForKey(key)
}

actual fun clearLegacySessionRaw(key: String) {
    NSUserDefaults.standardUserDefaults.removeObjectForKey(key)
}

