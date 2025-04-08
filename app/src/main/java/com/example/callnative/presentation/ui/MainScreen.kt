package com.example.callnative.presentation.ui

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.callnative.common.utils.NavigationUtils
import com.example.callnative.data.enums.CallType
import com.example.callnative.presentation.ui.call.CallScreen
import com.example.callnative.presentation.viewmodel.CallViewModel

object MainScreen {
    const val ROUTE = "mainScreen"

    @Composable
    fun Screen(
        callViewModel: CallViewModel = hiltViewModel()
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        )
        { innerPadding ->
            Row(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 100.dp)) {

                Column(modifier = Modifier.fillMaxSize()) {
                    Button(onClick = {
                        // Mở VideoCallActivity
                        Log.e("TAG Call", "Screen: call video " )
                        callViewModel.openPermission(true)
                    }) {
                        Text("Video Call", color = Color.White)
                    }
                    Button(onClick = {
                        // Mở VoiceCallActivity
                        Log.e("TAG Call", "Screen: call voice " )
                        callViewModel.openPermission(false)
                    }) {
                        Text("Voice Call", color = Color.White)
                    }
                }
            }
        }

    }
}