package com.github.stephenwanjala.crspcalckmp

import android.app.Application
import com.github.stephenwanjala.crspcalckmp.di.initKoin

class CRSPCalcApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}