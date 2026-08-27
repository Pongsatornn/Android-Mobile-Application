package com.example.mobile_midterm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_midterm.ui.theme.MObilemidtermTheme

class ConfirmationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val bookingInfo = intent.getBundleExtra("booking_bundle")?.getParcelable<BookingInfo>("booking_info")

        setContent {
            MObilemidtermTheme {
                ConfirmationScreen(bookingInfo)
            }
        }
    }
}

@Composable
fun ConfirmationScreen(bookingInfo: BookingInfo?) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Warm/Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically) // จัดระยะห่างระหว่างบรรทัดให้เท่ากันที่ 16dp
        ) {
            Text(
                text = "Booking",
                fontSize = 90.sp,
                color = Color(0xFFE91E63), // Pink
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Confirmed!",
                fontSize = 75.sp, // ขนาดใหญ่ที่สุดที่ไม่ล้นหน้าจอ
                color = Color(0xFFE91E63), // Pink
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = bookingInfo?.name ?: "",
                fontSize = 40.sp,
                color = Color(0xFFFFD700), // Gold
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )

            Text(
                text = bookingInfo?.room ?: "",
                fontSize = 40.sp,
                color = Color(0xFFFFD700), // Gold
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )

            Text(
                text = "From ${bookingInfo?.name ?: ""}",
                fontSize = 40.sp,
                color = Color(0xFF42A5F5), // Bright Blue
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
