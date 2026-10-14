package com.example.multimediaandsensors.ui.video

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.multimediaandsensors.R

@Composable
fun VideoScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Campus Video",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        // VideoView is a classic Android View, so AndroidView bridges it into Compose.
        AndroidView(
            factory = {
                VideoView(context).apply {
                    val uri =
                        Uri.parse(
                            "android.resource://" +
                                context.packageName +
                                "/" +
                                R.raw.campus
                        )

                    setVideoURI(uri)

                    setMediaController(
                        MediaController(context)
                            .also {
                                it.setAnchorView(this)
                            }
                    )

                    start()
                }
            },
            onRelease = { videoView ->
                videoView.stopPlayback()
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "แตะที่วิดีโอเพื่อแสดงปุ่มควบคุม",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
