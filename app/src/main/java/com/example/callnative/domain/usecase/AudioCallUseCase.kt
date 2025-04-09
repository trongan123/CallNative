package com.example.callnative.domain.usecase

import com.example.callnative.common.utils.CoroutineUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnectionFactory
import javax.inject.Inject

class AudioCallUseCase @Inject constructor() {

    private val audioTrackId = "AudioCall"
    private var audioTrackLocal: AudioTrack? = null
    private var audioSource: AudioSource? = null

    private val _hasMicrophone = MutableStateFlow(true)
    val hasMicrophone: StateFlow<Boolean> = _hasMicrophone

    fun handleAudioCall(
        peerConnectionFactory: PeerConnectionFactory?
    ) {
        audioSource = peerConnectionFactory?.createAudioSource(MediaConstraints())
        handleAudioStack(peerConnectionFactory)
    }

    private fun handleAudioStack(
        peerConnectionFactory: PeerConnectionFactory?
    ) {
        audioTrackLocal = peerConnectionFactory?.createAudioTrack(audioTrackId, audioSource)
    }


    fun handleToggleMicrophone() {
        CoroutineUtils.launchBackground {
            _hasMicrophone.value = !_hasMicrophone.value
        }
    }

    fun setHasMicrophone(hasMicrophone: Boolean) {
        _hasMicrophone.value = hasMicrophone
    }
}