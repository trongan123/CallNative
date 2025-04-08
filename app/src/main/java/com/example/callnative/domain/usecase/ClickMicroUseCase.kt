package com.example.callnative.domain.usecase

import com.example.callnative.common.utils.CoroutineUtils
import javax.inject.Inject

class ClickMicroUseCase @Inject constructor() {

    fun handleMicroCall(){
        CoroutineUtils.launchBackground {

        }
    }
}