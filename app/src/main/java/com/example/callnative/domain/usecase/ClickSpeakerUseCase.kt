package com.example.callnative.domain.usecase

import com.example.callnative.common.utils.CoroutineUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ClickSpeakerUseCase @Inject constructor() {

    private val _hasSpeaker = MutableStateFlow(false)
     val hasSpeaker: StateFlow<Boolean> = _hasSpeaker

    fun handleToggleSpeakerCall() {
        CoroutineUtils.launchBackground {
            _hasSpeaker.value = !_hasSpeaker.value
        }
    }

    fun setHasSpeaker(hasSpeaker: Boolean) {
        _hasSpeaker.value = hasSpeaker
    }
}