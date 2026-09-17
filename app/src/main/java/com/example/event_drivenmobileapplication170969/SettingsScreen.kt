package com.example.event_drivenmobileapplication170969

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(state: StudentState, onAbout: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        SectionTitle("Appearance")
        SettingRow(Icons.Filled.DarkMode, "Dark Mode") {
            Switch(checked = state.darkMode, onCheckedChange = { state.darkMode = it })
        }
        SettingRow(Icons.Filled.Notifications, "Notifications") {
            Switch(
                checked = state.notificationEnabled,
                onCheckedChange = { state.notificationEnabled = it }
            )
        }
        HorizontalDivider(Modifier.padding(vertical = 8.dp))

        SectionTitle("Preference")
        SettingRow(Icons.Filled.Favorite, "Favorite") {
            FavoriteToggle(state.isFavorite) { state.isFavorite = it }
        }
        HorizontalDivider(Modifier.padding(vertical = 8.dp))

        SectionTitle("About")
        SettingRow(Icons.Filled.Info, "About App", onClick = onAbout) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

/** Row = จัดไอคอน + ข้อความ + ตัวควบคุม ให้อยู่แถวเดียวกัน */
@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Text(title, Modifier.weight(1f))
        trailing()
    }
}

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Student Registration App", style = MaterialTheme.typography.headlineSmall)
        Text("Event Handling + UI Components + Navigation")
        Text("Kotlin + Jetpack Compose", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Version 1.0")
    }
}
