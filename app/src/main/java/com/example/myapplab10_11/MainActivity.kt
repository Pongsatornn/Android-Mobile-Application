package com.example.myapplab10_11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.myapplab10_11.ui.camera.CameraScreen
import com.example.myapplab10_11.ui.home.HomeScreen
import com.example.myapplab10_11.ui.music.MusicScreen
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

@Composable
fun StudentLifeApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(HOME) {
                HomeScreen(
                    onCameraClick = { navController.navigateTab(CAMERA) },
                    onMusicClick = { navController.navigateTab(MUSIC) },
                    onVideoClick = { navController.navigateTab(VIDEO) },
                    onSensorClick = { navController.navigateTab(SENSOR) }
                )
            }
            composable(CAMERA) { CameraScreen() }
            composable(MUSIC) { MusicScreen() }
            composable(VIDEO) { VideoScreen() }
            composable(SENSOR) { StepCounterScreen() }
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
