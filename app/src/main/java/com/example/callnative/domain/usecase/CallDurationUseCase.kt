package com.example.callnative.domain.usecase

import android.annotation.SuppressLint
import com.example.callnative.common.utils.CoroutineUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

// Timer & CallState
class CallDurationUseCase @Inject constructor() {

    private var callDurationSeconds = 0

    private val _callState = MutableStateFlow("00:00")
    val callState: StateFlow<String> get() = _callState

    // Setup and Start Timer
    fun startCallTimer() {
        CoroutineUtils.launchBackground {
            _callState.value = "Connected"
            while (true) {
                delay(1000)
                callDurationSeconds++
                _callState.value = formatCallDuration(callDurationSeconds)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatCallDuration(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }

}