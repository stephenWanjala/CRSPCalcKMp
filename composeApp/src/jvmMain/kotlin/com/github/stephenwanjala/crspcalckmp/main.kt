package com.github.stephenwanjala.crspcalckmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.stephenwanjala.crspcalckmp.di.initKoin
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    initKoin()
    Window(
        state = rememberWindowState(placement = WindowPlacement.Maximized),
        onCloseRequest = ::exitApplication,
        title = "CRSP CalcKMp",
    ) {
        App()
    }
}