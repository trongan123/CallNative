package com.example.callnative

import android.app.Application
import com.example.callnative.data.ObjectBox
import dagger.hilt.android.HiltAndroidApp
import org.webrtc.PeerConnectionFactory

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this) // Khởi tạo ObjectBox

        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(applicationContext)
                .createInitializationOptions()
        )
    }
}