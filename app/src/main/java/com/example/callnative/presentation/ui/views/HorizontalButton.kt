package com.example.callnative.presentation.ui.views

import android.content.Context
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.callnative.R
import com.example.callnative.presentation.viewmodel.CallViewModel

@Composable
fun HorizontalButton(
    viewModel: CallViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    val hasSpeaker by viewModel.getHasSpeaker().collectAsState()
    val hasMicrophone by viewModel.getHasMicrophone().collectAsState()
    val hasVideo by viewModel.getHasVideo().collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateBottomPadding(),
                    start = 5.dp,
                    end = 5.dp
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabelButtonView(
                modifier = Modifier
                    .weight(1f)
                    .focusGroup(),
                iconResId = if (hasSpeaker) R.drawable.ic_speaker_enable else R.drawable.ic_speaker_disable,
                label = context.getString(R.string.speaker),
                size = 60.dp,
                onClick = {
                    viewModel.toggleSpeaker()
                })
            LabelButtonView(
                modifier = Modifier
                    .weight(1f)
                    .focusGroup()
                    .padding(0.dp),
                iconResId = if (hasVideo) R.drawable.ic_camera_enable else R.drawable.ic_camera_disable,
                label = context.getString(R.string.camera),
                size = 60.dp,
                onClick = {
                    // Handle camera action
                    viewModel.toggleVideo()
                },
            )
            LabelButtonView(
                modifier = Modifier
                    .weight(1f)
                    .focusGroup(),
                iconResId = if (hasMicrophone) R.drawable.ic_micro_enable else R.drawable.ic_micro_disable,
                label = context.getString(R.string.mute),
                size = 60.dp,
                onClick = {
                    viewModel.toggleMicrophone()
                })
            LabelButtonView(
                modifier = Modifier
                    .weight(1f)
                    .focusGroup(),
                iconResId = R.drawable.ic_end_call,
                label = context.getString(R.string.end),
                size = 60.dp,
                onClick = {
                    viewModel.handleEndCall()
                })
        }
    }
}
