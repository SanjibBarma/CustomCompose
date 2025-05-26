package com.example.customcompose.views.compose.helper_compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView

@Composable
fun KeepScreenOnEffect() {
    val view = LocalView.current
    SideEffect {
        view.keepScreenOn = true
    }
}
