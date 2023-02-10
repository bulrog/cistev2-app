package com.bulrog59.ciste2dot0.scenes

import androidx.lifecycle.LifecycleObserver

interface Scene : LifecycleObserver {
    fun shutdown()

}