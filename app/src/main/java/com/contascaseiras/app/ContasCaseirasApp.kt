package com.contascaseiras.app

import android.app.Application
import com.contascaseiras.app.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ContasCaseirasApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.criarCanal(this)
    }
}
