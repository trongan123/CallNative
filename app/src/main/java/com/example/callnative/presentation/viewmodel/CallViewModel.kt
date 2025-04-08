package com.example.callnative.presentation.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callnative.common.utils.CoroutineUtils
import com.example.callnative.common.utils.NavigationUtils
import com.example.callnative.data.enums.CallType
import com.example.callnative.data.models.UserEntity
import com.example.callnative.domain.usecase.ClickEndCallUseCase
import com.example.callnative.domain.usecase.PermissionCallUseCase
import com.example.callnative.domain.usecase.VideoCallUseCase
import com.example.callnative.presentation.ui.call.CallScreen
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val permissionCallUseCase: PermissionCallUseCase,
    private val videoCallUseCase: VideoCallUseCase,
    private val clickEndCallUseCase: ClickEndCallUseCase
) : ViewModel() {


    var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null

    // Mock data
    val calleeData = MutableStateFlow<UserEntity>(UserEntity.default)
    val callerData = MutableStateFlow<UserEntity>(UserEntity.default)

    // Device handler
    private var _callType = MutableStateFlow<CallType>(CallType.VOICE_CALL)
    val callType: StateFlow<CallType> = _callType

    private val _hasSpeaker = MutableStateFlow(false)
    val hasSpeaker: StateFlow<Boolean> = _hasSpeaker

    private val _hasVideo = MutableStateFlow(false)
    val hasVideo: StateFlow<Boolean> = _hasVideo

    private val _hasMicrophone = MutableStateFlow(true)
    val hasMicrophone: StateFlow<Boolean> = _hasMicrophone

    // Timer & CallState
    private var callDurationSeconds = 0
    private val _callState = MutableStateFlow("00:00")
    val callState: StateFlow<String> get() = _callState

    fun setupData(callType: CallType) {
        viewModelScope.launch {
            _callType.value = callType

            // Callee & Caller data
            calleeData.value = UserEntity(
                userId = UUID.randomUUID().toString(),
                avatarImageUrl = "https://images.pexels.com/photos/29879483/pexels-photo-29879483/free-photo-of-festive-christmas-ornament-on-pine-tree-branch.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                displayName = "Thomas Editor"
            )
            callerData.value = UserEntity(
                userId = UUID.randomUUID().toString(),
                avatarImageUrl = "https://images.pexels.com/photos/11288126/pexels-photo-11288126.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                displayName = "Issac NewYork"
            )

            // Setup default value for button
            _hasVideo.value = callType == CallType.VIDEO_CALL
            _hasSpeaker.value = callType == CallType.VIDEO_CALL
            _hasMicrophone.value = callType == CallType.VOICE_CALL

            // Setup and Start Timer
            delay(2000)
            _callState.value = "Connected"
            startCallTimer()
        }
    }

    private fun startCallTimer() {
        viewModelScope.launch {
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

    fun toggleSpeaker() {
        _hasSpeaker.value = !_hasSpeaker.value
    }

    fun toggleVideo(context: Context) {
        _callType.value = CallType.VIDEO_CALL
        _hasVideo.value = !_hasVideo.value

        if (cameraProviderFuture == null) return

        if (_hasVideo.value) {
            videoCallUseCase.startCamera(context, cameraProviderFuture!!)
        } else {
            videoCallUseCase.stopCamera(context, cameraProviderFuture!!)
        }

    }

    fun toggleMicrophone() {
        _hasMicrophone.value = !_hasMicrophone.value
    }

    fun openPermission(isCallVideo: Boolean) {
        permissionCallUseCase.handlePermissionCall(isCallVideo, onGranted = {
            NavigationUtils.savedStateHandle(
                "CallType",
                if (isCallVideo) CallType.VIDEO_CALL else CallType.VOICE_CALL
            )
            NavigationUtils.navigate(CallScreen.ROUTE)
        }, null)

    }

    fun handleCamara(
        ctx: Context,
        cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        CoroutineUtils.launchBackground {
            this.cameraProviderFuture = cameraProviderFuture
            videoCallUseCase.handleRecordVideo(
                ctx,
                cameraProviderFuture,
                previewView,
                lifecycleOwner
            )
        }
    }

    fun switchCamera() {
        videoCallUseCase.switchCamera()
    }

    fun stopCamera(ctx: Context) {
        if (cameraProviderFuture == null) return
        videoCallUseCase.stopCamera(ctx, cameraProviderFuture!!)
    }

    fun handleEndCall(ctx: Context) {
        CoroutineUtils.launchBackground {
            stopCamera(ctx)
            clickEndCallUseCase.handelEndCall()
        }

    }

}