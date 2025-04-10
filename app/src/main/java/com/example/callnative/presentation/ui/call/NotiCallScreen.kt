package com.example.callnative.presentation.ui.call

import android.content.Context
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.callnative.R
import com.example.callnative.presentation.theme.color_text_type_call
import com.example.callnative.presentation.ui.views.AsyncImageView
import com.example.callnative.presentation.ui.views.IconView
import com.example.callnative.presentation.ui.views.LabelButtonView
import com.example.callnative.presentation.viewmodel.CallViewModel

object NotiCallScreen {
    const val ROUTE = "notiCallScreen"

    @Composable
    fun Screen(viewModel: CallViewModel = hiltViewModel(), isCallVideo: Boolean) {
        val context: Context = LocalContext.current
        val calleeData by viewModel.calleeData.collectAsState()

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImageView(
                imageUrl = calleeData.avatarImageUrl,
                defaultAvatarResId = R.drawable.background_noti_call,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(0.dp))
                    .blur(100.dp)
            )

            // Foreground Content
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(
                        top = 50.dp,
                        start = 10.dp,
                        end = 10.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = calleeData.displayName,
                    color = Color.White,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Row(
                        modifier = Modifier
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconView(
                            R.drawable.ic_logo,
                            size = 24.dp
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 5.dp),
                            text = context.getString(if (isCallVideo) R.string.ziichat_video else R.string.ziichat_voice),
                            color = MaterialTheme.colorScheme.color_text_type_call,
                            fontSize = 24.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        bottom = 30.dp,
                        start = 5.dp,
                        end = 5.dp
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LabelButtonView(
                    modifier = Modifier
                        .weight(1f)
                        .focusGroup(),
                    iconResId = R.drawable.ic_decline,
                    label = context.getString(R.string.decline),
                    size = 70.dp,
                    onClick = {
                        viewModel.handleDecline()
                    })
                LabelButtonView(
                    modifier = Modifier
                        .weight(1f)
                        .focusGroup()
                        .padding(0.dp),
                    iconResId = R.drawable.ic_answer,
                    label = context.getString(R.string.answer),
                    size = 70.dp,
                    onClick = {
                        viewModel.handleAnswer(isCallVideo)
                    },
                )
            }
        }

    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    CallNativeTheme {
//        Screen()
//    }
//}
