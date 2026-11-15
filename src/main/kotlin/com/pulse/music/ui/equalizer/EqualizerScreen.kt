package com.pulse.music.ui.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulse.music.ui.theme.*

@Composable
fun EqualizerScreen(
    isEnabled: Boolean,
    presets: List<String>,
    currentPreset: Int,
    bassBoostStrength: Int,
    bandFrequencies: Map<Int, Int>,
    bandLevels: Map<Int, Int>,
    onToggleEnabled: (Boolean) -> Unit,
    onSelectPreset: (Int) -> Unit,
    onBassBoostChange: (Int) -> Unit,
    onBandLevelChange: (Int, Int) -> Unit,
    onReset: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseBlack)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 120.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(PulseCardDark)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PulseTextPrimary)
            }

            Text(
                text = "Equalizer",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = PulseTextPrimary
            )

            IconButton(
                onClick = onReset,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(PulseCardDark)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = PulseTextPrimary)
            }
        }

        // Enable / Disable Master Toggle
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(18.dp)),
            color = PulseCardDark
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Equalizer & Effects",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PulseTextPrimary
                    )
                    Text(
                        text = if (isEnabled) "Active" else "Disabled",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isEnabled) PulseRedAccent else PulseTextMuted
                    )
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggleEnabled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PulseRedAccent,
                        uncheckedThumbColor = PulseTextMuted,
                        uncheckedTrackColor = PulseSecondaryDark
                    )
                )
            }
        }

        // Presets Horizontal Row
        if (presets.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sound Presets",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = PulseTextSecondary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(presets) { index, presetName ->
                    val isSelected = currentPreset == index
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectPreset(index) },
                        label = {
                            Text(
                                text = presetName,
                                color = if (isSelected) Color.White else PulseTextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PulseRedAccent,
                            containerColor = PulseCardDark
                        ),
                        border = null,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // Bass Boost Card
        Spacer(modifier = Modifier.height(20.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(18.dp)),
            color = PulseCardDark
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Bass Boost",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PulseTextPrimary
                    )
                    Text(
                        text = "${bassBoostStrength / 10}%",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PulseRedAccent
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = bassBoostStrength.toFloat(),
                    onValueChange = { onBassBoostChange(it.toInt()) },
                    valueRange = 0f..1000f,
                    enabled = isEnabled,
                    colors = SliderDefaults.colors(
                        thumbColor = PulseRedAccent,
                        activeTrackColor = PulseProgressBar,
                        inactiveTrackColor = PulseBorderDark
                    )
                )
            }
        }

        // Frequency Bands (5-band EQ)
        if (bandFrequencies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(18.dp)),
                color = PulseCardDark
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Frequency Bands",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PulseTextPrimary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    bandFrequencies.keys.sorted().forEach { band ->
                        val freqHz = bandFrequencies[band] ?: 1000
                        val freqLabel = if (freqHz >= 1000000) {
                            "${freqHz / 1000000} kHz"
                        } else if (freqHz >= 1000) {
                            "${freqHz / 1000} Hz"
                        } else {
                            "$freqHz Hz"
                        }
                        val currentLevel = bandLevels[band] ?: 0

                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = freqLabel, style = MaterialTheme.typography.bodyMedium, color = PulseTextSecondary)
                                Text(
                                    text = "${currentLevel / 100} dB",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (currentLevel != 0) PulseRedAccent else PulseTextMuted
                                )
                            }
                            Slider(
                                value = currentLevel.toFloat(),
                                onValueChange = { onBandLevelChange(band, it.toInt()) },
                                valueRange = -1500f..1500f,
                                enabled = isEnabled,
                                colors = SliderDefaults.colors(
                                    thumbColor = PulseRedAccent,
                                    activeTrackColor = PulseProgressBar,
                                    inactiveTrackColor = PulseBorderDark
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
