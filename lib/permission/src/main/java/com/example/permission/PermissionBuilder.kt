package com.example.permission

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes


abstract class PermissionBuilder {
    private val context: Context? get() = AppProvider.getAppContext()

    private var listener: PermissionListener? = null
    private var permissions: Array<String>? = null
    private var denyMessage: CharSequence? = null

    fun check() {
        when {
            listener == null -> throw IllegalArgumentException("You must setPermissionListener() on AppPermission")
            permissions.isNullOrEmpty() -> throw IllegalArgumentException("You must setPermissions() on AppPermission")
            else -> {
                val intent = Intent(context, AppPermissionActivity::class.java).apply {
                    putExtra(AppPermissionActivity.EXTRA_PERMISSIONS, permissions)
                    putExtra(AppPermissionActivity.EXTRA_DENY_MESSAGE, denyMessage)
                    putExtra(AppPermissionActivity.EXTRA_PACKAGE_NAME, context?.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                }

                AppPermissionActivity.startActivity(context, intent, listener!!)
            }
        }
    }

    fun setListener(listener: PermissionListener): PermissionBuilder {
        this.listener = listener
        return this
    }

    fun setPermissions(vararg permissions: String): PermissionBuilder {
        this.permissions = permissions.toList().toTypedArray()
        return this
    }

    fun setDeniedMessage(@StringRes stringRes: Int): PermissionBuilder {
        return setDeniedMessage(context?.getText(stringRes) as CharSequence)
    }

    fun setDeniedMessage(denyMessage: CharSequence): PermissionBuilder {
        this.denyMessage = denyMessage
        return this
    }
}