package com.github.stephenwanjala.crspcalckmp.ui

import kotlinx.serialization.Serializable

sealed interface Destinations {
    @Serializable
    object HomeDestination : Destinations

    @Serializable
    data class DetailDestination(val make: String, val model: String) : Destinations
}