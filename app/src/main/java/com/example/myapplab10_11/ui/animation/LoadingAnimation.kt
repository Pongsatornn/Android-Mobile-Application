package com.example.myapplab10_11.ui.animation

import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.myapplab10_11.R

@Composable
fun LoadingAnimation(size: Dp = 180.dp) {
    LottieLoop(R.raw.loading, size)
}

@Composable
fun LottieLoop(@RawRes res: Int, size: Dp = 180.dp) {
    val composition by
        rememberLottieComposition(
            LottieCompositionSpec.RawRes(res)
        )

    val progress by
        animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(size)
    )
}

private enum class ActivityLevel(@RawRes val res: Int, val label: String) {
    Loading(R.raw.loading, "Loading..."),
    Walking(R.raw.walking, "Walking! Keep it up"),
    Success(R.raw.success, "Goal reached! 🎉")
}

// steps < 1000 → loading, steps >= 1000 → walking, ถึงเป้า 10,000 → success
@Composable
fun StepActivityAnimation(steps: Int, goal: Int, size: Dp = 140.dp) {
    val level = when {
        steps >= goal -> ActivityLevel.Success
        steps >= 1000 -> ActivityLevel.Walking
        else -> ActivityLevel.Loading
    }

    AnimatedContent(
        targetState = level,
        transitionSpec = {
            (fadeIn() + scaleIn(initialScale = 0.8f)) togetherWith fadeOut()
        },
        label = "stepActivity"
    ) { current ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LottieLoop(current.res, size)
            Text(current.label, style = MaterialTheme.typography.titleMedium)
        }
    }
}
