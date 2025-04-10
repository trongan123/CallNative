package com.example.callnative.domain.usecase

import com.example.callnative.common.utils.CoroutineUtils
import com.example.callnative.common.utils.NavigationUtils
import com.example.callnative.presentation.ui.MainScreen
import javax.inject.Inject

class ClickEndCallUseCase @Inject constructor() {

    fun handelEndCall(){
        CoroutineUtils.launchOnMain {
            NavigationUtils.popBackStack()
        }
    }
}