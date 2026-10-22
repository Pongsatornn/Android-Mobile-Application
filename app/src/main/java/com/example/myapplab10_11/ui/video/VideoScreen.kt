package com.example.myapplab10_11.ui.video

import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.example.myapplab10_11.R

@Composable
fun VideoScreen() {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Campus Video",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        // VideoView เป็น View แบบเดิม จึงใช้ AndroidView เป็นสะพานเข้า Compose
        AndroidView(
            factory = {
                VideoView(context).apply {
                    val uri =
                        ("android.resource://" + context.packageName + "/" + R.raw.campus).toUri()

                    setVideoURI(uri)

                    setMediaController(
                        MediaController(context).also {
                            it.setAnchorView(this)
                        }
                    )

                    start()
                }
            },
            onRelease = { it.stopPlayback() },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )
    }
}
