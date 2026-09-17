package com.example.event_drivenmobileapplication170969

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(state: StudentState, onRegister: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ImagePickerDialog(
            onDismiss = { showDialog = false },
            onImageSelected = { resId ->
                state.selectedDrawableResId = resId
                state.imageUriString = null // เคลียร์ URI เพื่อให้แสดงผลจาก drawable แทน
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ---------- ImageView + ImageButton (Overlapping style) ----------
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.clickable {
                    showDialog = true
                }
            ) {
                // โหลดรูปภาพจากที่เลือกในดรอว์เอเบิลหรือ URI
                UriImage(
                    uriString = state.imageUriString,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    val resId = state.selectedDrawableResId
                    if (resId != null) {
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = "Student Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Student Photo",
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .padding(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier
                        .size(28.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Change photo",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "ยินดีต้อนรับ!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text("กรุณากรอกข้อมูลของคุณ")
            }
        }

        // ---------- EditText -> TextField ----------
        OutlinedTextField(
            value = state.name,
            onValueChange = { state.name = it },   // Keyboard -> onValueChange -> State -> UI
            label = { Text("Name") },
            placeholder = { Text("Enter your name") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.studentId,
            onValueChange = { input -> state.studentId = input.filter { it.isDigit() }.take(10) },
            label = { Text("Student ID") },
            placeholder = { Text("e.g. 6612345678") },
            leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // ---------- RadioButton + RadioGroup (Column + state เดียว) ----------
        SectionTitle("Degree (ระดับการศึกษา)")
        Row(Modifier.selectableGroup()) {
            listOf("Bachelor", "Master").forEach { degree ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = state.degree == degree,
                            onClick = { state.degree = degree },
                            role = Role.RadioButton
                        )
                        .padding(end = 24.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = state.degree == degree, onClick = null)
                    Spacer(Modifier.width(4.dp))
                    Text(degree)
                }
            }
        }

        // ---------- CheckBox ----------
        SectionTitle("Skills (ทักษะที่สนใจ)")
        Row {
            SkillCheckbox("Python", state.usePython) { state.usePython = it }
            SkillCheckbox("Kotlin", state.useKotlin) { state.useKotlin = it }
            SkillCheckbox("Java", state.useJava) { state.useJava = it }
        }

        // ---------- Switch ----------
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Notifications, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("รับการแจ้งเตือน", Modifier.weight(1f))
            Text(if (state.notificationEnabled) "ON" else "OFF")
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = state.notificationEnabled,
                onCheckedChange = { state.notificationEnabled = it }
            )
        }

        // ---------- SeekBar -> Slider (ผู้ใช้ควบคุม) ----------
        SectionTitle("Interest Level: ${state.interestLevel.toInt()}%")
        Slider(
            value = state.interestLevel,
            onValueChange = { state.interestLevel = it },   // Continuous Event
            valueRange = 0f..100f
        )

        // ---------- ProgressBar -> LinearProgressIndicator (App ควบคุม) ----------
        val animatedProgress by animateFloatAsState(
            targetValue = state.profileProgress,
            label = "profileProgress"
        )
        SectionTitle("Profile Completion: ${(state.profileProgress * 100).toInt()}%")
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth().height(8.dp)
        )

        Spacer(Modifier.height(8.dp))

        // ---------- Button + Event -> Navigate ----------
        Button(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("REGISTER")
        }
    }
}

@Composable
private fun SkillCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label)
    }
}
