package com.github.stephenwanjala.crspcalckmp.ui

import com.github.stephenwanjala.crspcalckmp.domain.models.Motorcycle
import com.github.stephenwanjala.crspcalckmp.domain.models.Vehicle

data class HomeState(
    val isLoading: Boolean = false,
    val vehicles: List<Vehicle> = emptyList(),
    val motoBikes: List<Motorcycle> = emptyList(),
    val selectedMakeFilter: String? = null,
    val selectedModelFilter: String? = null,
    val selectedFuelFilter: String? = null,
    val selectedTypeFilter: String? = null,
    val selectedTransmissionFilter: String? = null,
    val selectedDriveFilter: String? = null,
    val selectedSortType: SortType = SortType.Make(OrderType.Ascending)
)