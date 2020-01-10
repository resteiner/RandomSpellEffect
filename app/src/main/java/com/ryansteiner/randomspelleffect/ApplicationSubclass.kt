package com.ryansteiner.randomspelleffect

import android.app.Application
import com.bugsnag.android.Bugsnag

/**
 * This is required for Bugsnag
 */

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Bugsnag.init(this)
    }
}