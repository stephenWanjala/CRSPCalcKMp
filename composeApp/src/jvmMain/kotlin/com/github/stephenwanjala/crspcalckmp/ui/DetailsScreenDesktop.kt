package com.github.stephenwanjala.crspcalckmp.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.stephenwanjala.crspcalckmp.domain.models.Vehicle
import io.github.stephenwanjala.komposetable.ColumnResizeMode
import io.github.stephenwanjala.komposetable.KomposeTable
import io.github.stephenwanjala.komposetable.KomposeTableDefaults
import io.github.stephenwanjala.komposetable.SortState
import io.github.stephenwanjala.komposetable.TableSelectionModel
import io.github.stephenwanjala.komposetable.TableSortColumn
import io.github.stephenwanjala.komposetable.rememberKomposeTableState
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.awt.Cursor

@OptIn(

    ExperimentalMaterial3Api::class,

    ExperimentalSharedTransitionApi::class,

    ExperimentalSplitPaneApi::class
)

@Composable

fun DetailsScreenDesktop(
    viewModel: HomeViewModel, onNavigateUp: () -> Unit, vehicle: Vehicle? = null
) {

    val state = viewModel.state.collectAsStateWithLifecycle().value

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val selectedVehicle = remember { mutableStateOf<Vehicle?>(vehicle) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                },

                actions = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close details"
                        )
                    }
                },
                scrollBehavior = scrollBehavior

            )

        },

        ) { padding ->

        val splitterState = rememberSplitPaneState(initialPositionPercentage = 0.4f)


        HorizontalSplitPane(
            splitPaneState = splitterState,
            modifier = Modifier.padding(padding)
        ) {
            first(minSize = 400.dp) {
                VehicleSidePane(
                    vehicles = state.vehicles,
                    selectedVehicle = selectedVehicle.value, onVehicleClick = {
                        selectedVehicle.value = it
                    }
                )

            }

            second(minSize = 600.dp) {

                selectedVehicle.value?.let { vehicle ->

                    VehicleDetails(
                        vehicle = vehicle,
                        modifier = Modifier.padding(16.dp)
                    )
                } ?: run {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Select a vehicle from the list to view details",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            splitter {
                visiblePart {
                    Box(
                        Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    )
                }

                handle {
                    Box(
                        Modifier.markAsHandle().cursorForHorizontalResize().background(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                        ).width(8.dp)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VehicleSidePane(
    vehicles: List<Vehicle>,
    selectedVehicle: Vehicle?,
    onVehicleClick: (Vehicle) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectionModel = remember { TableSelectionModel<Vehicle>() }
    val sortState = remember { mutableStateOf(SortState()) }
    val state = rememberKomposeTableState(
        columnResizeMode = ColumnResizeMode.CONSTRAINED,
        defaultSelectedIndices = setOf(vehicles.indexOf(selectedVehicle)),
    )
    val columns = listOf(
        TableSortColumn<Vehicle>(
            id = "make",
            title = "Make",
            width = 120.dp,
            cellFactory = { vehicle ->
                Text(
                    text = vehicle.make ?: "N/A",
                    fontWeight = if (vehicle == selectedVehicle) FontWeight.Bold else FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            },
            comparator = compareBy { it.make ?: "" },
        ),
        TableSortColumn<Vehicle>(
            id = "model",
            title = "Model",
            width = 200.dp,
            cellFactory = { vehicle ->
                Text(
                    text = vehicle.model ?: "N/A",
                    fontWeight = if (vehicle == selectedVehicle) FontWeight.Medium else FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            },
            comparator = compareBy { it.model ?: "" },
        ),
        TableSortColumn<Vehicle>(
            id = "crsp",
            title = "CRSP",
            width = 100.dp,
            cellFactory = { vehicle ->
                Text(
                    text = vehicle.crsp?.let { "KES $it" } ?: "N/A",
                    fontWeight = if (vehicle == selectedVehicle) FontWeight.Medium else FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            },
            comparator = compareBy { it.crsp ?: 0.0 },
        )
    )
    KomposeTable(
        columns = columns,
        tableData = vehicles,
        selectionModel = selectionModel,
        sortState = sortState,
        state = state,
        colors = KomposeTableDefaults.colors.copy(
            alternatingRowColors = listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            headerBackgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            selectedRowColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        onRowClick = { vehicle, _ ->
            onVehicleClick(vehicle)
        }, onSelectionChange = {
            it.firstOrNull()?.let { vehicle ->
                onVehicleClick(vehicle)
            }
        },
        modifier = modifier
    )
}