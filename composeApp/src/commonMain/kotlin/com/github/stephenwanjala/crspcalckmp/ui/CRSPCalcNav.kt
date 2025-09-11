package com.github.stephenwanjala.crspcalckmp.ui
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CRSPCalcNav(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope
) {
    val homeVm = koinViewModel<HomeViewModel>()
    val state = homeVm.state.collectAsStateWithLifecycle().value
    NavHost(
        navController = navController,
        startDestination = Destinations.HomeDestination,
        modifier = modifier
    ) {
        composable<Destinations.HomeDestination> {
            PlatformHomeScreen(
                onVehicleClick = { vehicle ->
                    navController.navigate(
                        Destinations.DetailDestination(
                            make = vehicle.make!!,
                            model = vehicle.model!!
                        )
                    )
                },
                state = state,
                animatedVisibilityScope = this,
                sharedTransitionScope = sharedTransitionScope,
                onFilterOptionSelected = { option, value ->
                    homeVm.onFilterOptionSelected(option, value)
                },
                onSortTypeSelected = { sortType ->
                    homeVm.onSortTypeSelected(sortType)
                },
                onClearAllFilters = {
                    homeVm.onClearAllFilters()
                }
            )
        }
        composable<Destinations.DetailDestination> { navBackEntry ->
            val vehicleModel = navBackEntry.toRoute<Destinations.DetailDestination>().model
            val vehicleMake = navBackEntry.toRoute<Destinations.DetailDestination>().make
            val vehicle =
                state.vehicles.first { it.model == vehicleModel && it.make == vehicleMake }
            PlatformDetailsScreen(
                vehicle = vehicle,
                animatedVisibilityScope = this,
                sharedTransitionScope = sharedTransitionScope,
                onNavigateUp = navController::navigateUp,
                viewModel = homeVm,
            )
        }
    }
}