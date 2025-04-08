package com.example.callnative.data

import android.content.Context
import org.webrtc.PeerConnectionFactory

object WebRTCManager {
    @Volatile
    private var isInitialized = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            synchronized(this) {
                if (!isInitialized) {
                    PeerConnectionFactory.initialize(
                        PeerConnectionFactory.InitializationOptions.builder(context.applicationContext)
                            .createInitializationOptions()
                    )
                    isInitialized = true
                }
            }
        }
    }
}