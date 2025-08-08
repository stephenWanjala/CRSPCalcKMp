package com.github.stephenwanjala.crspcalckmp.ui

import android.os.Build
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun CRSPCalcTheme(
    darkTheme: Boolean,
    content: @Composable (() -> Unit)
) {
    val colorScheme = when {
         Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> highContrastDarkColorScheme
        else -> highContrastLightColorScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}