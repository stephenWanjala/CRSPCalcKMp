package com.github.stephenwanjala.crspcalckmp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Motorcycle(
    val crsp: Double?,
    val engineCapacity: Int?,
    val fuel: String?,
    val make: String?,
    val model: String?,
    val modelNumber: String?,
    val transmission: String?,
    val seating: Int?
)