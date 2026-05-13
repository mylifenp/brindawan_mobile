package com.brindawan.mobile

import com.brindawan.mobile.di.initKoin

class MuseumApp : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
