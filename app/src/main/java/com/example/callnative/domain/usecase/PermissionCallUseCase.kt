package com.example.callnative.domain.usecase

import android.Manifest
import com.example.permission.AppPermission
import com.example.permission.AppPermissionUtils
import com.example.permission.PermissionListener
import com.example.permission.R
import javax.inject.Inject

class PermissionCallUseCase @Inject constructor() {

    fun handlePermissionCall(
        isCallVideo: Boolean,
        onGranted: (() -> Unit)?,
        onDenied: (() -> Unit)?
    ) {
        if (isCallVideo) {
            handlePermissionCallVideo(onGranted, onDenied)
            return
        }

        handlePermissionCallAudio(onGranted, onDenied)
    }

    private fun handlePermissionCallVideo(onGranted: (() -> Unit)?, onDenied: (() -> Unit)?) {
        if (checkSupportCallVideo()) {
            onGranted?.invoke()
            return
        }

        AppPermission.create().setPermissions(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )
            .setDeniedMessage(R.string.permission_denied_message)
            .setListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    onGranted?.invoke()
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    onDenied?.invoke()
                }
            })
            .check()
    }

    private fun handlePermissionCallAudio(onGranted: (() -> Unit)?, onDenied: (() -> Unit)?) {
        if (checkSupportCallAudio()) {
            onGranted?.invoke()
            return
        }

        AppPermission.create().setPermissions(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )
            .setDeniedMessage(R.string.permission_denied_message)
            .setListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    onGranted?.invoke()
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    onDenied?.invoke()
                }
            })
            .check()
    }

    private fun checkSupportCallVideo(): Boolean {
        return AppPermissionUtils.checkPermission(Manifest.permission.CAMERA)
                && AppPermissionUtils.checkPermission(Manifest.permission.CALL_PHONE)
                && AppPermissionUtils.checkPermission(Manifest.permission.RECORD_AUDIO)
                && AppPermissionUtils.checkPermission(Manifest.permission.READ_PHONE_STATE)
                && AppPermissionUtils.checkPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS)
    }

    private fun checkSupportCallAudio(): Boolean {
        return AppPermissionUtils.checkPermission(Manifest.permission.RECORD_AUDIO)
                && AppPermissionUtils.checkPermission(Manifest.permission.CALL_PHONE)
                && AppPermissionUtils.checkPermission(Manifest.permission.READ_PHONE_STATE)
                && AppPermissionUtils.checkPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS)
    }
}