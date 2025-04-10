package com.example.callnative.domain.usecase

import android.content.Context
import com.example.callnative.common.utils.CoroutineUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack
import javax.inject.Inject

class VideoCallUseCase @Inject constructor() {

    private val videoTrackId = "VideoCall"
    private val captureThread = "CaptureThread"
    private var videoSource: VideoSource? = null
    private var videoCapturer: CameraVideoCapturer? = null
    private var videoTrackLocal: VideoTrack? = null

    private val _hasVideo = MutableStateFlow(false)
    val hasVideo: StateFlow<Boolean> = _hasVideo

    fun handleRecordVideo(
        context: Context,
        eglBase: EglBase,
        surfaceViewRenderer: SurfaceViewRenderer,
        peerConnectionFactory: PeerConnectionFactory?
    ) {
        videoSource = peerConnectionFactory?.createVideoSource(false)
        videoCapturer = createCameraCapturer(context)

        videoCapturer?.initialize(
            SurfaceTextureHelper.create(captureThread, eglBase.eglBaseContext),
            context,
            videoSource?.capturerObserver
        )
        videoCapturer?.startCapture(1280, 720, 30)

        handleVideoStack(surfaceViewRenderer, peerConnectionFactory)
    }

    private fun handleVideoStack(
        surfaceViewRenderer: SurfaceViewRenderer,
        peerConnectionFactory: PeerConnectionFactory?
    ) {
        videoTrackLocal = peerConnectionFactory?.createVideoTrack(videoTrackId, videoSource)
        videoTrackLocal?.addSink(surfaceViewRenderer)
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

    fun handleToggleVideo() {
        CoroutineUtils.launchBackground {
            _hasVideo.value = !_hasVideo.value

            if (_hasVideo.value) {
                enableVideo()
            } else {
                disableVideo()
            }
        }
    }

    fun setHasVideo(hasVideo: Boolean) {
        _hasVideo.value = hasVideo
    }


    fun getVideoTrackLocal(): VideoTrack? = videoTrackLocal

    fun switchCamera() {
        videoCapturer?.switchCamera(null)
    }

    private fun enableVideo() {
        videoTrackLocal?.setEnabled(true)
    }

    // vẫn giữ kết nối, vẫn bật video nhưng không chuyền video cho connection
    private fun disableVideo() {
        videoTrackLocal?.setEnabled(false)
    }

    fun releaseCamera(surfaceViewRenderer: SurfaceViewRenderer? = null) {
        CoroutineUtils.launchBackground {
            try {
                videoCapturer?.stopCapture()
                videoCapturer?.dispose()
                videoSource?.dispose()
                videoTrackLocal?.removeSink(surfaceViewRenderer)
                surfaceViewRenderer?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            videoCapturer = null
            videoSource = null
            videoTrackLocal = null
            _hasVideo.value = false
        }
    }
}