package com.example.callnative.presentation.ui.views

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

    var surfaceViewRenderer: SurfaceViewRenderer? = null
    AndroidView(
        factory = { ctx ->
            surfaceViewRenderer = SurfaceViewRenderer(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                init(eglBase.eglBaseContext, null)
                setMirror(true)

                post {
                    viewModel.handleCamara(ctx, eglBase, this)
                }
            }
            surfaceViewRenderer
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose {
            viewModel.releaseCamera(surfaceViewRenderer)
        }
    }
}