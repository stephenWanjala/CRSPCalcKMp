package com.github.stephenwanjala.crspcalckmp

import androidx.core.text.isDigitsOnly
import java.text.NumberFormat
import java.util.Locale

actual fun formatNumber(number: Double, decimals: Int, locale: String): String {
    val localeParts = locale.split("-", "_")
    val javaLocale = when (localeParts.size) {
        1 -> Locale(localeParts[0])
        2 -> Locale(localeParts[0], localeParts[1])
        else -> Locale.getDefault()
    }

    return NumberFormat.getNumberInstance(javaLocale).apply {
        isGroupingUsed = true
        minimumFractionDigits = decimals
        maximumFractionDigits = decimals
    }.format(number)
}

actual fun String.isDigitsOnlyString(): Boolean {
    return this.isDigitsOnly()
}
