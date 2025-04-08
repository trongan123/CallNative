package com.example.callnative.presentation.ui.views

import android.content.Context
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.callnative.R
import com.example.callnative.data.enums.CallType
import com.example.callnative.presentation.viewmodel.CallViewModel

@Composable
fun HorizontalButton(
    viewModel: CallViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
    callType: CallType?
) {
    val hasSpeaker by viewModel.hasSpeaker.collectAsState()
    val hasMicrophone by viewModel.hasMicrophone.collectAsState()
    val hasVideo by viewModel.hasVideo.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabelButtonView(
                iconResId = if (hasSpeaker) R.drawable.ic_speaker_enable else R.drawable.ic_speaker_disable,
                label = if (hasSpeaker) "Speaker out" else "Speaker in",
                onClick = {
                    //
                    viewModel.toggleSpeaker()
                })
            LabelButtonView(
                iconResId = if (hasVideo) R.drawable.ic_camera_enable else R.drawable.ic_camera_disable,
                label = "Camera",
                callType == CallType.VIDEO_CALL,
                onClick = {
                    // Handle camera action
                    viewModel.toggleVideo(context)
                },
            )
            LabelButtonView(
                iconResId = if (hasMicrophone) R.drawable.ic_microphone_enable else R.drawable.ic_microphone_disable,
                label = if (hasMicrophone) "Mute" else "Un mute",
                onClick = {
                    //
                    viewModel.toggleMicrophone()
                })
            LabelButtonView(iconResId = R.drawable.ic_end, label = "End", onClick = {
                //
                viewModel.handleEndCall(context)
            })
        }
    }
}
