package com.github.stephenwanjala.crspcalckmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.stephenwanjala.crspcalckmp.di.initKoin

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "CRSP CalcKMp",
    ) {
        App()
    }
}