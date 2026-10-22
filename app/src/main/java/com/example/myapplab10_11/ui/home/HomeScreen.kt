package com.example.myapplab10_11.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onCameraClick: () -> Unit,
    onMusicClick: () -> Unit,
    onVideoClick: () -> Unit,
    onSensorClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Student Life",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            "👤 Multimedia App",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onCameraClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📷 Camera Filter")
        }

        Button(
            onClick = onMusicClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🎵 Music Player")
        }

        Button(
            onClick = onVideoClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🎬 Video Player")
        }

        Button(
            onClick = onSensorClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("👟 Step Counter")
        }
    }
}
