package com.example.callnative.presentation.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.callnative.data.enums.ProfileViewSize

@Composable
fun CallView(
    profileSize: ProfileViewSize,
    avatarImageUrl: String,
    displayName: String,
    callState: String? = null,
    defaultAvatarResId: Int,
    hasVideo: Boolean = false
) {
    Box(
        modifier = profileSize.modifier
    ) {
        if (hasVideo) {
            Box(modifier = Modifier.fillMaxSize()) {
              VideoView()
            }
        } else {
            // Background Image
            AsyncImageView(
                imageUrl = avatarImageUrl,
                defaultAvatarResId = defaultAvatarResId,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(if (profileSize == ProfileViewSize.MINIMIZE) 14.dp else 0.dp))
                    .blur(30.dp)
            )

            // Foreground Content
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar Image
                AsyncImageView(
                    imageUrl = avatarImageUrl,
                    defaultAvatarResId = defaultAvatarResId,
                    modifier = Modifier
                        .size(if (profileSize == ProfileViewSize.MINIMIZE) 40.dp else 64.dp)
                        .clip(CircleShape)
                )

                // Display Name
                DisplayNameView(
                    name = displayName,
                    isMinimized = profileSize == ProfileViewSize.MINIMIZE
                )

                // Call State
                callState?.let {
                    ContentView(state = callState)
                }
            }
        }
    }
}