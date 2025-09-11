package com.github.stephenwanjala.crspcalckmp.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.github.stephenwanjala.crspcalckmp.domain.models.Vehicle

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
actual fun PlatformDetailsScreen(
    vehicle: Vehicle,
    onNavigateUp: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel,
) {
    DetailsScreenDesktop(
        viewModel = viewModel,
        onNavigateUp = onNavigateUp,
        vehicle = vehicle
    )
}
