package com.github.stephenwanjala.crspcalckmp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.stephenwanjala.crspcalckmp.ui.CRSPCalcNav
import com.github.stephenwanjala.crspcalckmp.ui.CRSPCalcTheme
import crspcalckmp.composeapp.generated.resources.Res
import crspcalckmp.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
@Preview
fun App() {
    CRSPCalcTheme {
        SharedTransitionLayout {
            val navController = rememberNavController()
            CRSPCalcNav(
                navController = navController,
                modifier = Modifier,
                sharedTransitionScope=this
            )
        }
    }
}