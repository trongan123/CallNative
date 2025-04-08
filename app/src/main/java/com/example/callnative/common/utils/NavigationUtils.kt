package com.example.callnative.common.utils

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import java.lang.ref.WeakReference


object NavigationUtils {
    private var navControllerRef: WeakReference<NavController>? = null

    fun setNavController(navController: NavController?) {
        navControllerRef = navController?.let { WeakReference(it) }
    }

    fun popBackStack() {
        navControllerRef?.get()?.popBackStack()
    }

    fun navigate(route: String) {
        navControllerRef?.get()?.navigate(route)
    }

    fun savedStateHandle(id: String, data: Any?) {
        navControllerRef?.get()?.currentBackStackEntry?.savedStateHandle?.set(id, data)
    }

    fun getSavedStateHandle(): SavedStateHandle? {
        return navControllerRef?.get()?.previousBackStackEntry?.savedStateHandle
    }

    fun clearNavController() {
        navControllerRef = null
    }
}
