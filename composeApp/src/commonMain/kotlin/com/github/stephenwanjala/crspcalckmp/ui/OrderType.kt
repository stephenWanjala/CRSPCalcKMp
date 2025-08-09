package com.github.stephenwanjala.crspcalckmp.ui

sealed interface OrderType {
    data object Ascending : OrderType
    data object Descending : OrderType
}

