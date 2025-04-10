package com.example.callnative.presentation.ui.views

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.callnative.R
import com.example.callnative.common.utils.CoroutineUtils
import kotlinx.coroutines.delay

@Composable
fun BackExit(context: Context = LocalContext.current) {
    var backPressedOnce by remember { mutableStateOf(false) }
    BackHandler {
        if (backPressedOnce) {
            (context as? Activity)?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(
                context,
                context.getString(R.string.tap_again_to_exit),
                Toast.LENGTH_SHORT
            ).show()

            CoroutineUtils.launchBackground {
                delay(2000)
                backPressedOnce = false
            }
        }
    }
}