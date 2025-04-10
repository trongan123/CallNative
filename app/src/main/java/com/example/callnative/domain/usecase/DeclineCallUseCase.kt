package com.example.callnative.domain.usecase

import com.example.callnative.common.utils.CoroutineUtils
import javax.inject.Inject

class DeclineCallUseCase @Inject constructor() {

    fun handleDeclineCall() {
        CoroutineUtils.launchBackground {

        }
    }

}