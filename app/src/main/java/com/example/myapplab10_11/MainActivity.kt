package com.example.myapplab10_11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplab10_11.sensor.StepCounterManager
import com.example.myapplab10_11.ui.camera.CameraScreen
import com.example.myapplab10_11.ui.home.HomeScreen
import com.example.myapplab10_11.ui.music.MusicScreen
import com.example.myapplab10_11.ui.sensor.STEP_GOAL
import com.example.myapplab10_11.ui.sensor.StepCounterScreen
import com.example.myapplab10_11.ui.theme.MyappLab1011Theme
import com.example.myapplab10_11.ui.video.VideoScreen

const val HOME = "home"
const val CAMERA = "camera"
const val MUSIC = "music"
const val VIDEO = "video"
const val SENSOR = "sensor"

private data class BottomItem(val route: String, val icon: String, val label: String)

private val bottomItems = listOf(
    BottomItem(HOME, "🏠", "Home"),
    BottomItem(CAMERA, "📷", "Camera"),
    BottomItem(MUSIC, "🎵", "Music"),
    BottomItem(VIDEO, "🎬", "Video"),
    BottomItem(SENSOR, "👟", "Sensor")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyappLab1011Theme {
                StudentLifeApp()
            }
        }
    }
}

// Screen Transition: การเปลี่ยน Screen ก็คือ State Transition แบบหนึ่ง
// Camera → Slide, Music → Fade, Video → Scale, Sensor → Slide
private const val DURATION = 400

private typealias Scope = AnimatedContentTransitionScope<NavBackStackEntry>

private fun Scope.slideEnter(): EnterTransition =
    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(DURATION))

private fun Scope.slideExit(): ExitTransition =
    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(DURATION))

private fun fadeEnter(): EnterTransition = fadeIn(tween(DURATION))

private fun fadeExit(): ExitTransition = fadeOut(tween(DURATION))

private fun scaleEnter(): EnterTransition =
    scaleIn(tween(DURATION), initialScale = 0.85f) + fadeIn(tween(DURATION))

private fun scaleExit(): ExitTransition =
    scaleOut(tween(DURATION), targetScale = 0.85f) + fadeOut(tween(DURATION))

@Composable
fun StudentLifeApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Step Counter ระดับแอป เพื่อให้ Home และหน้า Sensor ใช้ค่าเดียวกัน
    val stepManager = remember {
        StepCounterManager(context.applicationContext)
    }

    DisposableEffect(Unit) {
        stepManager.start()
        onDispose {
            stepManager.stop()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HOME,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeEnter() },
            exitTransition = { fadeExit() }
        ) {
            composable(HOME) {
                HomeScreen(
                    steps = stepManager.steps,
                    stepGoal = STEP_GOAL,
                    onCameraClick = { navController.navigateTab(CAMERA) },
                    onMusicClick = { navController.navigateTab(MUSIC) },
                    onVideoClick = { navController.navigateTab(VIDEO) },
                    onSensorClick = { navController.navigateTab(SENSOR) }
                )
            }
            composable(
                CAMERA,
                enterTransition = { slideEnter() },
                exitTransition = { slideExit() }
            ) { CameraScreen() }
            composable(
                MUSIC,
                enterTransition = { fadeEnter() },
                exitTransition = { fadeExit() }
            ) { MusicScreen() }
            composable(
                VIDEO,
                enterTransition = { scaleEnter() },
                exitTransition = { scaleExit() }
            ) { VideoScreen() }
            composable(
                SENSOR,
                enterTransition = { slideEnter() },
                exitTransition = { slideExit() }
            ) { StepCounterScreen(stepManager) }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        bottomItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigateTab(item.route) },
                icon = { Text(item.icon) },
                label = { Text(item.label) }
            )
        }
    }
}

private fun NavHostController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id)
        launchSingleTop = true
    }
}
