package com.example.callnative.domain.usecase

import com.example.callnative.common.utils.CoroutineUtils
import com.example.callnative.common.utils.NavigationUtils
import javax.inject.Inject

class DeclineCallUseCase @Inject constructor() {

    fun handleDeclineCall() {
        CoroutineUtils.launchBackground {

            NavigationUtils.popBackStack()
        }
    }

}