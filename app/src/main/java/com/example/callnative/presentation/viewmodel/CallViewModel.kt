package com.example.callnative.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.callnative.common.utils.CoroutineUtils
import com.example.callnative.data.enums.CallType
import com.example.callnative.data.models.UserEntity
import com.example.callnative.domain.usecase.AnswerCallUseCase
import com.example.callnative.domain.usecase.AudioCallUseCase
import com.example.callnative.domain.usecase.CallDurationUseCase
import com.example.callnative.domain.usecase.ClickEndCallUseCase
import com.example.callnative.domain.usecase.ClickSpeakerUseCase
import com.example.callnative.domain.usecase.DeclineCallUseCase
import com.example.callnative.domain.usecase.PermissionCallUseCase
import com.example.callnative.domain.usecase.VideoCallUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpSender
import org.webrtc.SurfaceViewRenderer
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CallViewModel @Inject constructor(
    private val permissionCallUseCase: PermissionCallUseCase,
    private val videoCallUseCase: VideoCallUseCase,
    private val clickEndCallUseCase: ClickEndCallUseCase,
    private val audioCallUseCase: AudioCallUseCase,
    private val clickSpeakerUseCase: ClickSpeakerUseCase,
    private val callDurationUseCase: CallDurationUseCase,
    private val answerCallUseCase: AnswerCallUseCase,
    private val declineCallUseCase: DeclineCallUseCase
) : ViewModel() {

    // Mock data
    private var _calleeData = MutableStateFlow<UserEntity>(UserEntity.default)
    val calleeData: StateFlow<UserEntity> = _calleeData

    private val _callerData = MutableStateFlow<UserEntity>(UserEntity.default)
    val callerData: StateFlow<UserEntity> = _callerData

    private val _icCall = MutableStateFlow(false)
    val isCall: StateFlow<Boolean> = _icCall

    private val _callType = MutableStateFlow<CallType>(CallType.VOICE_CALL)
    val callType: StateFlow<CallType> = _callType

    //Connection WebRTC
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var audioSender: RtpSender? = null
    private var videoSender: RtpSender? = null

    fun initPeerConnectionFactory() {
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(PeerConnectionFactory.Options())
            .createPeerConnectionFactory()
    }

    fun toggleSpeaker() {
        clickSpeakerUseCase.handleToggleSpeakerCall()
    }

    fun getHasSpeaker(): StateFlow<Boolean> {
        return clickSpeakerUseCase.hasSpeaker
    }

    fun toggleVideo() {
        videoCallUseCase.handleToggleVideo()
    }

    fun getHasVideo(): StateFlow<Boolean> {
        return videoCallUseCase.hasVideo
    }

    fun toggleMicrophone() {
        audioCallUseCase.handleToggleMicrophone()
    }

    fun getHasMicrophone(): StateFlow<Boolean> {
        return audioCallUseCase.hasMicrophone
    }

    fun handleDecline() {
        _icCall.value = false
    }

    fun handleAnswer(isCallVideo: Boolean) {
        _icCall.value = false
        answerCallUseCase.handleAnswerCall(isCallVideo)
    }

    fun getCallState(): StateFlow<String> {
        return callDurationUseCase.callState
    }

    fun releaseCamera(surfaceViewRenderer :SurfaceViewRenderer?){
        if(surfaceViewRenderer == null) return

        videoCallUseCase.releaseCamera(surfaceViewRenderer)
    }

    fun openPermission(isCallVideo: Boolean) {
        permissionCallUseCase.handlePermissionCall(isCallVideo, onGranted = {
            _icCall.value = true
            _callType.value = if (isCallVideo) CallType.VIDEO_CALL else CallType.VOICE_CALL
            handleCallInfo()
        }, onDenied = {
            _icCall.value = false
        })
    }

    fun handleCamara(
        context: Context,
        eglBase: EglBase,
        surfaceViewRenderer: SurfaceViewRenderer
    ) {

        initPeerConnectionFactory()

        CoroutineUtils.launchBackground {
            videoCallUseCase.handleRecordVideo(
                context,
                eglBase,
                surfaceViewRenderer,
                peerConnectionFactory
            )
        }
    }

    fun handleAudio() {
        if (peerConnectionFactory == null) {
            initPeerConnectionFactory()
        }
        CoroutineUtils.launchBackground {
            audioCallUseCase.handleAudioCall(peerConnectionFactory)
        }
    }

    fun handleSwitchCamera() {
        videoCallUseCase.switchCamera()
    }

    fun handleEndCall() {
        CoroutineUtils.launchBackground {
            _icCall.value = false
            clickEndCallUseCase.handelEndCall()
        }
    }

    fun handleCallInfo() {
        viewModelScope.launch {
            // Callee & Caller data
            _calleeData.value = UserEntity(
                userId = UUID.randomUUID().toString(),
                avatarImageUrl = "https://images.pexels.com/photos/29879483/pexels-photo-29879483/free-photo-of-festive-christmas-ornament-on-pine-tree-branch.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                displayName = "Thomas Editor"
            )
            _callerData.value = UserEntity(
                userId = UUID.randomUUID().toString(),
                avatarImageUrl = "https://images.pexels.com/photos/11288126/pexels-photo-11288126.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1",
                displayName = "Issac NewYork"
            )
        }
    }

    fun setupData(callType: CallType) {
        viewModelScope.launch {
            handleCallInfo()
            // Setup default value for button
            val isVideoCall = callType == CallType.VIDEO_CALL

            videoCallUseCase.setHasVideo(isVideoCall)
            clickSpeakerUseCase.setHasSpeaker(isVideoCall)
            audioCallUseCase.setHasMicrophone(isVideoCall)

            callDurationUseCase.startCallTimer()
        }
    }

}