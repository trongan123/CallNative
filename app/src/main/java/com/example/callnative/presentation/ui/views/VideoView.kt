package com.example.callnative.presentation.ui.views

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.callnative.presentation.viewmodel.CallViewModel
import org.webrtc.EglBase
import org.webrtc.SurfaceViewRenderer

@Composable
fun VideoView(
    viewModel: CallViewModel = hiltViewModel()
) {
    val eglBase = remember { EglBase.create() }

    AndroidView(
        factory = { ctx ->
            val surfaceViewRenderer = SurfaceViewRenderer(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                init(eglBase.eglBaseContext, null)
                setMirror(true)
            }
            viewModel.handleCamara(ctx, eglBase, surfaceViewRenderer)
            surfaceViewRenderer
        },
        modifier = Modifier.fillMaxSize()
    )
}