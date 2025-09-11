package com.github.stephenwanjala.crspcalckmp.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import com.github.stephenwanjala.crspcalckmp.domain.models.Vehicle

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
actual fun PlatformHomeScreen(
    onVehicleClick: (Vehicle) -> Unit,
    state: HomeState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onFilterOptionSelected: (FilterOptions, String?) -> Unit,
    onSortTypeSelected: (SortType) -> Unit,
    onClearAllFilters: () -> Unit
) {
    HomeScreen(
        onVehicleClick = onVehicleClick,
        state = state,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        onFilterOptionSelected = onFilterOptionSelected,
        onSortTypeSelected = onSortTypeSelected,
        onClearAllFilters = onClearAllFilters
    )
}