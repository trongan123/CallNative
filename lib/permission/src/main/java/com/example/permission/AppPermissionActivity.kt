package com.example.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Deque
import java.util.LinkedList

class AppPermissionActivity : AppCompatActivity() {
    companion object {
        const val REQ_CODE_PERMISSION_REQUEST = 10
        const val EXTRA_PERMISSIONS = "permissions"
        const val EXTRA_DENY_MESSAGE = "deny_message"
        const val EXTRA_PACKAGE_NAME = "package_name"

        //list các quyền cần truy cập
        private var permissionListenerStack: Deque<PermissionListener>? = null

        fun startActivity(context: Context?, intent: Intent, listener: PermissionListener) {
            if (permissionListenerStack == null) {
                permissionListenerStack = LinkedList()
            }
            permissionListenerStack?.push(listener)
            context?.startActivity(intent)
        }
    }

    private lateinit var settingButtonText: String
    private lateinit var denyTitle: CharSequence
    private lateinit var deniedCloseButtonText: String

    private var denyMessage: CharSequence? = null
    private var permissions: Array<String>? = null
    private val onRequestSetting = getRequestSettingLauncher()
    private var packageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingButtonText = getString(R.string.open_settings)
        denyTitle = getString(R.string.notification)
        deniedCloseButtonText = getString(R.string.not_now)

        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        setupFromSavedInstanceState(savedInstanceState)

        //kiểm tra quyền hiển thị dialog
        if (needWindowPermission()) {
            requestWindowPermission()
        } else {
            checkPermissions(false)
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    //get data
    private fun setupFromSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            permissions = savedInstanceState.getStringArray(EXTRA_PERMISSIONS)
            denyMessage = savedInstanceState.getCharSequence(EXTRA_DENY_MESSAGE)
            packageName = savedInstanceState.getString(EXTRA_PACKAGE_NAME)
        } else {
            intent?.let {
                permissions = it.getStringArrayExtra(EXTRA_PERMISSIONS)
                denyMessage = it.getCharSequenceExtra(EXTRA_DENY_MESSAGE)
                packageName = it.getStringExtra(EXTRA_PACKAGE_NAME)
            }
        }
    }

    private fun needWindowPermission(): Boolean {
        return permissions?.any { it == Manifest.permission.SYSTEM_ALERT_WINDOW && hasWindowPermission() } == true
    }

    private fun hasWindowPermission(): Boolean {
        return !Settings.canDrawOverlays(applicationContext)
    }

    private fun requestWindowPermission() {
        val uri = Uri.fromParts("package", packageName, null)
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
        onSystemAlertWindowPermissionRequestLauncher().launch(intent)
    }

    //kiểm tra và yêu cầu cấp quyền
    private fun checkPermissions(fromOnActivityResult: Boolean) {
        val needPermissions = permissions?.filter {
            it == Manifest.permission.SYSTEM_ALERT_WINDOW && hasWindowPermission() ||
                    AppPermissionUtils.isDenied(it)
        } ?: emptyList()

        when {
            needPermissions.isEmpty() -> permissionResult(null)
            fromOnActivityResult || (needPermissions.size == 1 && needPermissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) ->
                permissionResult(needPermissions)

            else -> requestPermissions(needPermissions)
        }
    }

    // handel kết quả cấp quyền
    private fun permissionResult(deniedPermissions: List<String>?) {
        finish()

        permissionListenerStack?.let {
            val listener = it.pop()
            if (deniedPermissions.isNullOrEmpty()) {
                listener.onPermissionGranted()
            } else {
                listener.onPermissionDenied(deniedPermissions)
            }
            if (it.isEmpty()) {
                permissionListenerStack = null
            }
        }
    }

    private fun requestPermissions(needPermissions: List<String>) {
        ActivityCompat.requestPermissions(
            this,
            needPermissions.toTypedArray(),
            REQ_CODE_PERMISSION_REQUEST
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray(EXTRA_PERMISSIONS, permissions)
        outState.putCharSequence(EXTRA_DENY_MESSAGE, denyMessage)
        outState.putString(EXTRA_PACKAGE_NAME, packageName)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val deniedPermissions = AppPermissionUtils.getDeniedPermissions(*permissions)

        if (deniedPermissions.isEmpty()) {
            permissionResult(null)
        } else {
            showPermissionDenyDialog(deniedPermissions)
        }
    }

    //show dialog từ trối quyền
    private fun showPermissionDenyDialog(deniedPermissions: List<String>) {
        if (denyMessage.isNullOrEmpty()) {
            permissionResult(deniedPermissions)
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(denyTitle)
            .setMessage(denyMessage)
            .setCancelable(false)
            .setNegativeButton(deniedCloseButtonText) { _, _ -> permissionResult(deniedPermissions) }
            .setPositiveButton(settingButtonText) { _, _ ->
                onRequestSetting.launch(
                    AppPermissionUtils.getSettingIntent()
                )
            }
            .show()
    }

    private fun showWindowPermissionDenyDialog() {
        MaterialAlertDialogBuilder(this)
            .setMessage(denyMessage)
            .setCancelable(false)
            .setNegativeButton(deniedCloseButtonText) { _, _ -> checkPermissions(false) }
            .setPositiveButton(settingButtonText) { _, _ ->
                val uri = Uri.fromParts("package", packageName, null)
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
                onSystemAlertWindowPermissionRequestSettingLauncher().launch(intent)
            }
            .show()
    }

    private fun getRequestSettingLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkPermissions(true)
        }
    }

    private fun onSystemAlertWindowPermissionRequestLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (hasWindowPermission() && !denyMessage.isNullOrEmpty()) {
                showWindowPermissionDenyDialog()
            } else {
                checkPermissions(false)
            }
        }
    }

    private fun onSystemAlertWindowPermissionRequestSettingLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkPermissions(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AppPermissionUtils.REQ_CODE_REQUEST_SETTING) {
            checkPermissions(true)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}