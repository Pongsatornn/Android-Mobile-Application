package com.example.multimediaandsensors.ui.animation

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.multimediaandsensors.R

@Composable
fun LottieRawAnimation(
    @RawRes animationRes: Int,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    iterations: Int = LottieConstants.IterateForever
) {
    val composition by
        rememberLottieComposition(
            LottieCompositionSpec.RawRes(
                animationRes
            )
        )

    val progress by
        animateLottieCompositionAsState(
            composition = composition,
            iterations = iterations
        )

    LottieAnimation(
        composition = composition,
        progress = {
            progress
        },
        modifier = modifier.size(size)
    )
}

@Composable
fun LoadingAnimation(modifier: Modifier = Modifier, size: Dp = 180.dp) {
    LottieRawAnimation(R.raw.loading, modifier, size)
}

@Composable
fun WalkingAnimation(modifier: Modifier = Modifier, size: Dp = 180.dp) {
    LottieRawAnimation(R.raw.walking, modifier, size)
}

@Composable
fun SuccessAnimation(modifier: Modifier = Modifier, size: Dp = 180.dp) {
    LottieRawAnimation(R.raw.success, modifier, size, iterations = 1)
}
