package com.github.stephenwanjala.crspcalckmp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.window.Dialog
import androidx.window.core.layout.WindowWidthSizeClass
import com.github.stephenwanjala.crspcalckmp.domain.models.Vehicle
import com.github.stephenwanjala.crspcalckmp.formatNumber
import crspcalckmp.composeapp.generated.resources.Res
import crspcalckmp.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource

data class VehicleSharedElementKey(val vehicle: Vehicle)

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class,
    InternalResourceApi::class
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
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isDesktop =
        adaptiveInfo.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

    var showFilterBottomSheet by remember { mutableStateOf<FilterOptions?>(null) }
    var showSortBottomSheet by remember { mutableStateOf(false) }
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

                        if (isDesktop) {
                            FilterDropdownChip(
                                filterOption = item,
                                isSelected = isSelected,
                                state = state,
                                onFilterOptionSelected = onFilterOptionSelected,
                                onSortTypeSelected = onSortTypeSelected,
                                onShowFilterDialog = { filterOptionToShow ->
                                    showFilterDialog = filterOptionToShow
                                }
                            )
                        } else {
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
                }
                if (isDesktop) {
                    // Grid layout for desktop
                    VehicleGrid(
                        vehicles = state.vehicles,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onVehicleClick = onVehicleClick,
                        scrollBehavior = scrollBehavior
                    )
                } else {
                    // List layout for mobile
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
        }

        // Mobile Bottom Sheets (only show on mobile)
        if (!isDesktop) {
            // Filter Modal Sheet
            showFilterBottomSheet?.let { filterOption ->
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
                    ),
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

        // Desktop Filter Dialog (only show on desktop)
        if (isDesktop) {
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
                SearchableFilterDialog(
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
}

@Composable
fun FilterDropdownChip(
    filterOption: FilterOptions,
    isSelected: Boolean,
    state: HomeState,
    onFilterOptionSelected: (FilterOptions, String?) -> Unit,
    onSortTypeSelected: (SortType) -> Unit,
    onShowFilterDialog: (FilterOptions) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        FilterChip(
            selected = isSelected,
            onClick = {
                if (filterOption == FilterOptions.Sort || filterOption == FilterOptions.Order) {
                    expanded = !expanded
                } else {
                    onShowFilterDialog(filterOption)
                }
            },
            label = { Text(text = filterOption.name) },
            trailingIcon = {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = filterOption.name
                    )
                }
            },
            enabled = true,
        )

        // Only show DropdownMenu for Sort/Order, other filters use the dialog
        if (filterOption == FilterOptions.Sort || filterOption == FilterOptions.Order) {
            SortDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                state = state,
                onSortTypeSelected = { sortType ->
                    onSortTypeSelected(sortType)
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun SearchableFilterDialog(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit,
    onDismissRequest: () -> Unit,
    filterOption: FilterOptions,
    onClearFilter: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredOptions = remember(options, searchQuery) {
        if (searchQuery.isBlank()) {
            options
        } else {
            options.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .widthIn(min = 300.dp, max = 500.dp)
                .height(400.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Select ${filterOption.name}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search ${filterOption.name}") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (selectedOption != null) {
                    // Clear filter option
                    FilterChip(
                        modifier = Modifier.fillMaxWidth(),
                        selected = false, // Never selected itself, it's an action
                        onClick = {
                            onClearFilter()
                            searchQuery = ""
                        },
                        label = { Text("Clear ${filterOption.name} Filter") },
                        leadingIcon = {
                            Icon(Icons.Default.ClearAll, contentDescription = "Clear")
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                }


                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filteredOptions.isEmpty() && searchQuery.isNotEmpty()) {
                        item {
                            Text(
                                "No results found for \"$searchQuery\"",
                                modifier = Modifier.padding(vertical = 16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(filteredOptions) { option ->
                            FilterChip(
                                modifier = Modifier.fillMaxWidth(),
                                selected = selectedOption == option,
                                onClick = { onOptionSelected(option) },
                                label = { Text(option) },
                                trailingIcon = {
                                    if (selectedOption == option) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected")
                                    }
                                },
                                shape = MaterialTheme.shapes.medium
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun FilterDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    filterOption: FilterOptions,
    state: HomeState,
    onFilterOptionSelected: (String?) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

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
        FilterOptions.Transmission -> state.vehicles.mapNotNull { it.transmission }.distinct()
        FilterOptions.Drive -> state.vehicles.mapNotNull { it.engineCapacity }.distinct()
        else -> emptyList()
    }.sorted()

    val filteredOptions = remember(options, searchQuery) {
        if (searchQuery.isBlank()) {
            options
        } else {
            options.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    val selectedOption = when (filterOption) {
        FilterOptions.Make -> state.selectedMakeFilter
        FilterOptions.Model -> state.selectedModelFilter
        FilterOptions.Fuel -> state.selectedFuelFilter
        FilterOptions.Type -> state.selectedTypeFilter
        FilterOptions.Transmission -> state.selectedTransmissionFilter
        FilterOptions.Drive -> state.selectedDriveFilter
        else -> null
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            searchQuery = ""
            onDismissRequest()
        },
        modifier = Modifier.widthIn(min = 250.dp, max = 400.dp)
    ) {
        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search ${filterOption.name}") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            }
        )

        // Clear filter option
        if (selectedOption != null) {
            DropdownMenuItem(
                text = { Text("Clear Filter") },
                onClick = {
                    onFilterOptionSelected(null)
                    searchQuery = ""
                },
                leadingIcon = {
                    Icon(Icons.Default.ClearAll, contentDescription = "Clear")
                }
            )
            HorizontalDivider()
        }

        // Filter options
        filteredOptions.forEach { option ->
            DropdownMenuItem(
                text = { Text(option) },
                onClick = {
                    onFilterOptionSelected(option)
                    searchQuery = ""
                },
                leadingIcon = {
                    if (selectedOption == option) {
                        Icon(Icons.Default.Check, contentDescription = "Selected")
                    }
                }
            )
        }

        if (filteredOptions.isEmpty() && searchQuery.isNotEmpty()) {
            DropdownMenuItem(
                text = { Text("No results found", style = MaterialTheme.typography.bodyMedium) },
                onClick = { },
                enabled = false
            )
        }
    }
}

@Composable
fun SortDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    state: HomeState,
    onSortTypeSelected: (SortType) -> Unit
) {
    val sortOptions = listOf(
        SortType.Make(OrderType.Ascending),
        SortType.Price(OrderType.Ascending),
        SortType.Seats(OrderType.Ascending)
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.widthIn(min = 200.dp)
    ) {
        sortOptions.forEach { sortOption ->
            val isCurrentlySelected = state.selectedSortType?.let {
                it::class == sortOption::class
            } == true

            DropdownMenuItem(
                text = {
                    val baseName = when (sortOption) {
                        is SortType.Make -> "Make"
                        is SortType.Price -> "Price"
                        is SortType.Seats -> "Seats"
                    }
                    Text(baseName)
                },
                onClick = {
                    val newOrderType = if (isCurrentlySelected) {
                        // Toggle order if same sort type
                        if (state.selectedSortType?.orderType == OrderType.Ascending)
                            OrderType.Descending
                        else
                            OrderType.Ascending
                    } else {
                        OrderType.Ascending
                    }
                    onSortTypeSelected(sortOption.copy(newOrderType = newOrderType))
                },
                leadingIcon = {
                    if (isCurrentlySelected) {
                        Icon(Icons.Default.Check, contentDescription = "Selected")
                    }
                },
                trailingIcon = {
                    if (isCurrentlySelected) {
                        val orderText =
                            if (state.selectedSortType?.orderType == OrderType.Ascending) "↑" else "↓"
                        Text(orderText, style = MaterialTheme.typography.bodySmall)
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

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VehicleGrid(
    vehicles: List<Vehicle>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onVehicleClick: (Vehicle) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        modifier = Modifier
            .fillMaxWidth()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(8.dp),
        state = gridState,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(vehicles) { vehicle ->
            VehicleGridItem(
                vehicle = vehicle,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                onVehicleClick = onVehicleClick
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VehicleGridItem(
    vehicle: Vehicle,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onVehicleClick: (Vehicle) -> Unit
) {
    val sharedElementKey = VehicleSharedElementKey(vehicle)

    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .sharedElement(
                    rememberSharedContentState(key = sharedElementKey),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .fillMaxWidth()
                .clickable { onVehicleClick(vehicle) },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                hoveredElevation = 6.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Engine and Fuel info at the top
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val engineCapacityFormatted = vehicle.engineCapacity
                        ?.toDoubleOrNull()
                        ?.let { "${formatNumber(number = it, decimals = 2)} CC" }
                        ?: vehicle.engineCapacity ?: "N/A"

                    Text(
                        text = engineCapacityFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = vehicle.fuel ?: "N/A",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Vehicle Make (prominent)
                Text(
                    text = vehicle.make ?: "N/A",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Vehicle Model
                Text(
                    text = vehicle.model ?: "N/A",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Additional details
                if (vehicle.bodyType != null || vehicle.transmission != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        vehicle.bodyType?.let { bodyType ->
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = bodyType,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }

                        vehicle.transmission?.let { transmission ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = transmission,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price (prominent at bottom)
                val price = vehicle.crsp?.let {
                    formatNumber(number = it, decimals = 2)
                } ?: "${vehicle.crsp}"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KES $price",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
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
    animatedVisibilityScope: AnimatedVisibilityScope,
    onVehicleClick: (Vehicle) -> Unit
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