package com.example.callnative.domain.usecase

import com.example.callnative.common.utils.CoroutineUtils
import javax.inject.Inject

class ClickSpeakerUseCase @Inject constructor() {

    fun handleSpeakerCall(){
        CoroutineUtils.launchBackground {

        }
    }
}