package com.example.permission

interface PermissionListener {

    fun onPermissionGranted()

    fun onPermissionDenied(deniedPermissions: List<String>)
}