package com.example.multimediaandsensors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.multimediaandsensors.ui.camera.CameraScreen
import com.example.multimediaandsensors.ui.home.HomeScreen
import com.example.multimediaandsensors.ui.music.MusicScreen
import com.example.multimediaandsensors.ui.sensor.StepCounterScreen
import com.example.multimediaandsensors.ui.theme.MultimediaAndSensorsTheme
import com.example.multimediaandsensors.ui.video.VideoScreen

const val HOME = "home"
const val CAMERA = "camera"
const val MUSIC = "music"
const val VIDEO = "video"
const val SENSOR = "sensor"

private val bottomTabs = listOf(
    Triple(HOME, "🏠", "Home"),
    Triple(CAMERA, "📷", "Camera"),
    Triple(MUSIC, "🎵", "Music"),
    Triple(VIDEO, "🎬", "Video"),
    Triple(SENSOR, "👟", "Sensor")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultimediaAndSensorsTheme {
                StudentLifeApp()
            }
        }
    }
}

@Composable
fun StudentLifeApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                bottomTabs.forEach { (route, icon, label) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = { navController.navigateTab(route) },
                        icon = { Text(icon) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HOME,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(TRANSITION_MS)) },
            exitTransition = { fadeOut(tween(TRANSITION_MS)) }
        ) {
            composable(HOME) {
                HomeScreen(
                    onCameraClick = { navController.navigateTab(CAMERA) },
                    onMusicClick = { navController.navigateTab(MUSIC) },
                    onVideoClick = { navController.navigateTab(VIDEO) },
                    onSensorClick = { navController.navigateTab(SENSOR) }
                )
            }
            // Camera -> Slide
            composable(
                CAMERA,
                enterTransition = {
                    slideIntoContainer(SlideDirection.Left, tween(TRANSITION_MS))
                },
                exitTransition = {
                    slideOutOfContainer(SlideDirection.Right, tween(TRANSITION_MS))
                }
            ) { CameraScreen() }

            // Music -> Fade
            composable(
                MUSIC,
                enterTransition = { fadeIn(tween(TRANSITION_MS)) },
                exitTransition = { fadeOut(tween(TRANSITION_MS)) }
            ) { MusicScreen() }

            // Video -> Scale
            composable(
                VIDEO,
                enterTransition = {
                    scaleIn(tween(TRANSITION_MS), initialScale = 0.8f) + fadeIn(tween(TRANSITION_MS))
                },
                exitTransition = {
                    scaleOut(tween(TRANSITION_MS), targetScale = 0.8f) + fadeOut(tween(TRANSITION_MS))
                }
            ) { VideoScreen() }

            // Sensor -> Slide (vertical)
            composable(
                SENSOR,
                enterTransition = {
                    slideIntoContainer(SlideDirection.Up, tween(TRANSITION_MS))
                },
                exitTransition = {
                    slideOutOfContainer(SlideDirection.Down, tween(TRANSITION_MS))
                }
            ) { StepCounterScreen() }
        }
    }
}

private const val TRANSITION_MS = 400

private fun NavHostController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id)
        launchSingleTop = true
    }
}
