package com.example.callnative.presentation.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LabelButtonView(
    modifier: Modifier?,
    iconResId: Int,
    label: String,
    enable: Boolean = true,
    onClick: () -> Unit
) {

    Column(
        modifier = modifier ?: Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconView(
            iconRes = iconResId,
            size = 50,
            onclick = onClick,
            bottom = 5.dp
        )
        Text(text = label, color = Color.White, fontSize = 16.sp)
    }
}
