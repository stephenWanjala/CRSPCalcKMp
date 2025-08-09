package com.github.stephenwanjala.crspcalckmp

import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import kotlin.math.pow

actual fun formatNumber(number: Double, decimals: Int, locale: String): String {
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterDecimalStyle
        minimumFractionDigits = decimals.toULong()
        maximumFractionDigits = decimals.toULong()
        usesGroupingSeparator = true
        this.locale = NSLocale(locale)
    }
    val nsNumber = NSNumber(number)

    return formatter.stringFromNumber(nsNumber) ?: number.toString(decimals, useThousandSeparators = true)
}

/**
 * Converts a Double to a String with a specified number of decimal places.
 *
 * @param decimals The number of decimal places to include in the String.
 *                 If decimals is 0 or less, the number will be returned as an integer String.
 * @param useThousandSeparators Whether to include thousand separators in the integer part.
 * @return A String representation of the Double with the specified number of decimal places.
 *
 * Examples:
 * ```
 * 123.456.toString(2, true)  // "123.45"
 * 123.4.toString(3, true)    // "123.400"
 * 123.0.toString(2, true)    // "123.00"
 * 123.456.toString(0, true)  // "123"
 * 123456.789.toString(2, true)  // "123,456.79"
 * 123456.789.toString(2, false) // "123456.79"
 * ```
 */
fun Double.toString(decimals: Int, useThousandSeparators: Boolean = false): String {
    // Handle negative numbers
    val isNegative = this < 0
    val absNumber = if (isNegative) -this else this

    // Split the number into integer and decimal parts
    val intPart = absNumber.toLong()
    val decimalPart = if (decimals > 0) {
        val factor = 10.0.pow(decimals)
        val decimal = ((absNumber - intPart) * factor).toLong()
        decimal.toString().padStart(decimals, '0')
    } else {
        ""
    }

    // Format integer part with thousand separators if requested
    val formattedIntPart = if (useThousandSeparators) {
        intPart.toString().reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    } else {
        intPart.toString()
    }

    // Combine sign, integer part, and decimal part
    val result = if (decimals > 0) {
        "$formattedIntPart.$decimalPart"
    } else {
        formattedIntPart
    }

    return if (isNegative) "-$result" else result
}

actual fun String.isDigitsOnlyString(): Boolean {
    return this.all { it.isDigit() }
}