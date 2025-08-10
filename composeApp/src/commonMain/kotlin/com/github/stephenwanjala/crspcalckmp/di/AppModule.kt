package com.github.stephenwanjala.crspcalckmp.di

import com.github.stephenwanjala.crspcalckmp.ui.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    viewModelOf(::HomeViewModel)
}


fun initKoin() {
    startKoin {
        modules(appModule)
    }
}
