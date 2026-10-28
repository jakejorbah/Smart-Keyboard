package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardPreferences
import com.example.data.model.KeyboardSettings
import com.example.data.model.KeyboardTheme
import com.example.data.model.OneHandedMode
import com.example.ui.components.KeyboardLayout
import com.example.ui.theme.ElectricBlue
import kotlin.math.roundToInt

@Composable
fun CustomizeScreen(
    preferences: KeyboardPreferences,
    settings: KeyboardSettings
) {
    var showLivePreview by remember { mutableStateOf(true) }
    var selectedWallpaperIdx by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Scrollable Options Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Customize",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Fine-tune glass aesthetics and typing behavior",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { preferences.resetToDefaults() }
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset to Defaults",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // SECTION 1: GLASS TRANSPARENCY & LOOK
            CustomizationSection(title = "Glass Appearance & Transparency") {
                SliderItem(
                    label = "Keyboard Background Opacity",
                    value = settings.keyboardOpacity,
                    valueText = "${(settings.keyboardOpacity * 100).roundToInt()}%",
                    range = 0.10f..0.98f,
                    onValueChange = { preferences.updateSettings(settings.copy(keyboardOpacity = it)) }
                )

                SliderItem(
                    label = "Keycap Transparency",
                    value = settings.keycapOpacity,
                    valueText = "${(settings.keycapOpacity * 100).roundToInt()}%",
                    range = 0.10f..0.95f,
                    onValueChange = { preferences.updateSettings(settings.copy(keycapOpacity = it)) }
                )

                SliderItem(
                    label = "Key Corner Radius",
                    value = settings.cornerRadius.toFloat(),
                    valueText = "${settings.cornerRadius} dp",
                    range = 4f..22f,
                    steps = 17,
                    onValueChange = { preferences.updateSettings(settings.copy(cornerRadius = it.roundToInt())) }
                )

                SliderItem(
                    label = "Key Spacing",
                    value = settings.keySpacing.toFloat(),
                    valueText = "${settings.keySpacing} dp",
                    range = 2f..8f,
                    steps = 5,
                    onValueChange = { preferences.updateSettings(settings.copy(keySpacing = it.roundToInt())) }
                )

                SliderItem(
                    label = "Frosted Blur Intensity",
                    value = settings.blurIntensity.toFloat(),
                    valueText = "${settings.blurIntensity}%",
                    range = 0f..100f,
                    steps = 20,
                    onValueChange = { preferences.updateSettings(settings.copy(blurIntensity = it.roundToInt())) }
                )

                // Background Tint Palette
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Background Tint Palette",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val activeTint = com.example.data.model.BackgroundTintPresets.find { it.colorValue == settings.backgroundTintColor }
                        Text(
                            text = activeTint?.name ?: "Custom",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElectricBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(com.example.data.model.BackgroundTintPresets.size) { idx ->
                            val tint = com.example.data.model.BackgroundTintPresets[idx]
                            val isSelected = settings.backgroundTintColor == tint.colorValue
                            val chipBg = if (tint.colorValue == 0L) Color(0x33CBD5E1) else Color(tint.colorValue)

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(chipBg)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) ElectricBlue else Color.White.copy(alpha = 0.35f),
                                        shape = CircleShape
                                    )
                                    .clickable { preferences.updateSettings(settings.copy(backgroundTintColor = tint.colorValue)) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (tint.colorValue == 0L) MaterialTheme.colorScheme.onSurface else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (settings.backgroundTintColor != 0L) {
                    SliderItem(
                        label = "Tint Opacity",
                        value = settings.tintOpacity,
                        valueText = "${(settings.tintOpacity * 100).roundToInt()}%",
                        range = 0.05f..0.95f,
                        onValueChange = { preferences.updateSettings(settings.copy(tintOpacity = it)) }
                    )
                }

                SliderItem(
                    label = "Border Width",
                    value = settings.borderWidth,
                    valueText = "${String.format("%.1f", settings.borderWidth)} dp",
                    range = 0.5f..2.5f,
                    onValueChange = { preferences.updateSettings(settings.copy(borderWidth = it)) }
                )

                SliderItem(
                    label = "Border Glow Opacity",
                    value = settings.borderOpacity,
                    valueText = "${(settings.borderOpacity * 100).roundToInt()}%",
                    range = 0.05f..1.0f,
                    onValueChange = { preferences.updateSettings(settings.copy(borderOpacity = it)) }
                )
            }

            // SECTION 2: KEYBOARD LAYOUT & ERGONOMICS
            CustomizationSection(title = "Layout & Ergonomics") {
                SliderItem(
                    label = "Keyboard Height Scale",
                    value = settings.keyboardHeightFactor,
                    valueText = "${(settings.keyboardHeightFactor * 100).roundToInt()}%",
                    range = 0.85f..1.25f,
                    onValueChange = { preferences.updateSettings(settings.copy(keyboardHeightFactor = it)) }
                )

                ToggleItem(
                    label = "Dedicated Number Row",
                    description = "Show full 0-9 row above standard QWERTY",
                    checked = settings.numberRowEnabled,
                    onCheckedChange = { preferences.updateSettings(settings.copy(numberRowEnabled = it)) }
                )

                // One handed mode segmented selector
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Text(
                        text = "One-Handed Typing Mode",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            OneHandedMode.NONE to "Standard",
                            OneHandedMode.LEFT to "Left Hand",
                            OneHandedMode.RIGHT to "Right Hand"
                        ).forEach { (mode, title) ->
                            val isSelected = settings.oneHandedMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ElectricBlue else Color.Transparent)
                                    .clickable { preferences.updateSettings(settings.copy(oneHandedMode = mode)) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 3: SMART TYPING & FEEDBACK
            CustomizationSection(title = "Smart Typing & Haptics") {
                ToggleItem(
                    label = "Smart Word Suggestions",
                    description = "Predict next words and text expansions above the keyboard",
                    checked = settings.suggestionsEnabled,
                    onCheckedChange = { preferences.updateSettings(settings.copy(suggestionsEnabled = it)) }
                )

                ToggleItem(
                    label = "Autocorrect",
                    description = "Automatically fix typos with top match on spacebar",
                    checked = settings.autoCorrectEnabled,
                    onCheckedChange = { preferences.updateSettings(settings.copy(autoCorrectEnabled = it)) }
                )

                ToggleItem(
                    label = "Auto-Capitalization",
                    description = "Capitalize first word of every sentence",
                    checked = settings.autoCapitalization,
                    onCheckedChange = { preferences.updateSettings(settings.copy(autoCapitalization = it)) }
                )

                ToggleItem(
                    label = "Double-Space Period",
                    description = "Double tapping spacebar inserts a '.' followed by a space",
                    checked = settings.doubleSpacePeriod,
                    onCheckedChange = { preferences.updateSettings(settings.copy(doubleSpacePeriod = it)) }
                )

                ToggleItem(
                    label = "Haptic Vibration Feedback",
                    description = "Crisp subtle tactile feedback on keypress",
                    checked = settings.hapticFeedback,
                    onCheckedChange = { preferences.updateSettings(settings.copy(hapticFeedback = it)) }
                )

                ToggleItem(
                    label = "Spring Keypress Animation",
                    description = "Subtle spring-like scaling feedback on each keypress",
                    checked = settings.keypressAnimationEnabled,
                    onCheckedChange = { preferences.updateSettings(settings.copy(keypressAnimationEnabled = it)) }
                )

                ToggleItem(
                    label = "Respect Reduced-Motion",
                    description = "Automatically disable key scaling if system accessibility animations are off",
                    checked = settings.respectReducedMotion,
                    onCheckedChange = { preferences.updateSettings(settings.copy(respectReducedMotion = it)) }
                )

                ToggleItem(
                    label = "Key Click Sound",
                    description = "Play subtle click sound when keys are tapped",
                    checked = settings.soundFeedback,
                    onCheckedChange = { preferences.updateSettings(settings.copy(soundFeedback = it)) }
                )
            }
        }

        // Live Docked Preview Card at Bottom
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header of docked preview
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLivePreview = !showLivePreview }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Interactive Preview",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElectricBlue
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Mini wallpaper selector
                        WallpaperPresets.forEachIndexed { i, p ->
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(p.brush)
                                    .border(
                                        width = if (selectedWallpaperIdx == i) 2.dp else 0.dp,
                                        color = if (selectedWallpaperIdx == i) ElectricBlue else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedWallpaperIdx = i }
                            )
                        }

                        Icon(
                            imageVector = if (showLivePreview) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = "Toggle Preview",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                AnimatedVisibility(visible = showLivePreview) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WallpaperPresets[selectedWallpaperIdx].brush)
                            .padding(4.dp)
                    ) {
                        KeyboardLayout(
                            settings = settings,
                            currentTextBeforeCursor = "Custom ",
                            onTextInput = {},
                            onDelete = {},
                            onEnter = {},
                            onOpenSettings = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomizationSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun SliderItem(
    label: String,
    value: Float,
    valueText: String,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = valueText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ElectricBlue
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = ElectricBlue,
                activeTrackColor = ElectricBlue
            )
        )
    }
}

@Composable
private fun ToggleItem(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
        )
    }
}
