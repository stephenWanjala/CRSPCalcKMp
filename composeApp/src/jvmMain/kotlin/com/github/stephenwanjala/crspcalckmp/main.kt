package com.github.stephenwanjala.crspcalckmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CRSP CalcKMp",
    ) {
        App()
    }
}