package com.example.callnative.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val ColorScheme.black50: Color
    @Composable
    get() = black_50

val ColorScheme.color_text_type_call: Color
    @Composable
    get() = if (isSystemInDarkTheme()) color_text_type_call_dark else color_text_type_call_light
