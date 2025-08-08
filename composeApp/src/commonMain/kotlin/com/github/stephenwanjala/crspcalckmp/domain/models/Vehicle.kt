package com.github.stephenwanjala.crspcalckmp.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Vehicle(
    val bodyType: String?,
    val crsp: Double?,
    val driveConfiguration: String?,
    val engineCapacity: String?,
    val fuel: String?,
    val gvw: Int?,
    val make: String?,
    val model: String?,
    val modelNumber: String?,
    val seating: Int?,
    val transmission: String?
)

