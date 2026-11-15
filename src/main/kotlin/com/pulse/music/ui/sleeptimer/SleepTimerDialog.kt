package com.pulse.music.ui.sleeptimer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulse.music.ui.components.formatDuration
import com.pulse.music.ui.theme.*

@Composable
fun SleepTimerDialog(
    remainingMs: Long?,
    onStartTimer: (Int) -> Unit,
    onCancelTimer: () -> Unit,
    onDismiss: () -> Unit
) {
    var customMinutesText by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PulseCardDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = PulseRedAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sleep Timer",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = PulseTextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (remainingMs != null && remainingMs > 0) {
                    // Active Timer running
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PulseSecondaryDark,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Music stops in:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PulseTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatDuration(remainingMs),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                color = PulseRedAccent
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onCancelTimer()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PulseBorderDark),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.TimerOff, contentDescription = null, tint = PulseTextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cancel Timer", color = PulseTextPrimary)
                            }
                        }
                    }
                } else {
                    // Preset buttons
                    val presets = listOf(5, 10, 15, 30, 45, 60)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        presets.chunked(3).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { minutes ->
                                    Button(
                                        onClick = {
                                            onStartTimer(minutes)
                                            onDismiss()
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = PulseSecondaryDark),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("$minutes m", color = PulseTextPrimary)
                                    }
                                }
                            }
                        }
                    }

                    if (showCustomInput) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customMinutesText,
                                onValueChange = { customMinutesText = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Minutes") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = PulseTextPrimary,
                                    unfocusedTextColor = PulseTextPrimary,
                                    focusedBorderColor = PulseRedAccent,
                                    unfocusedBorderColor = PulseBorderDark
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val mins = customMinutesText.toIntOrNull()
                                    if (mins != null && mins > 0) {
                                        onStartTimer(mins)
                                        onDismiss()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PulseRedAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Set", color = Color.White)
                            }
                        }
                    } else {
                        TextButton(
                            onClick = { showCustomInput = true },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Custom duration...", color = PulseRedAccent)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = PulseTextMuted)
            }
        }
    )
}
