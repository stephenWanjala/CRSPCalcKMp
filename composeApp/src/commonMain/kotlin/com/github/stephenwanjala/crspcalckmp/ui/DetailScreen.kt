package com.github.stephenwanjala.crspcalckmp.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AirlineSeatReclineExtra
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.stephenwanjala.crspcalckmp.domain.models.Vehicle
import com.github.stephenwanjala.crspcalckmp.formatNumber
import com.github.stephenwanjala.crspcalckmp.isDigitsOnlyString
import crspcalckmp.composeapp.generated.resources.Res
import crspcalckmp.composeapp.generated.resources.barcode
import crspcalckmp.composeapp.generated.resources.configuration
import crspcalckmp.composeapp.generated.resources.transmission
import crspcalckmp.composeapp.generated.resources.weight
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    vehicle: Vehicle, onNavigateUp: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val sharedElementKey = VehicleSharedElementKey(vehicle)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = vehicle.make!!)
        }, navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }, scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        VehicleDetails(
            sharedTransitionScope,
            paddingValues,
            sharedElementKey,
            animatedVisibilityScope,
            scrollBehavior,
            vehicle
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
fun VehicleDetails(
    sharedTransitionScope: SharedTransitionScope,
    paddingValues: PaddingValues,
    sharedElementKey: VehicleSharedElementKey,
    animatedVisibilityScope: AnimatedVisibilityScope,
    scrollBehavior: TopAppBarScrollBehavior,
    vehicle: Vehicle
) {
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .sharedElement(
                    rememberSharedContentState(key = sharedElementKey),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .verticalScroll(state = rememberScrollState())
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DetailCol(
                icon = Icons.Rounded.LocalOffer,
                label = "Price",
                text = "KES ${
                    vehicle.crsp?.let {
                        formatNumber(number = it, decimals = 2)
                    }
                }"
            )
            DetailCol(
                icon = Icons.Default.DirectionsTransit,
                label = "Model",
                text = vehicle.model!!
            )

            DetailCol(
                iconResource = Res.drawable.barcode,
                label = "Model Number",
                text = if (vehicle.modelNumber == null || vehicle.modelNumber.isEmpty()) "_" else vehicle.modelNumber
            )
            DetailCol(
                icon = Icons.Default.LocalGasStation,
                label = "Fuel",
                text = vehicle.fuel
            )
            DetailCol(
                iconResource = Res.drawable.transmission,
                label = "Transmission",
                text = vehicle.transmission
            )
            DetailCol(
                iconResource = Res.drawable.configuration,
                label = "Configuration",
                text = vehicle.driveConfiguration
            )
            DetailCol(
                icon = Icons.Default.Bolt,
                label = "Engine",
                text = if (vehicle.engineCapacity?.isDigitsOnlyString() == true) "${vehicle.engineCapacity} CC" else vehicle.engineCapacity
            )
            DetailCol(
                icon = Icons.Default.DirectionsCar,
                label = "Body",
                text = vehicle.bodyType
            )
            DetailCol(
                iconResource = Res.drawable.weight,
                label = "Weight",
                text = if (vehicle.gvw !== null) formatNumber(
                    number = vehicle.gvw.toDouble(),
                    decimals = 0
                ) else vehicle.gvw
            )
            DetailCol(
                icon = Icons.Default.AirlineSeatReclineExtra,
                label = "Seats",
                text = if (vehicle.seating == null) "_" else "${vehicle.seating}"
            )

        }

    }
}

@Composable
fun VehicleDetails(
    vehicle: Vehicle,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
//            .verticalScroll(state = rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header Section
        VehicleDetailsHeader(vehicle = vehicle)

        // Main Details Grid
        VehicleDetailsGrid(vehicle = vehicle)

        // Technical Specifications Section
        TechnicalSpecificationsSection(vehicle = vehicle)
    }
}

@Composable
private fun VehicleDetailsHeader(vehicle: Vehicle) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = vehicle.model ?: "Unknown Model",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vehicle.make ?: "Unknown Make",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "KES ${vehicle.crsp?.let { formatNumber(number = it, decimals = 2) } ?: "N/A"}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun VehicleDetailsGrid(vehicle: Vehicle) {
    Text(
        text = "Vehicle Information",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 280.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.heightIn(min = 200.dp, max = 600.dp)
    ) {
        item {
            DetailCard(
                icon = Icons.Default.DirectionsTransit,
                label = "Model Number",
                value = if (vehicle.modelNumber.isNullOrEmpty()) "Not specified" else vehicle.modelNumber
            )
        }

        item {
            DetailCard(
                icon = Icons.Default.LocalGasStation,
                label = "Fuel Type",
                value = vehicle.fuel ?: "Not specified"
            )
        }

        item {
            DetailCard(
                iconResource = Res.drawable.transmission,
                label = "Transmission",
                value = vehicle.transmission ?: "Not specified"
            )
        }

        item {
            DetailCard(
                iconResource = Res.drawable.configuration,
                label = "Drive Configuration",
                value = vehicle.driveConfiguration ?: "Not specified"
            )
        }

        item {
            DetailCard(
                icon = Icons.Default.DirectionsCar,
                label = "Body Type",
                value = vehicle.bodyType ?: "Not specified"
            )
        }

        item {
            DetailCard(
                icon = Icons.Default.AirlineSeatReclineExtra,
                label = "Seating Capacity",
                value = vehicle.seating?.let { "$it seats" } ?: "Not specified"
            )
        }
    }
}

@Composable
private fun TechnicalSpecificationsSection(vehicle: Vehicle) {
    Text(
        text = "Technical Specifications",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TechnicalSpecItem(
                icon = Icons.Default.Bolt,
                label = "Engine Capacity",
                value = if (vehicle.engineCapacity?.isDigitsOnlyString() == true)
                    "${vehicle.engineCapacity} CC"
                else
                    vehicle.engineCapacity ?: "Not specified"
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            TechnicalSpecItem(
                iconResource = Res.drawable.weight,
                label = "Gross Vehicle Weight",
                value = vehicle.gvw?.let {
                    "${formatNumber(number = it.toDouble(), decimals = 0)} kg"
                } ?: "Not specified"
            )
        }
    }
}

@Composable
private fun DetailCard(
    icon: ImageVector? = null,
    iconResource: DrawableResource? = null,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        icon != null -> Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        iconResource != null -> Icon(
                            painter = painterResource(iconResource),
                            contentDescription = label,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TechnicalSpecItem(
    icon: ImageVector? = null,
    iconResource: DrawableResource? = null,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    icon != null -> Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    iconResource != null -> Icon(
                        painter = painterResource(iconResource),
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun DetailCol(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String, text: String?
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.onBackground
                ), contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp)
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Light
            )
            Text(
                text = text ?: "_", fontWeight = FontWeight.Bold,
            )
        }

    }
}


@Composable
fun DetailCol(
    modifier: Modifier = Modifier,
    iconResource: DrawableResource,
    label: String, text: String?
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.onBackground
                ), contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(resource = iconResource),
                contentDescription = label,
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp)
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Light
            )
            Text(
                text = text ?: "_", fontWeight = FontWeight.Bold,
            )
        }

    }
}