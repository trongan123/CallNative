package com.example.callnative.presentation.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.callnative.data.enums.CallType
import com.example.callnative.presentation.ui.call.NotiCallScreen
import com.example.callnative.presentation.viewmodel.CallViewModel
import org.webrtc.PeerConnectionFactory

object MainScreen {
    const val ROUTE = "mainScreen"

    @Composable
    fun Screen(
        viewModel: CallViewModel = hiltViewModel(),
        context: Context = LocalContext.current
    ) {

        val isCall = viewModel.isCall.collectAsState()
        val callType = viewModel.callType.collectAsState()

        DisposableEffect(Unit) {
            PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(context)
                    .createInitializationOptions()
            )

            onDispose { }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        )
        { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        // Mở VideoCallActivity
                        viewModel.openPermission(true)
                    }) {
                        Text("Video Call", color = Color.White)
                    }
                    Button(onClick = {
                        // Mở VoiceCallActivity
                        viewModel.openPermission(false)
                    }) {
                        Text("Voice Call", color = Color.White)
                    }
                }
            }
        }

        if (isCall.value) {
            NotiCallScreen.Screen(isCallVideo = callType.value == CallType.VIDEO_CALL)
        }

    }


}