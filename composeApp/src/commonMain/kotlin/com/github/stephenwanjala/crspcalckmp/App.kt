package com.github.stephenwanjala.crspcalckmp

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.stephenwanjala.crspcalckmp.ui.CRSPCalcNav
import com.github.stephenwanjala.crspcalckmp.ui.CRSPCalcTheme
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