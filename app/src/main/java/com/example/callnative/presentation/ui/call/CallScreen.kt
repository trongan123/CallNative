package com.example.callnative.presentation.ui.call

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.callnative.R
import com.example.callnative.common.utils.NavigationUtils
import com.example.callnative.data.enums.CallType
import com.example.callnative.data.enums.ProfileViewSize
import com.example.callnative.presentation.ui.views.CallView
import com.example.callnative.presentation.ui.views.HorizontalButton
import com.example.callnative.presentation.ui.views.LabelButtonView
import com.example.callnative.presentation.viewmodel.CallViewModel
import kotlin.math.roundToInt

object CallScreen {
    const val ROUTE = "callScreen"

    @Composable
    fun Screen(
        viewModel: CallViewModel = hiltViewModel()
    ) {
        var callType = NavigationUtils.getSavedStateHandle()?.get<CallType>("CallType")
        viewModel.setupData(callType = callType ?: CallType.VOICE_CALL)
        viewModel.initPeerConnectionFactory()
        viewModel.handleAudio()
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        )
        { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .systemBarsPadding()
            ) {
                // Bind ViewModel
                val calleeData by viewModel.calleeData.collectAsState()
                val callerData by viewModel.callerData.collectAsState()
                val callState by viewModel.getCallState().collectAsState()

                val hasVideo by viewModel.getHasVideo().collectAsState()

                // Drag&Drop values
                val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                val boxSizeWidth = 112.dp
                val boxSizeHeight = 120.dp

                val density = LocalDensity.current
                val screenWidthPx = with(density) { screenWidth.toPx() }
                val screenHeightPx = with(density) { screenHeight.toPx() }
                val boxSizePx = with(density) { boxSizeWidth.toPx() }

                var offsetX by remember {
                    mutableFloatStateOf(
                        screenWidthPx - boxSizePx - with(
                            density
                        ) { 16.dp.toPx() })
                }
                var offsetY by remember { mutableFloatStateOf(with(density) { 0.dp.toPx() }) }
                val animatedOffsetX by animateFloatAsState(targetValue = offsetX)
                val animatedOffsetY by animateFloatAsState(targetValue = offsetY)
                val maxOffsetY =
                    screenHeightPx - with(density) { boxSizeHeight.toPx() } - with(density) { 250.dp.toPx() }

                // Remote user
                Box {
                    CallView(
                        profileSize = ProfileViewSize.FULLSCREEN,
                        avatarImageUrl = calleeData.avatarImageUrl,
                        displayName = calleeData.displayName,
                        callState = callState,
                        defaultAvatarResId = R.drawable.img_avatar_default
                    )

                    // Local user
                    if (callType == CallType.VIDEO_CALL) {
                        Box(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        animatedOffsetX.roundToInt(),
                                        animatedOffsetY.roundToInt()
                                    )
                                }
                                .padding(top = 50.dp, end = 16.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        offsetX = (offsetX + dragAmount.x)
                                            .coerceIn(0f, screenWidthPx - boxSizePx)
                                        offsetY = (offsetY + dragAmount.y)
                                            .coerceIn(0f, maxOffsetY)
                                    }
                                }
                        ) {
                            CallView(
                                profileSize = ProfileViewSize.MINIMIZE,
                                avatarImageUrl = callerData.avatarImageUrl,
                                displayName = callerData.displayName,
                                defaultAvatarResId = R.drawable.img_avatar_default,
                                hasVideo = hasVideo
                            )
                        }
                    }
                }

                // Bottom Buttons
                HorizontalButton(callType = callType)

            }
        }

    }
}