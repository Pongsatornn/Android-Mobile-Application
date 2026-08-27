package com.example.mobile_midterm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_midterm.ui.theme.MObilemidtermTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MObilemidtermTheme {
                BookingScreen()
            }
        }
    }
}

@Composable
fun BookingScreen() {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF5)) // Even lighter cream, almost white-cream
            .padding(16.dp), // 16dp margin as per arrow
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // จัดให้อยู่กึ่งกลางหน้าจอเพื่อไม่ให้ด้านล่างโล่ง
    ) {
        Text(
            text = stringResource(id = R.string.title_book_a_room),
            fontSize = 36.sp,
            color = Color(0xFFC62828), // Deep Red
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Font size 36sp",
            fontSize = 14.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(30.dp))

        BookingField(
            label = stringResource(id = R.string.label_name),
            value = name,
            onValueChange = { name = it },
            hint = stringResource(id = R.string.hint_name)
        )

        BookingField(
            label = stringResource(id = R.string.label_date),
            value = date,
            onValueChange = { date = it },
            hint = stringResource(id = R.string.hint_date)
        )

        BookingField(
            label = stringResource(id = R.string.label_room),
            value = room,
            onValueChange = { room = it },
            hint = stringResource(id = R.string.hint_room)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                val bookingInfo = BookingInfo(name, date, room)
                val intent = Intent(context, ConfirmationActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("booking_info", bookingInfo)
                intent.putExtra("booking_bundle", bundle)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3FB18C)), // Teal Green from image
            modifier = Modifier
                .width(180.dp)
                .height(60.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(text = stringResource(id = R.string.btn_book), fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BookingField(label: String, value: String, onValueChange: (String) -> Unit, hint: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Gap between blocks
            .padding(horizontal = 8.dp) // Inner padding
    ) {
        Text(
            text = label,
            color = Color(0xFFE91E63), // Pink from image
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp) // 8dp padding as per arrow
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = hint) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp), // Padding end 8dp as per arrow
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black
            )
        )
    }
}
