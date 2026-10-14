package com.example.multimediaandsensors.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.multimediaandsensors.sensor.StepCounterManager
import com.example.multimediaandsensors.ui.animation.LoadingAnimation
import com.example.multimediaandsensors.ui.animation.SuccessAnimation
import com.example.multimediaandsensors.ui.animation.WalkingAnimation
import kotlinx.coroutines.delay

private const val STEP_GOAL = 10000
private const val WALKING_THRESHOLD = 1000

@Composable
fun HomeScreen(
    onCameraClick: () -> Unit,
    onMusicClick: () -> Unit,
    onVideoClick: () -> Unit,
    onSensorClick: () -> Unit
) {
    val steps = rememberHomeSteps()

    var showMessage by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Student Life",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        AnimatedHeader()

        // Animation 4: Lottie (loading -> walking once the user has moved)
        Crossfade(
            targetState = steps != null && steps >= WALKING_THRESHOLD,
            label = "lottieSwap"
        ) { walking ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (walking) {
                    WalkingAnimation(size = 140.dp)
                    Text("Walking!", style = MaterialTheme.typography.labelLarge)
                } else {
                    LoadingAnimation(size = 140.dp)
                    Text("Loading...", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        StepCard(steps = steps, onOpenSensor = onSensorClick)

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                showMessage = !showMessage
            }
        ) {
            Text(if (showMessage) "Hide Message" else "Show Message")
        }

        // Animation 2: AnimatedVisibility with enter / exit
        AnimatedVisibility(
            visible = showMessage,
            enter =
                fadeIn() +
                    slideInVertically(),
            exit =
                fadeOut() +
                    slideOutVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SuccessAnimation(size = 56.dp)
                    Spacer(Modifier.padding(start = 12.dp))
                    Text(
                        if ((steps ?: 0) >= STEP_GOAL) "🎉 Goal reached!" else "🎉 Keep Going!",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        val features = listOf(
            Feature("📷", "Camera", onCameraClick),
            Feature("🎵", "Music", onMusicClick),
            Feature("🎬", "Video", onVideoClick),
            Feature("👟", "Sensor", onSensorClick)
        )

        features.chunked(2).forEachIndexed { row, pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                pair.forEachIndexed { col, feature ->
                    AnimatedFeatureCard(
                        feature = feature,
                        index = row * 2 + col,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// Returns live steps when ACTIVITY_RECOGNITION is granted, or null otherwise.
// Permission is requested on the Step Counter screen.
@Composable
private fun rememberHomeSteps(): Int? {
    val context = LocalContext.current
    val granted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACTIVITY_RECOGNITION
    ) == PackageManager.PERMISSION_GRANTED
    if (!granted) return null

    val manager = remember {
        StepCounterManager(context)
    }

    DisposableEffect(Unit) {
        manager.start()
        onDispose {
            manager.stop()
        }
    }

    return manager.steps
}

@Composable
private fun AnimatedHeader() {
    // Animation 1 (header): Fade + Slide in when the screen first appears
    val visibleState = remember {
        MutableTransitionState(false).apply { targetState = true }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter =
            fadeIn(tween(700)) +
                slideInVertically(tween(700)) { -it }
    ) {
        Text(
            "👋 Hello, Student",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun StepCard(steps: Int?, onOpenSensor: () -> Unit) {
    // Animation 1: Property animation (height)
    var expanded by remember {
        mutableStateOf(false)
    }

    val cardHeight by animateDpAsState(
        targetValue =
            if (expanded)
                270.dp
            else
                170.dp,
        label = "cardHeight"
    )

    // Animation 3: one state drives scale + alpha + color together
    var selected by remember {
        mutableStateOf(false)
    }

    val transition =
        updateTransition(
            targetState = selected,
            label = "cardTransition"
        )

    val scale by transition.animateFloat(
        label = "scale"
    ) { state ->
        if (state)
            1.05f
        else
            1f
    }

    val alpha by transition.animateFloat(
        label = "alpha"
    ) { state ->
        if (state)
            1f
        else
            0.85f
    }

    val containerColor by transition.animateColor(
        label = "color"
    ) { state ->
        if (state)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant
    }

    // Progress animates from 0 to the real value when the card appears
    var progressTarget by remember {
        mutableStateOf(0f)
    }
    LaunchedEffect(steps) {
        progressTarget = ((steps ?: 0) / STEP_GOAL.toFloat()).coerceIn(0f, 1f)
    }
    val progress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(1200),
        label = "stepProgress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable {
                selected = !selected
            },
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (steps != null) "👟 %,d Steps".format(steps) else "👟 -- Steps",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )

            Text(
                "%d%% of %,d".format((progress * 100).toInt(), STEP_GOAL),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            AnimatedVisibility(visible = expanded) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (steps == null)
                            "ยังไม่ได้อนุญาตการนับก้าว"
                        else
                            "เหลืออีก %,d ก้าวถึงเป้าหมาย".format((STEP_GOAL - steps).coerceAtLeast(0)),
                        textAlign = TextAlign.Center
                    )
                    TextButton(onClick = onOpenSensor) {
                        Text("Open Step Counter")
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            TextButton(
                onClick = {
                    expanded = !expanded
                }
            ) {
                Text(
                    if (expanded)
                        "Collapse"
                    else
                        "Expand"
                )
            }
        }
    }
}

private data class Feature(
    val icon: String,
    val title: String,
    val onClick: () -> Unit
)

@Composable
private fun AnimatedFeatureCard(
    feature: Feature,
    index: Int,
    modifier: Modifier = Modifier
) {
    // Staggered AnimatedVisibility: cards pop in one after another
    var visible by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        delay(150L + index * 120L)
        visible = true
    }

    Box(modifier = modifier.aspectRatio(1.3f)) {
        AnimatedVisibility(
            visible = visible,
            enter =
                fadeIn(tween(400)) +
                    scaleIn(tween(400), initialScale = 0.6f) +
                    slideInVertically(tween(400)) { it / 3 }
        ) {
            ElevatedCard(
                onClick = feature.onClick,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(feature.icon, fontSize = 36.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        feature.title,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
