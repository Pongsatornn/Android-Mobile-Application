package com.example.myapplab10_11.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplab10_11.ui.animation.StepActivityAnimation
import java.text.NumberFormat

private data class Feature(val icon: String, val title: String, val onClick: () -> Unit)

@Composable
fun HomeScreen(
    steps: Int,
    stepGoal: Int,
    onCameraClick: () -> Unit,
    onMusicClick: () -> Unit,
    onVideoClick: () -> Unit,
    onSensorClick: () -> Unit
) {
    // เริ่มที่ false แล้วเปลี่ยนเป็น true ทันที → AnimatedVisibility เล่น enter ตอนเปิดหน้า
    val appeared = remember {
        MutableTransitionState(false).apply { targetState = true }
    }

    var showMessage by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header: Fade + Slide
        AnimatedVisibility(
            visibleState = appeared,
            enter = fadeIn(tween(600)) +
                slideInVertically(tween(600)) { -it }
        ) {
            Column {
                Text(
                    "Student Life",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    "Hello, Student 👋",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        // 2. Step Card: Property Animation + Transition + Progress Animation
        AnimatedVisibility(
            visibleState = appeared,
            enter = fadeIn(tween(600, delayMillis = 150)) +
                slideInVertically(tween(600, delayMillis = 150)) { it / 2 }
        ) {
            StepCard(steps = steps, goal = stepGoal, startProgress = appeared.isIdle)
        }

        // 4. Loading: Lottie (loading → walking → success ตามจำนวนก้าว)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StepActivityAnimation(steps = steps, goal = stepGoal)
        }

        // AnimatedVisibility + Enter / Exit Animation
        Button(
            onClick = { showMessage = !showMessage },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showMessage) "Hide Message" else "Show Message")
        }

        AnimatedVisibility(
            visible = showMessage,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Text(
                    "🎉 Keep Going!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

        // 3. Feature Cards: AnimatedVisibility ทีละใบ (stagger)
        val features = listOf(
            Feature("📷", "Camera", onCameraClick),
            Feature("🎵", "Music", onMusicClick),
            Feature("🎬", "Video", onVideoClick),
            Feature("👟", "Sensor", onSensorClick)
        )

        features.chunked(2).forEachIndexed { row, pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                pair.forEachIndexed { col, feature ->
                    val delay = 300 + (row * 2 + col) * 120
                    AnimatedVisibility(
                        visibleState = appeared,
                        enter = fadeIn(tween(400, delayMillis = delay)) +
                            scaleIn(tween(400, delayMillis = delay), initialScale = 0.6f) +
                            slideInHorizontally(tween(400, delayMillis = delay)) {
                                if (col == 0) -it / 3 else it / 3
                            },
                        modifier = Modifier.weight(1f)
                    ) {
                        FeatureCard(feature)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepCard(steps: Int, goal: Int, startProgress: Boolean) {
    // PART 1: Property Animation (animateDpAsState)
    var expanded by remember {
        mutableStateOf(false)
    }

    val cardHeight by animateDpAsState(
        targetValue = if (expanded) 220.dp else 130.dp,
        label = "cardHeight"
    )

    // PART 3: Transition — state เดียว (selected) เปลี่ยน scale + alpha + color พร้อมกัน
    var selected by remember {
        mutableStateOf(false)
    }

    val transition = updateTransition(
        targetState = selected,
        label = "cardTransition"
    )

    val scale by transition.animateFloat(
        transitionSpec = { spring(dampingRatio = 0.5f) },
        label = "scale"
    ) { state ->
        if (state) 1.05f else 1f
    }

    val alpha by transition.animateFloat(
        label = "alpha"
    ) { state ->
        if (state) 1f else 0.85f
    }

    val containerColor by transition.animateColor(
        label = "color"
    ) { state ->
        if (state)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant
    }

    // Progress Animation: หลอดค่อย ๆ วิ่งจาก 0 ไปถึงค่าจริง
    var progressTarget by remember { mutableStateOf(0f) }
    LaunchedEffect(steps, startProgress) {
        if (startProgress) {
            progressTarget = (steps / goal.toFloat()).coerceIn(0f, 1f)
        }
    }
    val progress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(1200),
        label = "progress"
    )

    val format = NumberFormat.getIntegerInstance()

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable { selected = !selected },
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👟", fontSize = 28.sp)
                Spacer(Modifier.padding(4.dp))
                Text(
                    format.format(steps),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.padding(4.dp))
                Text("STEPS", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.weight(1f))
                OutlinedButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Collapse" else "Expand")
                }
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "${(progress * 100).toInt()}% of ${format.format(goal)}",
                style = MaterialTheme.typography.bodySmall
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text("Goal: ${format.format(goal)} steps")
                    Text("Remaining: ${format.format((goal - steps).coerceAtLeast(0))} steps")
                    Text(
                        "Tap the card to highlight it",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(feature: Feature) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable(onClick = feature.onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(feature.icon, fontSize = 32.sp)
            Text(feature.title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
