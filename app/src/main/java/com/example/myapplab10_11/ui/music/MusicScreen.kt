package com.example.myapplab10_11.ui.music

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplab10_11.R

@Composable
fun MusicScreen() {
    val context = LocalContext.current

    val mediaPlayer = remember {
        MediaPlayer.create(
            context,
            R.raw.music
        )
    }

    var isPlaying by remember {
        mutableStateOf(false)
    }

    DisposableEffect(Unit) {
        mediaPlayer.setOnCompletionListener {
            isPlaying = false
        }
        onDispose {
            mediaPlayer.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Student Music Player",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(30.dp))

        Text(text = "🎵", fontSize = 72.sp)

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Student Song",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(30.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause()
                        isPlaying = false
                    } else {
                        mediaPlayer.start()
                        isPlaying = true
                    }
                }
            ) {
                Text(if (isPlaying) "Pause" else "Play")
            }

            OutlinedButton(
                onClick = {
                    mediaPlayer.pause()
                    mediaPlayer.seekTo(0)
                    isPlaying = false
                }
            ) {
                Text("Stop")
            }
        }
    }
}
