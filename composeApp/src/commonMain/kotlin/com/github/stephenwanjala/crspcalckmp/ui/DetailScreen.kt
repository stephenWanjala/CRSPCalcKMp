package com.github.stephenwanjala.crspcalckmp.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AirlineSeatReclineExtra
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.rounded.LocalOffer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
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