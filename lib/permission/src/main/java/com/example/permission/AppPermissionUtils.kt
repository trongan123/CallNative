package com.example.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat


object AppPermissionUtils {
    const val REQ_CODE_REQUEST_SETTING = 2000
    private val context: Context? get() = AppProvider.getAppContext()

    fun isDenied(permission: String): Boolean {
        return !isGranted(permission)
    }

    fun isGranted(permission: String): Boolean {
        return when (permission) {
            Manifest.permission.SYSTEM_ALERT_WINDOW -> Settings.canDrawOverlays(context)
            else -> checkPermission(permission)
        }
    }

    fun checkPermission(permission: String): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(
                it,
                permission
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    fun getDeniedPermissions(vararg permissions: String): List<String> {
        return permissions.filter { isDenied(it) }
    }

    fun getSettingIntent(): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:${context?.packageName}"))
    }

    fun hasPermissionReadMedias(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermission(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}