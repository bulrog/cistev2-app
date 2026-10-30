package com.bulrog59.ciste2dot0

import android.app.Application
import com.google.firebase.FirebaseApp

class CisteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        installAppCheck()
    }
}
