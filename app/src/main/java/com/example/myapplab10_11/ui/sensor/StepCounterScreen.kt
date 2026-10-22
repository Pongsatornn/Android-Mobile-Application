package com.example.myapplab10_11.ui.sensor

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.myapplab10_11.sensor.StepCounterManager
import java.text.NumberFormat

const val STEP_GOAL = 10_000

@Composable
fun StepCounterScreen(manager: StepCounterManager) {
    val context = LocalContext.current

    // Android 10 (API 29) ขึ้นไปต้องขอ ACTIVITY_RECOGNITION ตอน runtime ด้วย
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

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            // manager ถูกสร้างระดับแอป (ใช้ร่วมกับ Home) — เริ่มใหม่หลังได้ permission
            manager.start()
        } else {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    if (!hasPermission) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("ต้องอนุญาต Physical activity ก่อนจึงจะนับก้าวได้")
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            ) {
                Text("Allow")
            }
        }
        return
    }

    val steps = manager.steps
    val progress = (steps / STEP_GOAL.toFloat()).coerceIn(0f, 1f)
    val format = NumberFormat.getIntegerInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "STEP COUNTER",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        Text("👟", fontSize = 64.sp)

        Text(
            format.format(steps),
            style = MaterialTheme.typography.displayLarge
        )

        Text("STEPS")

        Spacer(Modifier.height(24.dp))

        Text("Today's Activity")

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )

        Spacer(Modifier.height(8.dp))

        Text("${format.format(steps)} / ${format.format(STEP_GOAL)} (${(progress * 100).toInt()}%)")

        Spacer(Modifier.height(16.dp))

        Text(
            when {
                !manager.isSensorAvailable -> "อุปกรณ์นี้ไม่มี Step Counter Sensor"
                steps >= STEP_GOAL -> "Goal reached! 🎉"
                else -> "Keep Going!"
            },
            style = MaterialTheme.typography.titleMedium
        )
    }
}
