package com.github.stephenwanjala.crspcalckmp.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.github.stephenwanjala.crspcalckmp.domain.models.Vehicle
import crspcalckmp.composeapp.generated.resources.Res
import crspcalckmp.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class,
    InternalResourceApi::class
)
@Composable
fun HomeScreenDesktop(
    onVehicleClick: (Vehicle) -> Unit,
    state: HomeState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onFilterOptionSelected: (FilterOptions, String?) -> Unit,
    onSortTypeSelected: (SortType) -> Unit,
    onClearAllFilters: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    adaptiveInfo.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

    var showFilterDialog by remember { mutableStateOf<FilterOptions?>(null) }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            TopAppBar(title = {
                Icon(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "Logo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }, actions = {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
                }
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "About App")
                }
            }, scrollBehavior = scrollBehavior)
        }, floatingActionButton = {
            FloatingActionButton(onClick = onClearAllFilters) {
                Icon(
                    imageVector = Icons.Default.ClearAll,
                    contentDescription = "Clear All Filters"
                )
            }
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(paddingValues)
                    .padding(paddingValues),
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    items(FilterOptions.entries.toList()) { item ->
                        val isSelected = when (item) {
                            FilterOptions.Make -> state.selectedMakeFilter != null
                            FilterOptions.Model -> state.selectedModelFilter != null
                            FilterOptions.Fuel -> state.selectedFuelFilter != null
                            FilterOptions.Type -> state.selectedTypeFilter != null
                            FilterOptions.Transmission -> state.selectedTransmissionFilter != null
                            FilterOptions.Drive -> state.selectedDriveFilter != null
                            FilterOptions.Sort, FilterOptions.Order -> false
                        }

                        DesktopFilterDropdownChip(
                            filterOption = item,
                            isSelected = isSelected,
                            state = state,
                            onFilterOptionSelected = onFilterOptionSelected,
                            onSortTypeSelected = onSortTypeSelected,
                            onShowFilterDialog = { filterOptionToShow ->
                                showFilterDialog = filterOptionToShow
                            }
                        )
                    }
                }

                // Always show table layout on desktop
                DesktopVehicleTable(
                    vehicles = state.vehicles,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onVehicleClick = onVehicleClick
                )
            }
        }

        // Desktop Filter Dialog
        showFilterDialog?.let { filterOption ->
            val options = when (filterOption) {
                FilterOptions.Make -> state.vehicles.mapNotNull { it.make }.distinct()
                FilterOptions.Model -> {
                    val filteredByMake = state.vehicles.filter {
                        state.selectedMakeFilter == null || it.make == state.selectedMakeFilter
                    }
                    filteredByMake.mapNotNull { it.model }.filter { it.isNotBlank() }.distinct()
                }

                FilterOptions.Fuel -> state.vehicles.mapNotNull { it.fuel }.distinct()
                FilterOptions.Type -> state.vehicles.mapNotNull { it.bodyType }.distinct()
                FilterOptions.Transmission -> state.vehicles.mapNotNull { it.transmission }
                    .distinct()

                FilterOptions.Drive -> state.vehicles.mapNotNull { it.engineCapacity }
                    .distinct()

                else -> emptyList()
            }.sorted()

            val selectedOption = when (filterOption) {
                FilterOptions.Make -> state.selectedMakeFilter
                FilterOptions.Model -> state.selectedModelFilter
                FilterOptions.Fuel -> state.selectedFuelFilter
                FilterOptions.Type -> state.selectedTypeFilter
                FilterOptions.Transmission -> state.selectedTransmissionFilter
                FilterOptions.Drive -> state.selectedDriveFilter
                else -> null
            }

            DesktopSearchableFilterDialog(
                options = options,
                selectedOption = selectedOption,
                onOptionSelected = { value ->
                    onFilterOptionSelected(filterOption, value)
                    showFilterDialog = null
                },
                onDismissRequest = { showFilterDialog = null },
                filterOption = filterOption,
                onClearFilter = {
                    onFilterOptionSelected(filterOption, null)
                    showFilterDialog = null
                }
            )
        }
    }
}