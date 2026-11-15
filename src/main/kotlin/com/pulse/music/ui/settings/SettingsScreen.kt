package com.pulse.music.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulse.music.ui.theme.*

@Composable
fun SettingsScreen(
    songCount: Int,
    albumCount: Int,
    artistCount: Int,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    onRescanLibrary: () -> Unit,
    onEqualizerClick: () -> Unit,
    onSleepTimerClick: () -> Unit
) {
    var gaplessPlayback by remember { mutableStateOf(true) }
    var resumeOnOpen by remember { mutableStateOf(true) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseBlack)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 120.dp)
    ) {
        // Settings Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = PulseTextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // Appearance Section
        SettingsSectionHeader(title = "Appearance")
        SettingsRow(
            title = "AMOLED Dark Mode",
            subtitle = "Deep black interface for AMOLED displays",
            icon = Icons.Default.DarkMode,
            trailing = {
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onToggleDarkTheme,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PulseRedAccent,
                        uncheckedThumbColor = PulseTextMuted,
                        uncheckedTrackColor = PulseSecondaryDark
                    )
                )
            }
        )

        SettingsRow(
            title = "Accent Color",
            subtitle = "Pulse Red (#D71920) - Nothing OS Inspired",
            icon = Icons.Default.Palette,
            trailing = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PulseRedAccent)
                )
            }
        )

        // Playback Section
        Spacer(modifier = Modifier.height(16.dp))
        SettingsSectionHeader(title = "Playback")

        SettingsRow(
            title = "Equalizer & Effects",
            subtitle = "Tune bass boost and 5-band sound presets",
            icon = Icons.Default.GraphicEq,
            onClick = onEqualizerClick
        )

        SettingsRow(
            title = "Sleep Timer",
            subtitle = "Automatic fade-out and stop playback",
            icon = Icons.Default.Timer,
            onClick = onSleepTimerClick
        )

        SettingsRow(
            title = "Gapless Playback",
            subtitle = "Continuous audio streaming with no gaps between tracks",
            icon = Icons.Default.FastForward,
            trailing = {
                Switch(
                    checked = gaplessPlayback,
                    onCheckedChange = { gaplessPlayback = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PulseRedAccent
                    )
                )
            }
        )

        SettingsRow(
            title = "Resume Playback on Launch",
            subtitle = "Restore last played track and queue position",
            icon = Icons.Default.PlayCircle,
            trailing = {
                Switch(
                    checked = resumeOnOpen,
                    onCheckedChange = { resumeOnOpen = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PulseRedAccent
                    )
                )
            }
        )

        // Library Section
        Spacer(modifier = Modifier.height(16.dp))
        SettingsSectionHeader(title = "Library & Storage")

        SettingsRow(
            title = "Rescan Music Library",
            subtitle = "$songCount tracks • $albumCount albums • $artistCount artists",
            icon = Icons.Default.Refresh,
            onClick = onRescanLibrary
        )

        // About Section
        Spacer(modifier = Modifier.height(16.dp))
        SettingsSectionHeader(title = "About Pulse")

        SettingsRow(
            title = "Pulse Music Player",
            subtitle = "Version 1.0 (Build 2026.11) • Nothing Phone 2a Native",
            icon = Icons.Default.Info,
            onClick = { showAboutDialog = true }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = PulseCardDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("PULSE", color = PulseTextPrimary, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PulseRedAccent))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your Music. Your Rhythm.", style = MaterialTheme.typography.titleMedium, color = PulseRedAccent)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Pulse is a premium-quality native Android music player crafted with Jetpack Compose, Android Media3 ExoPlayer, and Room Database.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PulseTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Target Device: Nothing Phone 2a", style = MaterialTheme.typography.bodyMedium, color = PulseTextPrimary)
                    Text("Design Language: Minimalist AMOLED Deep Black", style = MaterialTheme.typography.bodyMedium, color = PulseTextPrimary)
                    Text("Engine: Android Media3 ExoPlayer + MediaSessionService", style = MaterialTheme.typography.bodyMedium, color = PulseTextPrimary)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PulseRedAccent)
                ) {
                    Text("Awesome", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.sp),
        color = PulseRedAccent,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PulseCardDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PulseTextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = PulseTextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = PulseTextSecondary
            )
        }

        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = PulseTextMuted
            )
        }
    }
}
