package com.example.mobileapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileapplication.ui.theme.MobileApplicationTheme
import androidx.compose.material.icons.filled.Star
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudentProfileApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun StudentProfileApp(modifier: Modifier = Modifier) {
    var showDetail by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var isActiveStatus by remember { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxSize()) {
        if (!showDetail) {
            HomeScreen(
                isFavorite = isFavorite,
                onFavoriteToggle = { isFavorite = !isFavorite },
                onShowInfoClick = { showDetail = true }
            )
        } else {
            DetailScreen(
                isActiveStatus = isActiveStatus,
                onStatusToggle = { isActiveStatus = !isActiveStatus },
                onBackClick = { showDetail = false }
            )
        }
    }
}

@Composable
fun HomeScreen(
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onShowInfoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        // Custom Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF1E88E5),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.getPaddingValuesOrDp(), horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "My Student Profile",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Home Screen : Student Profile App",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Main Body Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Image with specialized circular decoration
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD))
                            .border(3.dp, Color(0xFFFF4081), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFBBDEFB))
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Pongsatorn Yingsabay",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Computer Science",
                        fontSize = 16.sp,
                        color = Color(0xFF7F8C8D),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Student ID Row with custom styling
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, Color(0xFF4CAF50), RoundedCornerShape(10.dp))
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(10.dp))
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Student ID : ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "67050353",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // SHOW MY INFO Button
                    Button(
                        onClick = onShowInfoClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info Icon",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SHOW MY INFO",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Add Favorite Button
                    OutlinedButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp, 
                            if (isFavorite) Color(0xFFE91E63) else Color(0xFF9C27B0)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isFavorite) Color(0xFFFCE4EC) else Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Favorite Icon",
                                tint = if (isFavorite) Color(0xFFE91E63) else Color(0xFF9C27B0)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isFavorite) "Remove from Favorite" else "Add Favorite",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFavorite) Color(0xFFE91E63) else Color(0xFF9C27B0)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailScreen(
    isActiveStatus: Boolean,
    onStatusToggle: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
    ) {
        // Custom Top Bar with Back Navigation and Actions
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF1E88E5),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "My Student Profile",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = Color.White
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Profile Row (Image left, info right)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD))
                            .border(2.dp, Color(0xFF1E88E5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Detail Profile Picture",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text ="POngsatorn Yingsabay" ,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Computer Science",
                            fontSize = 14.sp,
                            color = Color(0xFF7F8C8D)
                        )
                        Text(
                            text = "ID: 67050353",
                            fontSize = 14.sp,
                            color = Color(0xFF7F8C8D)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        // Clickable Status Badge (Fulfilling the state requirement)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isActiveStatus) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                                .clickable { onStatusToggle() }
                                .padding(vertical = 4.dp, horizontal = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isActiveStatus) Color(0xFF4CAF50) else Color(0xFFF44336))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isActiveStatus) "Active" else "Inactive",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActiveStatus) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Block (GPA, Year, Academic Year)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCC80)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "2.80", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        Text(text = "GPA", fontSize = 12.sp, color = Color(0xFF7F8C8D))
                    }
                    Box(modifier = Modifier.width(1.dp).height(35.dp).background(Color(0xFFE0E0E0)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "3", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        Text(text = "Year", fontSize = 12.sp, color = Color(0xFF7F8C8D))
                    }
                    Box(modifier = Modifier.width(1.dp).height(35.dp).background(Color(0xFFE0E0E0)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "2567", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        Text(text = "Academic Year", fontSize = 12.sp, color = Color(0xFF7F8C8D))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB2DFDB)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailInfoRow(icon = Icons.Default.Person, label = "Faculty", value = "Faculty of Science")
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF0F0F0)).padding(vertical = 4.dp))
                    DetailInfoRow(icon = Icons.Default.Email, label = "Email", value = "67050353@kmitl.ac.th")
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF0F0F0)).padding(vertical = 4.dp))
                    DetailInfoRow(icon = Icons.Default.Phone, label = "Phone", value = "0946629375")
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF0F0F0)).padding(vertical = 4.dp))
                    DetailInfoRow(icon = Icons.Default.Home, label = "Address", value = "123 University Road, Bangkok")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About Me Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About Me",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E88E5)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "สวัสดี ฉันเป็นนักศึกษาภาควิชาวิทยาการคอมพิวเตอร์ มีความสนใจเกี่ยวกับการพัฒนาโมบายแอปพลิเคชัน เวลาว่างชอบเล่นเกมส์ เล่นกีฬา แต่ไม่ชอบอ่านหนังสือ",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFF34495E),
                        textAlign = TextAlign.Justify
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Edit Profile Button
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Edit Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF00897B),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color(0xFF95A5A6))
            Text(text = value, fontSize = 14.sp, color = Color(0xFF2C3E50), fontWeight = FontWeight.Medium)
        }
    }
}

// Utility extension to avoid compiler issues if dp conversions are needed in specific areas
fun Int.getPaddingValuesOrDp() = this.dp

@Preview(showBackground = true)
@Composable
fun StudentProfileAppPreview() {
    MobileApplicationTheme {
        StudentProfileApp()
    }
}
