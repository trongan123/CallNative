package com.example.callnative.presentation.ui.views

import android.content.Context
import android.view.ViewGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.callnative.presentation.viewmodel.CallViewModel
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer

@Composable
fun VideoCallView(
    viewModel: CallViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val eglBase = remember { EglBase.create() }

    DisposableEffect(Unit) {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .createInitializationOptions()
        )

        onDispose { }
    }


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

            // Setup camera capturer
            val peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(PeerConnectionFactory.Options())
                .createPeerConnectionFactory()

            val videoSource = peerConnectionFactory.createVideoSource(false)
            val videoCapturer = createCameraCapturer(ctx)

            videoCapturer?.initialize(
                SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext),
                ctx,
                videoSource.capturerObserver
            )
            videoCapturer?.startCapture(1280, 720, 30)

            val videoTrack = peerConnectionFactory.createVideoTrack("DemoApp", videoSource)
            videoTrack.addSink(surfaceViewRenderer)

            surfaceViewRenderer
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun createCameraCapturer(context: Context): CameraVideoCapturer? {
    val enumerator = Camera2Enumerator(context)
    for (deviceName in enumerator.deviceNames) {
        if (enumerator.isFrontFacing(deviceName)) {
            return enumerator.createCapturer(deviceName, null)
        }
    }
    return null
}