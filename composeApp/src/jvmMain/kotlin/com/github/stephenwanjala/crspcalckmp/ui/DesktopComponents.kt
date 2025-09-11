package com.github.stephenwanjala.crspcalckmp.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.stephenwanjala.crspcalckmp.domain.models.Vehicle
import com.github.stephenwanjala.crspcalckmp.formatNumber
import io.github.stephenwanjala.komposetable.ColumnResizeMode
import io.github.stephenwanjala.komposetable.KomposeTable
import io.github.stephenwanjala.komposetable.KomposeTableDefaults
import io.github.stephenwanjala.komposetable.SortState
import io.github.stephenwanjala.komposetable.TableSelectionModel
import io.github.stephenwanjala.komposetable.TableSortColumn
import io.github.stephenwanjala.komposetable.rememberKomposeTableState

@Composable
fun DesktopFilterDropdownChip(
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
fun DesktopSearchableFilterDialog(
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
fun DesktopSortDropdownMenu(
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DesktopVehicleTable(
    vehicles: List<Vehicle>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onVehicleClick: (Vehicle) -> Unit
) {
    val selectionModel = remember { TableSelectionModel<Vehicle>() }
    val sortState = remember { mutableStateOf(SortState()) }

    with(sharedTransitionScope) {
        val columns = listOf(
            TableSortColumn<Vehicle>(
                id = "make",
                title = "Make",
                width = 150.dp,
                cellFactory = { vehicle ->
                    Text(
                        text = vehicle.make ?: "N/A",
                        fontWeight = FontWeight.Medium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = VehicleSharedElementKey(vehicle)),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                },
                comparator = compareBy { it.make ?: "" },
            ),
            TableSortColumn<Vehicle>(
                id = "model",
                title = "Model",
                width = 250.dp,
                cellFactory = { vehicle ->
                    Text(
                        text = vehicle.model ?: "N/A",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                comparator = compareBy { it.model ?: "" },
            ),
            TableSortColumn<Vehicle>(
                id = "price",
                title = "Price (KES)",
                width = 120.dp,
                cellFactory = { vehicle ->
                    val price = vehicle.crsp?.let {
                        formatNumber(number = it, decimals = 2)
                    } ?: "${vehicle.crsp}"
                    Text(
                        text = "KES $price",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                comparator = compareBy { it.crsp ?: 0.0 },
            ),
            TableSortColumn<Vehicle>(
                id = "fuel",
                title = "Fuel",
                width = 90.dp,
                cellFactory = { vehicle ->
                    Text(
                        text = vehicle.fuel ?: "N/A",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                comparator = compareBy { it.fuel ?: "" },
            ),
            TableSortColumn<Vehicle>(
                id = "bodyType",
                title = "Body Type",
                width = 120.dp,
                cellFactory = { vehicle ->
                    Text(
                        text = vehicle.bodyType ?: "N/A",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                comparator = compareBy { it.bodyType ?: "" },
            ),
            TableSortColumn<Vehicle>(
                id = "transmission",
                title = "Transmission",
                width = 120.dp,
                cellFactory = { vehicle ->
                    Text(
                        text = vehicle.transmission ?: "N/A",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.padding(8.dp),
                    )
                },
                comparator = compareBy { it.transmission ?: "" },
            ),
            TableSortColumn<Vehicle>(
                id = "engineCapacity",
                title = "Engine (CC)",
                width = 100.dp,
                cellFactory = { vehicle ->
                    val engineCapacityFormatted = vehicle.engineCapacity
                        ?.toDoubleOrNull()
                        ?.let { "${formatNumber(number = it, decimals = 2)}" }
                        ?: "N/A"
                    Text(
                        text = engineCapacityFormatted,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                comparator = compareBy { it.engineCapacity?.toDoubleOrNull() ?: 0.0 },
            ),
        )
        val state = rememberKomposeTableState(
            columnResizeMode = ColumnResizeMode.CONSTRAINED
        )

        KomposeTable(
            columns = columns,
            tableData = vehicles,
            selectionModel = selectionModel,
            sortState = sortState,
            state = state,
            colors = KomposeTableDefaults.colors.copy(
                alternatingRowColors = listOf(
                    MaterialTheme.colorScheme.background.copy(alpha = .3f),
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .3f)
                ),
                headerBackgroundColor = MaterialTheme.colorScheme.background
            ),
            onRowClick = { vehicle, _ ->
                onVehicleClick(vehicle)
            },
            onSelectionChange = { selectedVehicles ->

            },
        )
    }
}