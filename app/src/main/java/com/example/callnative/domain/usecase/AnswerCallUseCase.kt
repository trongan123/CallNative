package com.example.callnative.domain.usecase

import com.example.callnative.common.utils.CoroutineUtils
import com.example.callnative.common.utils.NavigationUtils
import com.example.callnative.data.enums.CallType
import com.example.callnative.presentation.ui.call.CallScreen
import javax.inject.Inject

class AnswerCallUseCase @Inject constructor() {

    fun handleAnswerCall(isCallVideo: Boolean) {
        CoroutineUtils.launchOnMain {
            NavigationUtils.savedStateHandle(
                "CallType",
                if (isCallVideo) CallType.VIDEO_CALL else CallType.VOICE_CALL
            )
            NavigationUtils.navigate(CallScreen.ROUTE)
        }
    }

}