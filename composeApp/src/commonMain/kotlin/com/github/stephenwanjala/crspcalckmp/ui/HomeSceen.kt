package com.github.stephenwanjala.crspcalckmp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.stephenwanjala.crspcalckmp.domain.models.Vehicle
import com.github.stephenwanjala.crspcalckmp.formatNumber
import crspcalckmp.composeapp.generated.resources.Res
import crspcalckmp.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource

data class VehicleSharedElementKey(val vehicle: Vehicle)

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class, InternalResourceApi::class
)
@Composable
fun HomeScreen(
    onVehicleClick: (Vehicle) -> Unit,
    state: HomeState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onFilterOptionSelected: (FilterOptions, String?) -> Unit,
    onSortTypeSelected: (SortType) -> Unit,
    onClearAllFilters: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showFilterBottomSheet by remember { mutableStateOf<FilterOptions?>(null) }
    var showSortBottomSheet by remember { mutableStateOf(false) }

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
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (item == FilterOptions.Sort || item == FilterOptions.Order) {
                                    showSortBottomSheet = true
                                } else {
                                    showFilterBottomSheet = item
                                }
                            },
                            label = { Text(text = item.name) },
                            trailingIcon = {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected"
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = item.name
                                    )
                                }
                            },
                            enabled = true,
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(8.dp),
                    state = lazyListState
                ) {
                    items(state.vehicles) { vehicle ->
                        HorizontalDivider(thickness = .1.dp)
                        VehicleItem(
                            vehicle = vehicle,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope, onVehicleClick = onVehicleClick
                        )
                        if (vehicle != state.vehicles.last()) {
                            HorizontalDivider(thickness = .1.dp)

                        }
                    }
                }
            }
        }

        // Filter Modal Sheet
        showFilterBottomSheet?.let { filterOption ->
            val options = when (filterOption) {
                FilterOptions.Make -> state.vehicles.mapNotNull { it.make }.distinct()
                FilterOptions.Model -> {
                    // Only show models for the selected make, if any. Otherwise, all models.
                    val filteredByMake = state.vehicles.filter {
                        state.selectedMakeFilter == null || it.make == state.selectedMakeFilter
                    }
                    filteredByMake.mapNotNull { it.model }.filter { it.isNotBlank() }.distinct()
                }

                FilterOptions.Fuel -> state.vehicles.mapNotNull { it.fuel }.distinct()
                FilterOptions.Type -> state.vehicles.mapNotNull { it.bodyType }.distinct()
                FilterOptions.Transmission -> state.vehicles.mapNotNull { it.transmission }
                    .distinct()

                FilterOptions.Drive -> state.vehicles.mapNotNull { it.engineCapacity }.distinct()
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

            FilterModalSheet(
                options = options,
                selectedOption = selectedOption,
                onOptionSelected = { value ->
                    onFilterOptionSelected(filterOption, value)
                    showFilterBottomSheet = null
                },
                onDismiss = { showFilterBottomSheet = null },
                option = filterOption,
                onClearFilter = {
                    onFilterOptionSelected(filterOption, null)
                    showFilterBottomSheet = null
                }
            )
        }

        // Sort Modal Sheet
        if (showSortBottomSheet) {
            SortModalSheet(
                onOptionSelected = { sortOption ->
                    val newOrderType = if (sortOption == state.selectedSortType) {
                        // Toggle order if the same sort type is selected again
                        if (state.selectedSortType.orderType == OrderType.Ascending) OrderType.Descending else OrderType.Ascending
                    } else {
                        OrderType.Ascending
                    }
                    onSortTypeSelected(sortOption.copy(newOrderType = newOrderType))
                },
                onDismiss = { showSortBottomSheet = false },
                options = listOf(
                    SortType.Make(OrderType.Ascending),
                    SortType.Price(OrderType.Ascending),
                    SortType.Seats(OrderType.Ascending)
                ), // Pass base sort types
                getName = { sortType ->
                    val orderSuffix =
                        if (sortType.orderType == OrderType.Ascending) " (Asc)" else " (Desc)"
                    when (sortType) {
                        is SortType.Make -> "Make" + if (state.selectedSortType is SortType.Make) orderSuffix else ""
                        is SortType.Price -> "Price" + if (state.selectedSortType is SortType.Price) orderSuffix else ""
                        is SortType.Seats -> "Seats" + if (state.selectedSortType is SortType.Seats) orderSuffix else ""
                    }
                },
                selectedOption = state.selectedSortType,
                description = "Sort By",
                onOrderChanged = { newOrderType ->
                    state.selectedSortType?.let { currentSortType ->
                        onSortTypeSelected(currentSortType.copy(newOrderType = newOrderType))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : SortType> SortModalSheet(
    onOptionSelected: (T) -> Unit,
    onDismiss: () -> Unit,
    options: List<T>,
    getName: (T) -> String,
    selectedOption: T? = null,
    description: String,
    onOrderChanged: (OrderType) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                items(options) { valueOption ->
                    val isSelected = when {
                        selectedOption == null -> false
                        selectedOption::class == valueOption::class -> true
                        else -> false
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onOptionSelected(valueOption)
                        },
                        label = {
                            Text(text = getName(valueOption))
                        },
                        trailingIcon = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected"
                                )
                            }
                        }
                    )
                    if (isSelected) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 4.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedOption?.orderType == OrderType.Ascending,
                                onClick = { onOrderChanged(OrderType.Ascending) },
                                label = { Text("Ascending") },
                                trailingIcon = {
                                    if (selectedOption?.orderType == OrderType.Ascending) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Ascending Selected"
                                        )
                                    }
                                }
                            )
                            FilterChip(
                                selected = selectedOption?.orderType == OrderType.Descending,
                                onClick = { onOrderChanged(OrderType.Descending) },
                                label = { Text("Descending") },
                                trailingIcon = {
                                    if (selectedOption?.orderType == OrderType.Descending) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Descending Selected"
                                        )
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterModalSheet(
    selectedOption: String? = null,
    onOptionSelected: (String?) -> Unit,
    onDismiss: () -> Unit,
    options: List<String>,
    option: FilterOptions,
    onClearFilter: () -> Unit
) {
    val searchQueryState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val filteredOptions = remember(options, searchQueryState.value.text) {
        if (searchQueryState.value.text.isBlank()) {
            options
        } else {
            options.filter {
                it.contains(searchQueryState.value.text, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = Modifier.fillMaxSize().consumeWindowInsets(WindowInsets.statusBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = option.name,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )
                if (selectedOption != null) {
                    IconButton(onClick = onClearFilter) {
                        Icon(
                            imageVector = Icons.Default.ClearAll,
                            contentDescription = "Clear Filter"
                        )
                    }
                }
            }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                value = searchQueryState.value,
                singleLine = true,
                onValueChange = { newValue ->
                    searchQueryState.value = newValue
                },
                placeholder = { Text(text = "Search") },
                trailingIcon = {
                    AnimatedVisibility(visible = searchQueryState.value.text.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQueryState.value = TextFieldValue("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                items(filteredOptions) { filterValue ->
                    FilterChip(
                        modifier = Modifier.fillMaxWidth(),
                        selected = selectedOption == filterValue,
                        onClick = {
                            onOptionSelected(filterValue)
                            onDismiss()
                        },
                        label = { Text(text = filterValue) },
                        trailingIcon = {
                            if (selectedOption == filterValue) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = filterValue
                                )
                            }
                        },
                        shape = MaterialTheme.shapes.medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VehicleItem(
    vehicle: Vehicle,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope, onVehicleClick: (Vehicle) -> Unit
) {
    val sharedElementKey = VehicleSharedElementKey(vehicle)
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .sharedElement(
                    rememberSharedContentState(key = sharedElementKey),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .fillMaxWidth()
                .clickable { onVehicleClick(vehicle) }
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val engineCapacityFormatted = vehicle.engineCapacity
                    ?.toDoubleOrNull()
                    ?.let { "${formatNumber(number = it, decimals = 2)} CC" }
                    ?: vehicle.engineCapacity ?: " "

                Text(text = "$engineCapacityFormatted • ${vehicle.fuel}")


            }
            Text(text = vehicle.make ?: "N/A", fontWeight = FontWeight.Bold)
            Text(text = vehicle.model ?: "N/A")
            val price = vehicle.crsp?.let {
                formatNumber(number = it, decimals = 2)
            } ?: "${vehicle.crsp}"
            Text(
                text = "KES $price",
                fontWeight = FontWeight.Bold
            )
        }

    }
}

