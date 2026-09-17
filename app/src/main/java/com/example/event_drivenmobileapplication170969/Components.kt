package com.example.event_drivenmobileapplication170969

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** หัวข้อ section เล็ก ๆ (TextView) */
@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

/** ToggleButton -> IconToggleButton : กดครั้งแรก ON กดอีกครั้ง OFF */
@Composable
fun FavoriteToggle(isFavorite: Boolean, onToggle: (Boolean) -> Unit) {
    IconToggleButton(checked = isFavorite, onCheckedChange = onToggle) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) Color(0xFFE53935) else LocalContentColor.current
        )
    }
}

/** Composable สำหรับแสดงรูปภาพจาก URI string ด้วยการจัดการคิวขนาน */
@Composable
fun UriImage(uriString: String?, modifier: Modifier, fallback: @Composable () -> Unit) {
    val context = LocalContext.current
    val imageBitmap = remember(uriString) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uriString) {
        if (!uriString.isNullOrBlank()) {
            try {
                val uri = Uri.parse(uriString)
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageBitmap.value = bitmap?.asImageBitmap()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                imageBitmap.value = null
            }
        } else {
            imageBitmap.value = null
        }
    }

    val bitmap = imageBitmap.value
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = "Selected Photo",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        fallback()
    }
}

/** Dialog สำหรับเลือกรูปภาพที่มีอยู่ใน drawable ทั้งหมด */
@Composable
fun ImagePickerDialog(
    onDismiss: () -> Unit,
    onImageSelected: (Int) -> Unit
) {
    val context = LocalContext.current
    
    // ดึงรายชื่อทรัพยากรทั้งหมดใน drawable โดยอัตโนมัติ ไม่ต้องมาคอยกดเพิ่มในโค้ดทีละรูป
    val drawablesList = remember {
        val list = mutableListOf<Int>()
        try {
            val drawableClass = Class.forName("${context.packageName}.R\$drawable")
            val fields = drawableClass.fields
            for (field in fields) {
                val resId = field.getInt(null)
                val name = field.name
                // กรองเอาเฉพาะรูปภาพที่คุณเพิ่มเข้ามาเอง (เช่น student, images หรือรูปอื่นๆ ที่ไม่ใช่ไอคอนระบบ)
                if (!name.startsWith("abc_") && 
                    !name.startsWith("androidx_") && 
                    !name.startsWith("notification_") && 
                    !name.startsWith("btn_") &&
                    !name.startsWith("ic_launcher")) {
                    list.add(resId)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            list.add(R.drawable.student)
            list.add(R.drawable.images)
        }
        list
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("เลือกรูปภาพโปรไฟล์") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = androidx.compose.ui.Modifier.size(280.dp)
            ) {
                items(drawablesList) { resId ->
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = androidx.compose.ui.Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .clickable {
                                onImageSelected(resId)
                                onDismiss()
                            }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("ปิด")
            }
        }
    )
}
