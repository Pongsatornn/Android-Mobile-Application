package com.example.multimediaandsensors.ui.sensor

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.multimediaandsensors.sensor.StepCounterManager

private const val STEP_GOAL = 10000

@Composable
fun StepCounterScreen() {
    val context = LocalContext.current

    // ACTIVITY_RECOGNITION is a runtime permission on Android 10 (API 29)+,
    // which is this app's minSdk.
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasPermission = granted
        }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    if (hasPermission) {
        StepCounterContent()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("ต้องอนุญาต Physical activity ก่อนเพื่อนับก้าว")
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            ) {
                Text("Allow")
            }
        }
    }
}

@Composable
private fun StepCounterContent() {
    val context = LocalContext.current

    val manager =
        remember {
            StepCounterManager(context)
        }

    DisposableEffect(Unit) {
        manager.start()

        onDispose {
            manager.stop()
        }
    }

    val progress by animateFloatAsState(
        targetValue = (manager.steps / STEP_GOAL.toFloat()).coerceIn(0f, 1f),
        label = "stepProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Today's Steps",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        Text("👟", fontSize = 56.sp)

        if (!manager.isSensorAvailable) {
            Spacer(Modifier.height(16.dp))
            Text(
                "อุปกรณ์นี้ไม่มี Step Counter Sensor",
                color = MaterialTheme.colorScheme.error
            )
        }

        Text(
            "%,d".format(manager.steps),
            style = MaterialTheme.typography.displayLarge
        )

        Text("steps")

        Spacer(Modifier.height(32.dp))

        Text(
            "Goal: %,d steps".format(STEP_GOAL),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )

        Spacer(Modifier.height(8.dp))

        Text("${(progress * 100).toInt()}%")

        Spacer(Modifier.height(16.dp))

        Text(
            if (manager.steps >= STEP_GOAL) "Goal reached! 🎉" else "Keep Going!",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
