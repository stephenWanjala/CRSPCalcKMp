package com.github.stephenwanjala.crspcalckmp

import androidx.core.text.isDigitsOnly

actual fun formatNumber(number: Double, decimals: Int, locale: String): String {
    return java.text.NumberFormat.getNumberInstance(java.util.Locale(locale)).apply {
        minimumFractionDigits = decimals
        maximumFractionDigits = decimals
    }.format(number)
}

actual fun String.isDigitsOnlyString(): Boolean {
    return this.isDigitsOnly()
}