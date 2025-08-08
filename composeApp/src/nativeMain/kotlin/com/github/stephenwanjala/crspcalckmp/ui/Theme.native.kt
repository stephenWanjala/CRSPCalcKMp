package com.github.stephenwanjala.crspcalckmp.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable



@Composable
actual fun CRSPCalcTheme(
    darkTheme: Boolean,
    content: @Composable (() -> Unit)
) {
    val colorScheme = when {
        darkTheme -> highContrastDarkColorScheme
        else -> highContrastLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}