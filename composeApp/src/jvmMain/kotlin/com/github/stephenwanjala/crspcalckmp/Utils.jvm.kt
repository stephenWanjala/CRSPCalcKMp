package com.github.stephenwanjala.crspcalckmp

actual fun formatNumber(
    number: Double,
    decimals: Int,
    locale: String
): String {
    return java.text.NumberFormat.getNumberInstance(java.util.Locale(locale)).apply {
        isGroupingUsed=true
        minimumFractionDigits = decimals
        maximumFractionDigits = decimals
    }.format(number)
}

actual fun String.isDigitsOnlyString(): Boolean {
    return this.all{ it.isDigit()}
}