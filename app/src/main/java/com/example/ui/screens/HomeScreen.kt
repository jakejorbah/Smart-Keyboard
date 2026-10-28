package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardPreferences
import com.example.data.model.KeyboardSettings
import com.example.data.model.KeyboardTheme
import com.example.ui.components.GlassCard
import com.example.ui.components.KeyboardLayout
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GlassSuccess
import com.example.util.KeyboardManager

data class WallpaperPreset(
    val name: String,
    val brush: Brush
)

val WallpaperPresets = listOf(
    WallpaperPreset("iOS Light", Brush.verticalGradient(listOf(Color(0xFFE2E8F0), Color(0xFFF8FAFC), Color(0xFFCBD5E1)))),
    WallpaperPreset("macOS Dark", Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF020617)))),
    WallpaperPreset("Sunset", Brush.linearGradient(listOf(Color(0xFFF43F5E), Color(0xFFFB923C), Color(0xFFFBBF24)))),
    WallpaperPreset("Aurora", Brush.linearGradient(listOf(Color(0xFF064E3B), Color(0xFF0E7490), Color(0xFF4C1D95)))),
    WallpaperPreset("Deep Ocean", Brush.verticalGradient(listOf(Color(0xFF082F49), Color(0xFF0C4A6E), Color(0xFF0284C7))))
)

@Composable
fun HomeScreen(
    preferences: KeyboardPreferences,
    settings: KeyboardSettings,
    onNavigateToCustomize: () -> Unit,
    onNavigateToThemes: () -> Unit,
    onNavigateToTestLab: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    var showSetupWizard by remember { mutableStateOf(false) }
    var selectedWallpaperIndex by remember { mutableIntStateOf(1) } // macOS dark by default
    val currentTheme = KeyboardTheme.getById(settings.themeId)

    val isEnabled = remember { KeyboardManager.isKeyboardEnabled(context) }
    val isSelected = remember { KeyboardManager.isKeyboardSelected(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Crystal Keyboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Your keyboard. Your style.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick Setup button
            OutlinedButton(
                onClick = { showSetupWizard = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Keyboard,
                    contentDescription = null,
                    tint = if (isSelected) GlassSuccess else ElectricBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isSelected) "Active" else "Setup IME")
            }
        }

        // Live Preview Container with dynamic wallpaper
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header of preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(ElectricCyan)
                        )
                        Text(
                            text = "LIVE TRANSPARENT PREVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan,
                            letterSpacing = 1.sp
                        )
                    }

                    // Wallpaper Switcher Pills
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        WallpaperPresets.forEachIndexed { idx, preset ->
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(preset.brush)
                                    .border(
                                        width = if (selectedWallpaperIndex == idx) 2.dp else 0.dp,
                                        color = if (selectedWallpaperIndex == idx) ElectricBlue else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedWallpaperIndex = idx }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Wallpaper background area holding the keyboard
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(WallpaperPresets[selectedWallpaperIndex].brush)
                        .padding(6.dp)
                ) {
                    KeyboardLayout(
                        settings = settings,
                        currentTextBeforeCursor = "Crystal ",
                        onTextInput = {},
                        onDelete = {},
                        onEnter = {},
                        onOpenSettings = onNavigateToCustomize,
                        onCycleTheme = {
                            val all = KeyboardTheme.AllThemes
                            val idx = all.indexOfFirst { it.id == settings.themeId }
                            val next = all[(if (idx >= 0) idx + 1 else 0) % all.size]
                            preferences.selectTheme(next.id)
                        }
                    )
                }
            }
        }

        // Quick Stats Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Theme",
                value = currentTheme.name,
                subtitle = if (currentTheme.isDark) "Dark Glass" else "Light Glass",
                icon = Icons.Default.Palette
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Transparency",
                value = "${(settings.keyboardOpacity * 100).toInt()}%",
                subtitle = "Keycap ${(settings.keycapOpacity * 100).toInt()}%",
                icon = Icons.Default.Layers
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Scale",
                value = "${(settings.keyboardHeightFactor * 100).toInt()}%",
                subtitle = settings.activeLanguage,
                icon = Icons.Default.Tune
            )
        }

        // Quick Theme Selector Strip
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Themes",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "View All →",
                    fontSize = 13.sp,
                    color = ElectricBlue,
                    modifier = Modifier.clickable { onNavigateToThemes() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(KeyboardTheme.AllThemes.take(6)) { theme ->
                    val isSelectedTheme = theme.id == settings.themeId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelectedTheme) ElectricBlue.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                            .border(
                                width = if (isSelectedTheme) 1.5.dp else 1.dp,
                                color = if (isSelectedTheme) ElectricBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { preferences.selectTheme(theme.id) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(theme.accentColor)
                            )
                            Text(
                                text = theme.name,
                                fontSize = 13.sp,
                                fontWeight = if (isSelectedTheme) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelectedTheme) ElectricBlue else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Main Action Cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionTile(
                title = "Customize Appearance & Transparency",
                description = "Adjust opacity, frosted blur, keycap radii, and key spacing",
                icon = Icons.Default.Tune,
                onClick = onNavigateToCustomize
            )
            ActionTile(
                title = "Keyboard Test Lab",
                description = "Live interactive typing ground with metrics and background switcher",
                icon = Icons.Default.Keyboard,
                onClick = onNavigateToTestLab
            )
            ActionTile(
                title = "Smart Message Planner & Templates",
                description = "Reusable message templates, scheduled reminders, and multi-platform quick send",
                icon = Icons.Default.EditCalendar,
                onClick = onNavigateToPlanner
            )
            ActionTile(
                title = "Settings & Privacy",
                description = "System activation guide, 100% on-device privacy guarantee, and dictionary",
                icon = Icons.Default.Settings,
                onClick = onNavigateToSettings
            )
        }
    }

    if (showSetupWizard) {
        SetupWizardDialog(
            onDismiss = { showSetupWizard = false },
            onOpenTestLab = onNavigateToTestLab
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElectricBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
