package com.example.callnative.data

import android.content.Context
import com.example.callnative.data.models.MyObjectBox
import io.objectbox.BoxStore
import javax.inject.Singleton

@Singleton
object ObjectBox {
    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore = MyObjectBox.builder().androidContext(context).build()
    }
}