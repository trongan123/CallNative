package com.example.callnative.domain.usecase

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

class VideoCallUseCase @Inject constructor() {
    private val _recorder = Recorder.Builder()
        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
        .build()

    private val _videoCapture = VideoCapture.withOutput(_recorder)
    val videoCapture: VideoCapture<Recorder> get() = _videoCapture

    private val _cameraLens = MutableStateFlow(CameraSelector.LENS_FACING_FRONT)
    val cameraLens: StateFlow<Int> = _cameraLens.asStateFlow()

    var previewView: PreviewView? = null
    var lifecycleOwner: LifecycleOwner? = null

    fun handleRecordVideo(
        context: Context,
        cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        this.previewView = previewView
        this.lifecycleOwner = lifecycleOwner

        startCamera(context, cameraProviderFuture)
    }


    fun startCamera(ctx: Context, cameraProviderFuture: ListenableFuture<ProcessCameraProvider>) {
        if (previewView == null || lifecycleOwner == null) return

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView!!.surfaceProvider
            }
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(cameraLens.value)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner!!,
                    cameraSelector,
                    preview,
                    videoCapture
                )
            } catch (_: Exception) {
                return@addListener
            }
        }, ContextCompat.getMainExecutor(ctx))
    }


    fun stopCamera(ctx: Context, cameraProviderFuture: ListenableFuture<ProcessCameraProvider>) {

        cameraProviderFuture.addListener({
            try {
                val provider = cameraProviderFuture.get()
                provider.unbindAll()
            } catch (_: Exception) {
                return@addListener
            }
        }, ContextCompat.getMainExecutor(ctx))
    }


    @SuppressLint("MissingPermission")
    fun startRecording(
        context: Context,
        videoCapture: VideoCapture<Recorder>,
        recording: MutableState<Recording?>
    ) {
        val file = File(context.codeCacheDir, "video.mp4")
        val outputOptions = FileOutputOptions.Builder(file).build()

        recording.value = videoCapture.output.prepareRecording(context, outputOptions)
            .apply { withAudioEnabled() }
            .start(ContextCompat.getMainExecutor(context)) { _ ->

            }
    }

    fun stopRecording(recording: MutableState<Recording?>) {
        recording.value?.stop()
        recording.value = null
    }

    fun switchCamera() {
        _cameraLens.value = if (_cameraLens.value == CameraSelector.LENS_FACING_BACK)
            CameraSelector.LENS_FACING_FRONT
        else
            CameraSelector.LENS_FACING_BACK
    }
}