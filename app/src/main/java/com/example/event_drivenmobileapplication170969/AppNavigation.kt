package com.example.event_drivenmobileapplication170969

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

object Routes {
    const val REGISTER = "register"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val resetForm: Boolean = false
)

private val bottomItems = listOf(
    NavItem(Routes.REGISTER, "Home", Icons.Filled.Home),
    NavItem(Routes.PROFILE, "Profile", Icons.Filled.Person),
    NavItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings)
)

private val drawerItems = listOf(
    NavItem(Routes.REGISTER, "Home", Icons.Filled.Home),
    NavItem(Routes.PROFILE, "Profile", Icons.Filled.Person),
    NavItem(Routes.REGISTER, "Register (ใหม่)", Icons.Filled.Create, resetForm = true),
    NavItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings),
    NavItem(Routes.ABOUT, "About", Icons.Filled.Info)
)

private fun titleFor(route: String) = when (route) {
    Routes.PROFILE -> "My Profile"
    Routes.SETTINGS -> "Settings"
    Routes.ABOUT -> "About"
    else -> "Student Registration"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentApp() {
    // 1) State กลางของทั้งแอป
    val state = remember { StudentState() }

    // 2) Dark Mode: Switch ใน Settings เปลี่ยน state.darkMode -> ธีมทั้งแอปเปลี่ยน
    val colors = if (state.darkMode)
        darkColorScheme(primary = Color(0xFF90CAF9))
    else
        lightColorScheme(primary = Color(0xFF1565C0))

    MaterialTheme(colorScheme = colors) {
        // 3) Navigation
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route ?: Routes.REGISTER

        // ไปหน้าต่าง ๆ แบบไม่ให้ back stack ซ้อนกันยาว
        fun navigateTo(route: String) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        // Navigation Drawer (เมนูด้านข้าง)
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerHeader(state)
                    drawerItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.label) },
                            icon = { Icon(item.icon, contentDescription = null) },
                            selected = !item.resetForm && currentRoute == item.route,
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (item.resetForm) state.reset()
                                navigateTo(item.route)
                            },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(titleFor(currentRoute)) },
                        navigationIcon = {
                            // ImageButton -> IconButton
                            if (currentRoute == Routes.REGISTER) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                                }
                            } else {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        },
                        actions = {
                            if (currentRoute == Routes.PROFILE) {
                                IconButton(onClick = { navigateTo(Routes.REGISTER) }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                // Bottom Navigation (แถบนำทางด้านล่าง)
                bottomBar = {
                    NavigationBar {
                        bottomItems.forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.route,
                                onClick = { navigateTo(item.route) },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                // NavHost = "ตัวสลับหน้าจอ" ตาม route
                NavHost(
                    navController = navController,
                    startDestination = Routes.REGISTER,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Routes.REGISTER) {
                        RegisterScreen(
                            state = state,
                            onRegister = {
                                // Event Chain: REGISTER -> onClick -> registered = true -> navigate()
                                state.registered = true
                                navigateTo(Routes.PROFILE)
                            }
                        )
                    }
                    composable(Routes.PROFILE) {
                        ProfileScreen(state = state, onEdit = { navigateTo(Routes.REGISTER) })
                    }
                    composable(Routes.SETTINGS) {
                        SettingsScreen(state = state, onAbout = { navController.navigate(Routes.ABOUT) })
                    }
                    composable(Routes.ABOUT) {
                        AboutScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerHeader(state: StudentState) {
    Column(Modifier.fillMaxWidth().padding(24.dp)) {
        UriImage(
            uriString = state.imageUriString,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            val resId = state.selectedDrawableResId
            if (resId != null) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = state.name.ifBlank { "Student Hub" },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text("Computer Science", style = MaterialTheme.typography.bodySmall)
    }
    HorizontalDivider()
}
